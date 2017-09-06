package com.egangotri.monierWilliams.dao.vo.main;

import com.egangotri.monierWilliams.service.DictionaryService;
import com.egangotri.monierWilliams.util.DictionaryConstants;
import com.egangotri.util.Log;
import com.egangotri.monierWilliams.util.SpringUtil;
import com.egangotri.monierWilliams.util.Util;

public class MainClass
{
/*
    public static final String   ARTHA_VIBHAKTA       = "##";

    public static final String   EMPHASIS_OPENING_TAG = "{";

    public static final String   EMPHASIS_CLOSING_TAG = "}";

    public static final String   LEFT                 = "Left";

    public static final String   RIGHT                = "Right";

    public static final String   ALL                  = "All";

    public static final String   EXACT                = "Exact";

    public static final int      DICTIONARY_MAX_RESULTS_ALLOWED = 100;

    public static final int      DICTIONARY_MAX_SUGGESTIONS_ALLOWED = 10;
    
    public static final String[] fileNames            =
                                                      { "a", "aa", "i", "ii", "u", "uu", "RR", "RRRR", "lR", "lRR", "e", "ai", "o", "au" , "k", "kh", "g", "gh", "gg", "c", "ch", "j", "jh", "jj", "tt", "tth", "d", "ddh", "nn", "t", "th", "d", "dh", "n", "p", "ph", "b", "bh", "m", "y", "r", "l", "v", "z", "ss", "s", "h" };
*/

    private DictionaryService service;
    
    public DictionaryService getService(){
        return service;
    }
    
    public void setService(DictionaryService ds){
        service = ds;
    }
    
    
    /**
     * @param args
     */
    public static void main(String args[])
    {
        Log.info("Started Conversions of all XML Files to DB");
        long programStartTime = System.nanoTime();
        MainClass mc = new MainClass();
        int counter = 0;
        for (String fileName : DictionaryConstants.fileNames)
        {
            mc.setService((DictionaryService) SpringUtil.getBean("DictionaryService"));
            long startTime = System.nanoTime();
            mc.getService().xmlToDatabase(fileName);
            long endTime = System.nanoTime();
            Util.getTimeDiff(startTime, endTime);
            counter++;
            try
            {
            Thread.sleep(10000);
            }
            
            catch(Exception e)
            {
                e.printStackTrace();
            }
            System.gc();
        }
        long programStopTime = System.nanoTime();
        Log.info("Terminated Conversions of all XML Files to DB.");
        Util.getTimeDiff(programStartTime, programStopTime);
    }

    public void runTasks()
    {
        // Better to use MWDictTest.java
        DictionaryService service = (DictionaryService) SpringUtil.getBean("DictionaryService");

        Log.info("********XML Conversion Program started.");
        try
        {
            for (String fileName : DictionaryConstants.fileNames)
            {
                XMLConverter xmlConverter = new XMLConverter(fileName);

                String filePath = Util.getFilePath(fileName);
                String xmlFilePath = Util.getXMLFilePath(fileName);

                xmlConverter.txtToXML();

                service.xmlToDatabase(fileName);

                // To prevent Memory Leak
                System.gc();
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.info("********XML Conversion Program ended.");
        // Spring Manipulation
        // toXML.springTest();
    }
}
