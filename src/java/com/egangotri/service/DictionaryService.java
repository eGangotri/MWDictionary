package com.egangotri.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.egangotri.db.DBUtil;
import com.egangotri.util.DictionaryConstants;
import com.egangotri.util.Log;
import org.apache.commons.lang3.StringUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.egangotri.dao.DerbyDAO;
import com.egangotri.dao.DictionaryDAOSupport;
import com.egangotri.util.Util;
import com.egangotri.vo.WordMaster;
import com.egangotri.util.EncodingUtil;

public class DictionaryService
{
    private DictionaryDAOSupport dictionaryDAO;
    
    private DerbyDAO derbyDAO;

    public DictionaryDAOSupport getDictionaryDAO()
    {
        return dictionaryDAO;
    }

    public void setDictionaryDAO(DictionaryDAOSupport dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }

    public boolean save(WordMaster wm)
    {
        return getDictionaryDAO().save(wm);
    }

    public WordMaster findWord(int id)
    {
        return getDictionaryDAO().findWord(id);
    }

    public ArrayList<WordMaster> findWord(String word, String searchType )
    {
        //return getDictionaryDAO().findWord(word, searchType );
        long count = DBUtil.findWordCount(word,searchType);
        if(count > DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED){
            System.out.println("More Values than the Limit of ${DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED} found");
        }
        return DBUtil.findWord(word,searchType);

    }


    public ArrayList<String> findSuggestions(String word, String encoding)
    {
        return getDictionaryDAO().findSuggestions(word, encoding );
    }
    public void xmlToDatabase(String xmlFileName)
    {
        Log.info("Entered xmlToDatabase");
        String xmlFilePath = Util.getXMLFilePath(xmlFileName);
        int counter = 0;
        try
        {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(new File(xmlFilePath));

            // Get the root element
            Element root = doc.getRootElement();
            // Print servlet information
            List entries = root.getChildren("entry");
            Log.info("Number of Entries: " + entries.size());
            Iterator<Element> entryIterator = (Iterator<Element>)  entries.iterator();
            // this iterates over the 'root' Element which contains 'entry'
            // subElements
            while (entryIterator.hasNext())
            {
                Element entry = (Element) entryIterator.next();

                Log.info(" inside Entry # " + ++counter);
                if (entry != null)
                {
                    List<Element> subEntries = (List<Element>) entry.getChildren();
                    Iterator<Element> subEntryIterator = (Iterator<Element>) subEntries.iterator();

                   
                    // This iterates over an 'entry' element whose first element
                    // is 'word' followed by many 'meaning' elements
                    int subEntryCounter = 0;
                    WordMaster wm = new WordMaster();
                    StringBuilder meanings = new StringBuilder();
                    while (subEntryIterator.hasNext())
                    {
                        subEntryCounter++;
                        Element subEntry = (Element) subEntryIterator.next();

                        if (subEntryCounter == 1)
                        {
                            // Implies a Word
                            Log.info("The Word : " + subEntry.getText().trim());

                            String word = subEntry.getText().trim();
                     
                            word = DictionaryService.removeExtraneousDigits(word);
                            wm.setWord(word);
                            String wordAsSLP = EncodingUtil.convertIASTToSLP(word);
                            wm.setWordInSLP(wordAsSLP);
                            String wordInDVN = EncodingUtil.convertSLPToDevanagari(wordAsSLP);
                            wm.setWordinDVN(wordInDVN);
                            Log.info("wordInDVN: " + wordInDVN);
                            wm.setWordinItrans(EncodingUtil.convertSLPToUniformItrans(wordAsSLP));
                            
                        }

                        else
                        {
                            // Implies Meaning Item
                            Log.info("Must be Meaning : " + subEntry.getName());
                            String meaning = subEntry.getText().trim();
                            Log.info("The Meaning : " + meaning);
                            meanings.append(meaning + DictionaryConstants.ARTHA_VIBHAKTA);
                        }
                    }

                    try
                    {
                        if (wm != null && meanings != null && StringUtils.isNotBlank(wm.getWord()))
                        {
                            wm.setMeaning(StringUtils.removeEnd(meanings.toString(), DictionaryConstants.ARTHA_VIBHAKTA) );
                            Log.info("Added Word #" + counter);
                            save(wm);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.info("xmlToDatabase.Problem saving to dao",e);
                    }
                }
            }
        }

        catch (Exception e)
        {
            Log.info("xmlToDatabase",e);
            Log.info(" counter == " + counter);
        }
        Log.info("Exiting xmlToDatabase");
    }
    
    /*
     *   The following cos some entries can be like 'a 5'
         where the numeric entry specifies the frequency
         of that word.
        Hence we disregard the numeric entry
     */
    public static String removeExtraneousDigits(String word)
    {
        String reformedWord[] = word.split(" ");
        if( reformedWord.length > 1)
        {
            String lastWord = reformedWord[reformedWord.length-1];
            if(Character.isDigit(lastWord.charAt(0)))
            {
                StringBuffer trimmedWord = new StringBuffer();
                for(int i = 0 ; i < reformedWord.length -1 ; i++)
                {
                    trimmedWord.append(reformedWord[i]);
                }
                word = trimmedWord.toString();
            }
        }
        
        return word;
    }
    
    public void logUserActivityToDB(String moduleName, String ipAddress,String searchTerm)
    {
       // getDictionaryDAO().logUserActivityToDB(moduleName, ipAddress, searchTerm);
    }
    
//    public String decorateResult(ArrayList<WordMaster> wordList, String entry, String transformedEntry)
//    {
//        if(wordList == null || wordList.size() == 0){
//            return "No such Word '<i>" + entry + "</i>' [" + transformedEntry + "]" + " Found";
//        }
//
//        // Set Notes
//        StringBuilder notes = new StringBuilder();
//        String heading = wordList.size() > DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED ? "More than " + DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED + " Meanings were found.<br>Only the First " + DictionaryConstants.DICTIONARY_MAX_RESULTS_ALLOWED + " Meanings will be shown" : "Found " + wordList.size() + " meaning(s)";
//        notes.append(heading);
//        notes.append(" for Search Term: <b>" + entry + " </b>[<b>" + transformedEntry + "</b>]");
//        int counter = 0;
//        for (WordMaster word : wordList)
//        {
//            notes.append("<br><font color='green'>*****************************</font>");
//            notes.append("<br><font color='brown'>" + ++counter + ".&nbsp;" + "<i><u>" + word.getWordinDVN() + ", " + word.getWord() + "</i></u></font><br>");
//            // notes.append("<br>");
//            String[] meanings = word.getMeaning().split(DictionaryConstants.ARTHA_VIBHAKTA);
//            if (meanings != null && meanings.length > 0)
//            {
//                notes.append("<ol>");
//                for (String meaning : meanings)
//                {
//                    notes.append("<li> <font color='blue'>" + meaning + "</font></li>");
//                }
//                notes.append("</ol>");
//            }
//
//        }
//        return notes.toString();
//    }
    

    public DerbyDAO getDerbyDAO()
    {
        return derbyDAO;
    }


    public void setDerbyDAO(DerbyDAO derbyDAO)
    {
        this.derbyDAO = derbyDAO;
    }
    
    
}
