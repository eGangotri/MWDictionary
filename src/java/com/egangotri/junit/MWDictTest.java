package com.egangotri.junit;

import java.util.ArrayList;

import com.egangotri.main.WebPageReader;
import com.egangotri.main.XMLConverter;
import com.egangotri.service.DictionaryService;
import com.egangotri.util.*;
import com.egangotri.vo.WordMaster;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MWDictTest extends TestCase
{
    public static Test suite()
    {
        return new TestSuite(MWDictTest.class);
    }

    protected void setUp()
    {
        try
        {
            super.setUp();
        }
        catch (Exception e)
        {
            Log.info("", e);
        } // this is required!! if not Spring will not be
        // initialised properly
    }

    /**
     * Overwrite this if there is for clean up This is a standard JUnit method,
     * this will be executed after each test method is executed.
     */
    protected void tearDown()
    {
    }

    // ---------------------------- Application Specific Test ---------------->
    // write your public test methods here, set up the methods to be called in
    // "suite()"
    public void t1estURL()
    {
        int counter = 0;
        for (String fileName : DictionaryConstants.fileNames)
        {
            WebPageReader webPageReader = new WebPageReader(fileName);
            long startTime = System.nanoTime();
            webPageReader.urlToTxtFile();
            long endTime = System.nanoTime();
            Util.getTimeDiff(startTime, endTime);
            counter++;

        }
    }

    public void t1estConversionToXML()
    {
        int counter = 0;
        for (String fileName : DictionaryConstants.fileNames)
        {
            XMLConverter xmlConverter = new XMLConverter(fileName);
            long startTime = System.nanoTime();
            xmlConverter.txtToXML();
            long endTime = System.nanoTime();
            Util.getTimeDiff(startTime, endTime);
            counter++;
        }
    }

    public void t1estXmlToDB()
    {
        Log.info("Started Conversions of all XML Files to DB");
        int counter = 0;
        for (String fileName : DictionaryConstants.fileNames)
        {
            DictionaryService service = (DictionaryService) SpringUtil.getBean("DictionaryService");
            long startTime = System.nanoTime();
            service.xmlToDatabase(fileName);
            long endTime = System.nanoTime();
            Util.getTimeDiff(startTime, endTime);
            counter++;
            break;
        }
    }

    public void test()
    {
        //t1estURL();
        //t1estConversionToXML();
        //t1estXmlToDB();
        DictionaryService service = (DictionaryService) SpringUtil.getBean("DictionaryService");
       // service.logUserActivityToDB("Test", "85.136.212.161", "/no terms");
        ArrayList<WordMaster> wordList =  service.findWord(EncodingUtil.convertToDVN("यवन", ""), DictionaryConstants.EXACT);
        Log.info("wordList: " + wordList.size());
        
        
    }
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

}
