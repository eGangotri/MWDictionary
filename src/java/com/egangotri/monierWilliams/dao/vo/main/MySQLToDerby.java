package com.egangotri.monierWilliams.dao.vo.main;

import java.util.ArrayList;

import com.egangotri.monierWilliams.service.DictionaryService;
import com.egangotri.util.Log;
import com.egangotri.monierWilliams.util.SpringUtil;
import com.egangotri.monierWilliams.dao.vo.WordMaster;

public class MySQLToDerby
{
    DictionaryService service ;
    public MySQLToDerby()
    {
        service = (DictionaryService) SpringUtil.getBean("DictionaryService");
    }
    

    public void insertWords(){
        int highestId = service.getDictionaryDAO().findHighestId();
        ArrayList<WordMaster> list = service.getDictionaryDAO().findWord(0, highestId);
        for (WordMaster wordInMySQL : list)
        {
            Log.info("Processing id = " + wordInMySQL.getId());
            service.getDerbyDAO().save(wordInMySQL);
            if ( wordInMySQL.getId() % 10000 == 0 ) { //20, same as the JDBC batch size
              //flush a batch of inserts and release memory:
                try
                {
                Thread.sleep(10000);
                System.gc();
                }
                
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                Log.info("flushing id = " + ( wordInMySQL.getId()));
                service.getDictionaryDAO().getHibernateTemplate().flush();
                service.getDictionaryDAO().getHibernateTemplate().clear();
              service.getDerbyDAO().getHibernateTemplate().flush();
              service.getDerbyDAO().getHibernateTemplate().clear();
              }
        }
        Log.info("Done transfer of wordsfrom MySQL to Derby.");
    }
    public static void main(String[] args)
    {
        new MySQLToDerby().insertWords();
    }

}
