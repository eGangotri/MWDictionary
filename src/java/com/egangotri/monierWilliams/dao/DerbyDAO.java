package com.egangotri.monierWilliams.dao;

import java.util.ArrayList;

import com.egangotri.util.Log;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.egangotri.monierWilliams.dao.vo.WordMaster;

public class DerbyDAO extends HibernateDaoSupport
{
    
  public int findHighestId(){
        
        int highestId = 0;
        Criteria crit = getSession().createCriteria(WordMaster.class);
        crit.setProjection(Projections.max("id"));
        ArrayList<Object> list = (ArrayList<Object> ) crit.list();
        highestId =  (Integer) list.get(0);
        Log.info("highest id " +highestId);
        return highestId;
    }
  
  
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
}
