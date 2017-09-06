package com.egangotri.monierWilliams.dao;

import java.util.ArrayList;

import com.egangotri.monierWilliams.util.DictionaryConstants;
import com.egangotri.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.egangotri.monierWilliams.dao.vo.WordMaster;

public class DictionaryDAOSupport extends HibernateDaoSupport
{

    public boolean save(WordMaster wm)
    {
        boolean success = false;
        Log.info("In save: wm == " + wm.getWord());
        try
        {
            getHibernateTemplate().saveOrUpdate(wm);
            Log.info("Added Word with Id = " + wm.getId());

            success = true;
        }
        catch (Exception e)
        {
            Log.info("Exception In Saving Word");
            e.printStackTrace();
        }
        return success;

    }

    public int findHighestId(){
        
        int highestId = 0;
        Criteria crit = getSession().createCriteria(WordMaster.class);
        crit.setProjection(Projections.max("id"));
        ArrayList<Object> list = (ArrayList<Object> ) crit.list();
        highestId =  (Integer) list.get(0);
        Log.info("highest id " +highestId);
        return highestId;
    }
    public WordMaster findWord(int id)
    {
        Criteria crit = getSession().createCriteria(WordMaster.class);
        crit.add(Restrictions.eq("id", id));
        Log.info("Finding word with ID: " + id);
        try{
        ArrayList<WordMaster> list = (ArrayList<WordMaster>) crit.list();
      
        Log.info("after  afFinding word with ID: " + id);
        if (list != null && list.size() > 0)
        {
            WordMaster wm = list.get(0);
            Log.info("Word is: " + wm.getWord());
            Log.info("Word in DVN is: " + wm.getWordinDVN());
            Log.info("Word in ITRANS is: " + wm.getWordinItrans());
            Log.info("Word in SLP is: " + wm.getWordInSLP());
            Log.info("meaning is : " + wm.getMeaning());

            return list.get(0);
        }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("No word with ID: " + id);
        return null;
    }
    public ArrayList<WordMaster> findWord(int beginRowId, int endRowId)
    {
        Criteria crit = getSession().createCriteria(WordMaster.class);
        crit.add(Restrictions.ge("id", beginRowId));
        crit.add(Restrictions.le("id", endRowId));
        Log.info("Finding word with ID: " + beginRowId);
        try{
        ArrayList<WordMaster> list = (ArrayList<WordMaster>) crit.list();
      
        Log.info("after  afFinding word with ID: " + beginRowId);
        if (list != null && list.size() > 0)
        {
            return list;
        }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("No word with ID: " + beginRowId);
        return null;
    }

    @Deprecated
    public ArrayList<WordMaster> findWord(String word, String searchType)
    {
        
        // Please note.
        // In order to have Case-Sensitive Results, you must specify the
        // DB with a collate value of utf8_bin
        // Example : COLLATE=utf8_bin
        // Pls see below for more Info
        /*
         * DROP TABLE IF EXISTS `mw_dictionary`.`word_master`; CREATE TABLE
         * `mw_dictionary`.`word_master` ( `ID` int(10) unsigned NOT NULL
         * auto_increment, `WORD` varchar(50) collate utf8_bin NOT NULL default
         * '', `MEANING` varchar(2000) collate utf8_bin NOT NULL default '',
         * `WORDINSLP` varchar(50) collate utf8_bin NOT NULL default '',
         * `WORDINITRANS` varchar(50) collate utf8_bin NOT NULL default '',
         * `WORDINDVN` varchar(50) collate utf8_bin NOT NULL default '', PRIMARY
         * KEY (`ID`) ) ENGINE=InnoDB AUTO_INCREMENT=20308 DEFAULT CHARSET=utf8
         * COLLATE=utf8_bin COMMENT='InnoDB free: 6144 kB';
         */
        
        Criteria crit = getSession().createCriteria(WordMaster.class);
        if (StringUtils.equalsIgnoreCase(searchType, DictionaryConstants.LEFT))
        {
            Criterion crit1 = Restrictions.like("wordInSLP", word + "%");
            Criterion crit2 = Restrictions.like("wordinDVN",word + "%");
            LogicalExpression orExp = Restrictions.or(crit1,crit2);
            crit.add(orExp);
        }

        else if (StringUtils.equalsIgnoreCase(searchType, DictionaryConstants.RIGHT))
        {
            Criterion crit1 = Restrictions.like("wordInSLP", "%" + word);
            Criterion crit2 = Restrictions.like("wordinDVN", "%" + word);
            LogicalExpression orExp = Restrictions.or(crit1,crit2);
            crit.add(orExp);
        }

        else if (StringUtils.equalsIgnoreCase(searchType, DictionaryConstants.ALL))
        {
            Criterion crit1 = Restrictions.like("wordInSLP", "%" + word + "%");
            Criterion crit2 = Restrictions.like("wordinDVN", "%" + word + "%");
            LogicalExpression orExp = Restrictions.or(crit1,crit2);
            crit.add(orExp);
        }
        
        else
        {
            Criterion crit1 = Restrictions.like("wordInSLP",word);
            Criterion crit2 = Restrictions.like("wordinDVN",word);
            LogicalExpression orExp = Restrictions.or(crit1,crit2);
            crit.add(orExp);
        }

        crit.setMaxResults(DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED + 1);
        ArrayList<WordMaster> list = (ArrayList<WordMaster>) crit.list();
        if (list != null && list.size() > 0) return list;

        return null;
    }

    public ArrayList<String> findSuggestions(String word, String encoding)
    {
        ArrayList<String> suggestionList = new ArrayList<String>();
        
        Criteria crit = getSession().createCriteria(WordMaster.class);
        Criterion crit1 = Restrictions.like("wordInSLP", word + "%");
        Criterion crit2 = Restrictions.like("wordinDVN",word + "%");
        LogicalExpression orExp = Restrictions.or(crit1,crit2);
        crit.add(orExp);
        crit.setMaxResults(DictionaryConstants.DICTIONARY_MAX_SUGGESTIONS_ALLOWED + 1);
        
        ArrayList<WordMaster> list = (ArrayList<WordMaster>) crit.list();
        
        if (list != null && list.size() > 0) 
            {
                for(WordMaster wm : list)
                {
                    suggestionList.add(wm.getWordInSLP());
                }
                return suggestionList;
            }

        return null;
    }

}
