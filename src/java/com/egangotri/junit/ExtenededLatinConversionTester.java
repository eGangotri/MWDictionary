package com.egangotri.junit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import com.egangotri.util.*;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.egangotri.service.DictionaryService;
import com.egangotri.vo.WordMaster;

@Slf4j
public class ExtenededLatinConversionTester
{

    public void testExtendedLatinConversion()
    {
        Log.info("Entered testExtendedLatinConversion");
        DictionaryService service = (DictionaryService) SpringUtil.getBean("DictionaryService");
        Log.info("Entered xmlToDatabase");
        String xmlFilePath = Util.getXMLFilePath("a");
        int counter = 0;
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(new File(Util.getFilePath("test"))));
            String fileHeader = StringUtils.rightPad("Elatin", 30) + "\t" + StringUtils.rightPad("SLP", 30) + "\t" + StringUtils.rightPad("DVN", 30);
            writer.write(fileHeader);
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(new File(xmlFilePath));

            // Get the root element
            Element root = doc.getRootElement();
            // Print servlet information
            List entries = root.getChildren("entry");
            Log.info("Number of Entries: " + entries.size());
            Thread.sleep(2000);
            Iterator<Element> entryIterator = (Iterator<Element>) entries.iterator();

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
                            Log.info("Must be Word : " + subEntry.getName());
                            Log.info("The Word : " + subEntry.getText().trim());

                            String word = subEntry.getText().trim();

                            word = DictionaryService.removeExtraneousDigits(word);
                            wm.setWord(word);
                            Log.info("word " + word);
                            String wordAsSLP = EncodingUtil.convertIASTToSLP(word);
                            String wordAsDVN = EncodingUtil.convertSLPToDevanagari(wordAsSLP);
                            Log.info("wordAsSLP" + wordAsSLP);
                            wm.setWordInSLP(wordAsSLP);
                            wm.setWordinDVN(EncodingUtil.convertSLPToDevanagari(wordAsSLP));
                            wm.setWordinItrans(EncodingUtil.convertSLPToUniformItrans(wordAsSLP));
                            String textForFile = "\n" + StringUtils.rightPad(word, 30) + "\t" + StringUtils.rightPad(wordAsSLP, 30) + "\t" + StringUtils.rightPad(wordAsDVN, 30);
                            writer.write(textForFile);
                        }

                        else
                        {
                            // Implies Meaning Item
                            Log.info("Must be Meaning : " + subEntry.getName());
                            String meaning = subEntry.getText().trim();
                            Log.info("The Meaning : " + meaning);
                            meanings.append(meaning + DictionaryConstants.ARTHA_VIBHAKTA);
                            wm.setMeaning(meaning);
                        }
                    }
                    service.save(wm);
                }
            }
        }

        catch (Exception e)
        {
            Log.info("xmlToDatabase", e);
            Log.info(" counter == " + counter);
        }

        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }

                catch (Exception e)
                {
                    Log.info("final", e);
                }
            }
        }
        Log.info("Exiting xmlToDatabase");
    }

    public static void main(String args[])
    {
        new ExtenededLatinConversionTester().testExtendedLatinConversion();
    }

}
