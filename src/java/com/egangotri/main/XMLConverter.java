package com.egangotri.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import com.egangotri.util.DictionaryConstants;
import com.egangotri.util.Log;
import org.apache.commons.lang3.StringUtils;
import com.egangotri.util.Util;

public class XMLConverter
{
    String fileName;
    
    public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public final static String XML_DTD    = "<!DOCTYPE root SYSTEM \"dictionary.dtd\">";

    public XMLConverter()
    {
        this.fileName = "";
    }

    public XMLConverter(String fileName)
    {
        this.fileName = fileName;
    }

    /*
     * Converts the txt File retrieved from the Web-Page to an XML File
     */
    public void txtToXML()
    {
        //Step 1: Read the Contents of the Text File and convert to XML Equivalent
        String txtFilePath = Util.getFilePath(fileName);
        File txtFile = new File(txtFilePath);
        
        Log.info("Conversion from RAW Txt to XML started for " + txtFile.getAbsolutePath());
        
        StringBuilder XMLContent = new StringBuilder(XML_HEADER + "\r\n" + XML_DTD);

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(txtFile));
            String line = "";

            boolean DTTAGSighted = false;
            int count = 0;
            while ((line = reader.readLine()) != null)
            {
                count++;
                if(count < 10)
                Log.info("Processing line # " + count + "-> " + line);
                line = line.trim();
                
                if (StringUtils.contains(line,"<dl>"))
                {
                    XMLContent.append("\r\n<root>\r\n");
                }
                else if (StringUtils.contains(line,"</dl>"))
                {
                    XMLContent.append("\r\n</root>\r\n");
                }

                else
                {

                    // Using Empty Line as an Indication that another
                    // Entry(Word-Meaning) pair has been started
                    if (line.length() == 0 && DTTAGSighted)
                    {
                        DTTAGSighted = false;
                        XMLContent.append("\r\n</entry>\r\n");
                    }

                    else
                    {
                        String XMLLine = new String(line.getBytes(), "UTF-8");
                        // Replace Tag <dt> with <entry><word>
                        if (line.contains("<dt>"))
                        {
                            DTTAGSighted = true;
                            XMLLine = XMLLine.replace("<dt>", "\r\n<entry>\r\n<word>\r\n");

                        }
                        if (line.contains("</dt>"))
                        {
                            XMLLine = XMLLine.replace("</dt>", "</word>\r\n");
                        }

                        if (line.contains("<dd>"))
                        {
                            XMLLine = XMLLine.replace("<dd>", "\r\n<meaning>\r\n");
                        }

                        if (line.contains("</dd>"))
                        {
                            XMLLine = XMLLine.replace("</dd>", "\r\n</meaning>\r\n");
                        }

                        if (line.contains("<br>"))
                        {
                            XMLLine = XMLLine.replace("<br>", "\r\n");

                        }
                        if (line.contains("<p>"))
                        {
                            XMLLine = XMLLine.replace("<p>", "\r\n");
                        }

                        if (line.contains("<em>"))
                        {
                            XMLLine = XMLLine.replace("<em>", DictionaryConstants.EMPHASIS_OPENING_TAG);
                        }
                        
                        if (line.contains("</em>"))
                        {
                            XMLLine = XMLLine.replace("</em>", DictionaryConstants.EMPHASIS_CLOSING_TAG);
                        }
                        
                        //XMLLine =  replaceToUnicodeEquivalent(line, XMLLine);
                        
                        XMLLine = replaceTagByTag(line,XMLLine);

                        // We have to now handle <font> tags which make the XML
                        // File Invalid
                        // <font tags all always at the end of a Line
                        
                        if (XMLLine.contains("<font"))
                        {
                            String tmpXMLLine = "";
                            String tmp[] = XMLLine.split("<font");
                            Log.info("tmp.length: " + tmp.length);
                            Log.info("tmp[0] " + tmp[0]);
                            Log.info("tmp[1] " + tmp[1]);
                            if (tmp.length == 2)
                            {
                                // Must be two only
                                tmpXMLLine = tmp[0];
                                String tmp1[] = tmp[1].split("</font>");
                                if (tmp1.length == 2)
                                {
                                    tmpXMLLine += tmp1[1];
                                }
                            }
                            XMLLine = tmpXMLLine;
                            Log.info("tmpXMLLine: " + tmpXMLLine);
                        }
                        XMLContent.append(XMLLine + "\r\n");
                    }
                    line.replaceAll("<dt>", "");
                    line.replaceAll("</dt>", "");
                    line.replaceAll("<dd>", "");
                    line.replaceAll("</dd>", "");
                }

            }
           // Log.info("XMLContent: " + XMLContent);
        }

        catch (Exception e)
        {
            Log.info("convertCologneTxtFormatToXML",e);
        }

        finally
        {
            try
            {
                if(reader != null ) reader.close();
                System.gc();
                Thread.sleep(1000);
            }

            catch (Exception e)
            {
                Log.info("Exception closing reader",e);
            }
        }

        //Step 2: 
        //Write the XML String to an XML File
        String xmlFilePath =  Util.getXMLFilePath(fileName);
        OutputStreamWriter writer = null;
        try
        {
            File xmlFile = new File(xmlFilePath);
            Log.info(" Writing to File: " + xmlFile.getAbsolutePath());
            writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(xmlFile)),"UTF-8");
            writer.write(XMLContent.toString());
        }

        catch (Exception e)
        {
            Log.info("writeToXMLFile",e);
        }

        finally
        {
            try
            {
                if(writer != null ) writer.close();
            }
            catch (Exception ex)
            {

                Log.info("writeToXMLFile",ex);
            }
        }
    }
    
    //Use http://weber.ucsd.edu/~dkjordan/resources/unicodemaker.html
    private String replaceTagByTag(String line, String XMLLine)
    {
        // From here onwards we will replace Encoding Related
        // Strings, such as ELatin A etc    
        
        XMLLine = replaceAll(line, XMLLine, "worksAuthorsAbbrs.html", "/html/abbreviations.html");
        
        // Uncertain
        XMLLine = replaceAll(line, XMLLine, "&#x2022;", ""); //"•" .meaning-tag only
        XMLLine = replaceAll(line, XMLLine, "&#x22d9;", ""); // "○" word tag only
        // ex
        // after
        // agni
        // all
        // agnimaan,
        // agnishaala
        // etc
        // will
        // have
        // this
        XMLLine = replaceAll(line, XMLLine, "&#x0341;", ""); // word tag only
        // represents
        // similar
        // words,
        // for
        // ex
        // after
        // agni
        // all
        // agnimaan,
        // agnishaala
        // etc
        // will
        // have
        // this
        XMLLine = replaceAll(line, XMLLine, "&#x226b;", "");//"•"
        XMLLine = replaceAll(line, XMLLine, "&#x25cb;", "");//"•"
        XMLLine = replaceAll(line, XMLLine, "&#x221a;", "√");
        XMLLine = replaceAll(line, XMLLine, "&#x1e3b;", "ḻ");
        XMLLine = replaceAll(line, XMLLine, "&#x0341;", "");
        XMLLine = replaceAll(line, XMLLine, "&#x1e37;", "ḷ");
        XMLLine = replaceAll(line, XMLLine, "&#x0301;", "");

        // Vowels
        XMLLine = replaceAll(line, XMLLine, "&#x00e2;", "\u00e2"); // "â"
        XMLLine = replaceAll(line, XMLLine, "&#x00e1;", "\u00e1"); // "á"
        //XMLLine = replaceAll(line, XMLLine, "&#x0101;", "\u0101"); // ā  
        XMLLine = replaceAll(line, XMLLine, "&#x0101;", "\u0101"); // ā SLP "A"
        XMLLine = replaceAll(line, XMLLine, "&#x0100;", "\u0100"); // Ā capitalized
        // version
        // of
        // 'A'

        XMLLine = replaceAll(line, XMLLine, "&#x012b;", "\u012b" ); // "ī" represents
        // SLP
        // "I"(vIra)
        XMLLine = replaceAll(line, XMLLine, "&#x00ed;","\u00ed" ); // "í" represents
        // SLP
        // "I"(vIra)
        XMLLine = replaceAll(line, XMLLine, "&#x012a;","\u012a" ); // "Ī" capitalized
        // "I"(vIra)

        XMLLine = replaceAll(line, XMLLine, "&#x016b;", "\u016b"); // "ū" represents
        // SLP
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x00fa;", "\u00fa" ); // "ú" represents
        // SLP
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x016a;", "\u016a"); //  "Ū" capitalized
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x00fb;", "\u00fb"); // "û" capitalized
        // "U"(krUra)

        XMLLine = replaceAll(line, XMLLine, "&#x1e5b;", "\u1e5b"); //  "ṛ" represents
        // SLP
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e5a;","\u1e5a" ); // "Ṛ" capitalized
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e5d;", "\u1e5d"); // "ṝ" capitalized
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e36;", "\u1e36"); //  "Ḷ" capitalized
        // "f"(RRi)

        XMLLine = replaceAll(line, XMLLine, "&#x00e9;", "\u00e9" ); // "é"
        XMLLine = replaceAll(line, XMLLine, "&#x00ea;", "\u00ea"  ); // "ê"

        XMLLine = replaceAll(line, XMLLine, "&#x00f4;", "\u00f4" ); //  "ô"represents
        // SLP
        // "o"(meghodaya)
        XMLLine = replaceAll(line, XMLLine, "&#x00f3;", "\u00f3"  ); // "ó"represents
        // SLP
        // "o"(meghodaya)

        XMLLine = replaceAll(line, XMLLine, "&#x1e43;", "\u1e43"  ); // "ṃ" represents
        XMLLine = replaceAll(line, XMLLine, "&#x7747;", "\u7747"  ); // "ṃ" represents
        // SLP
        // "M"(aMsha)
        XMLLine = replaceAll(line, XMLLine, "&#x1e25;", "\u1e25"); //"ḥ" represents
        // visarga

        // vargiyas
        XMLLine = replaceAll(line, XMLLine, "&#x1e0d;", "\u1e0d" ); //"ḍ" represents
        // SLP
        // "q"(D
        // as
        // in
        // Danda)
        XMLLine = replaceAll(line, XMLLine, "&#x1e0c;", "\u1e0c"); // "Ḍ" represents
        // SLP
        // "q"(D
        // as
        // in
        // Danda)

        XMLLine = replaceAll(line, XMLLine, "&#x1e6d;","\u1e6d"); //  "ṭ" represents
        // SLP
        // "w"(T
        // as
        // in
        // TamaaTar)

        // Nasals:
        XMLLine = replaceAll(line, XMLLine, "&#x00f1;", "\u00f1" ); // "ñ" represents
        // SLP
        // "Y"(jYaana)
        XMLLine = replaceAll(line, XMLLine, "&#x1e45;", "\u1e45" ); //"ṅ" represents
        // SLP
        // "N"(kalaNka)
        XMLLine = replaceAll(line, XMLLine, "&#x1e47;", "\u1e47"); //  "ṇ"represents
        // SLP
        // "R"(N)

        // sh-sh
        XMLLine = replaceAll(line, XMLLine, "&#x015b;", "\u015b" ); // "ś"represents
        // SLP
        // "S"(Sh
        // as
        // in
        // Sharma)
        XMLLine = replaceAll(line, XMLLine, "&#x015a;", "\u015a" ); // "Ś" Capitalized
        // form
        // of
        // SLP
        // "S"(Sh
        // as
        // in
        // Sharma)

        XMLLine = replaceAll(line, XMLLine, "&#x1e63;", "\u1e63"); // "ṣ" represents
        // SLP
        // "z"(kzaNa)
        XMLLine = replaceAll(line, XMLLine, "&#x1e62;", "\u1e62" ); //"Ṣ"

        
        return XMLLine;
        
    }

    public static String replaceToUnicodeEquivalent(String replacee)
    {
        Log.info("replaceToUnicodeEquivalent");
        String replacer = "";
        if(replacee.length() != 8)
            return replacee;

        String s =  replacee.substring(3, 7);
        
        Log.info(s);
        int i = Integer.parseInt(s,16);
        Log.info("parsed int" + i);
        
        byte[] bytes = new byte[2];
        bytes[0] = 0;
        bytes[1] = (byte) i;

        replacer = new String(bytes);
        
        Log.info(replacer);
        return replacer;
        
    }



    public String replaceAll(String line, String XMLLine, String replacee, String replacer)
    {
        if(StringUtils.equalsIgnoreCase(replacee, "&#x0101;"))
        {
            Log.info("***** replacing " + replacee +  "with " + replacer);
        }
        if (line.contains(replacee))
        {
            XMLLine = XMLLine.replaceAll(replacee, replacer);
        }

        return XMLLine;
    }

    @Deprecated
    private String replaceTagByTagOLD(String line, String XMLLine)
    {
        // From here onwards we will replace Encoding Related
        // Strings, such as ELatin A etc    
        
        // Uncertain
        XMLLine = replaceAll(line, XMLLine, "&#x2022;", ""); //"•" Only
        // found
        // in
        // meaning
        // Tag,
        // represents
        // a
        // Link
        // Dot
        XMLLine = replaceAll(line, XMLLine, "&#x22d9;", ""); // "○"Only
        // found
        // in
        // Word
        // Tag,
        // represents
        // similar
        // words,
        // for
        // ex
        // after
        // agni
        // all
        // agnimaan,
        // agnishaala
        // etc
        // will
        // have
        // this
        XMLLine = replaceAll(line, XMLLine, "&#x0341;;", ""); // Only
        // found
        // in
        // Word
        // Tag,
        // represents
        // similar
        // words,
        // for
        // ex
        // after
        // agni
        // all
        // agnimaan,
        // agnishaala
        // etc
        // will
        // have
        // this
        XMLLine = replaceAll(line, XMLLine, "&#x226b;", "");//"•"
        XMLLine = replaceAll(line, XMLLine, "&#x25cb;", "");//"•"
        XMLLine = replaceAll(line, XMLLine, "&#x221a;", "\u221a"); //"√"
        XMLLine = replaceAll(line, XMLLine, "&#x1e3b;", "\u1e3b"); // "ḻ"
        XMLLine = replaceAll(line, XMLLine, "&#x0341;", "");
        XMLLine = replaceAll(line, XMLLine, "&#x1e37;", "\u1e37"); //"ḷ"
        XMLLine = replaceAll(line, XMLLine, "&#x0301;", "");

        // Vowels
        XMLLine = replaceAll(line, XMLLine, "&#x00e2;", "â"); // seems
        // like
        // a
        // variation
        // of
        // a)
        XMLLine = replaceAll(line, XMLLine, "&#x00e1;", "á"); // represents
        // SLP
        // "a"(aham)
        //XMLLine = replaceAll(line, XMLLine, "&#x0101;", "\u0101"); //   
        XMLLine = replaceAll(line, XMLLine, "&#x0101;", "ā"); // represents
        // SLP
        // "A"
        XMLLine = replaceAll(line, XMLLine, "&#x0100;", "Ā"); // capitalized
        // version
        // of
        // 'A'

        XMLLine = replaceAll(line, XMLLine, "&#x012b;", "ī"); // represents
        // SLP
        // "I"(vIra)
        XMLLine = replaceAll(line, XMLLine, "&#x00ed;", "í"); // represents
        // SLP
        // "I"(vIra)
        XMLLine = replaceAll(line, XMLLine, "&#x012a;", "Ī"); // capitalized
        // "I"(vIra)

        XMLLine = replaceAll(line, XMLLine, "&#x016b;", "ū"); // represents
        // SLP
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x00fa;", "ú"); // represents
        // SLP
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x016a;", "Ū"); // capitalized
        // "U"(krUra)
        XMLLine = replaceAll(line, XMLLine, "&#x00fb;", "û"); // capitalized
        // "U"(krUra)

        XMLLine = replaceAll(line, XMLLine, "&#x1e5b;", "ṛ"); // represents
        // SLP
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e5a;", "Ṛ"); // capitalized
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e5d;", "ṝ"); // capitalized
        // "f"(RRi)
        XMLLine = replaceAll(line, XMLLine, "&#x1e36;", "Ḷ"); // capitalized
        // "f"(RRi)

        XMLLine = replaceAll(line, XMLLine, "&#x00e9;", "é"); //
        XMLLine = replaceAll(line, XMLLine, "&#x00ea;", "ê"); //

        XMLLine = replaceAll(line, XMLLine, "&#x00f4;", "ô"); // represents
        // SLP
        // "o"(meghodaya)
        XMLLine = replaceAll(line, XMLLine, "&#x00f3;", "ó"); // represents
        // SLP
        // "o"(meghodaya)

        XMLLine = replaceAll(line, XMLLine, "&#x1e43;", "ṃ"); // represents
        // SLP
        // "M"(aMsha)
        XMLLine = replaceAll(line, XMLLine, "&#x1e25;", "ḥ"); // represents
        // visarga

        // vargiyas
        XMLLine = replaceAll(line, XMLLine, "&#x1e0d;", "ḍ"); // represents
        // SLP
        // "q"(D
        // as
        // in
        // Danda)
        XMLLine = replaceAll(line, XMLLine, "&#x1e0c;", "Ḍ"); // represents
        // SLP
        // "q"(D
        // as
        // in
        // Danda)

        XMLLine = replaceAll(line, XMLLine, "&#x1e6d;", "ṭ"); // represents
        // SLP
        // "w"(T
        // as
        // in
        // TamaaTar)

        // Nasals:
        XMLLine = replaceAll(line, XMLLine, "&#x00f1;", "ñ"); // represents
        // SLP
        // "Y"(jYaana)
        XMLLine = replaceAll(line, XMLLine, "&#x1e45;", "ṅ"); // represents
        // SLP
        // "N"(kalaNka)
        XMLLine = replaceAll(line, XMLLine, "&#x1e47;", "ṇ"); // represents
        // SLP
        // "R"(N)

        // sh-sh
        XMLLine = replaceAll(line, XMLLine, "&#x015b;", "ś"); // represents
        // SLP
        // "S"(Sh
        // as
        // in
        // Sharma)
        XMLLine = replaceAll(line, XMLLine, "&#x015a;", "Ś"); // Capitalized
        // form
        // of
        // SLP
        // "S"(Sh
        // as
        // in
        // Sharma)

        XMLLine = replaceAll(line, XMLLine, "&#x1e63;", "ṣ"); // represents
        // SLP
        // "z"(kzaNa)
        XMLLine = replaceAll(line, XMLLine, "&#x1e62;", "Ṣ");

        return XMLLine;
        
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    
}
