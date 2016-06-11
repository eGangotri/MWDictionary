package com.egangotri.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.egangotri.util.Log;
import org.apache.commons.lang3.StringUtils;

import com.egangotri.util.Util;

/*
 * This Class reads HTML Files located in sub-paths of URL
 * "http://students.washington.edu/prem/mw/mw.html The read Contents are then dumped to
 * a txt File.
 */
public class WebPageReader
{
    String fileName = "";

    public WebPageReader(String fileName)
    {
        this.fileName = fileName;
    }

    /*
     * Read the given URL and then dump its content to a Text File
     */
    public String urlToTxtFile()
    {
        if (StringUtils.isBlank(fileName)) return "";

        String sourceURL = Util.getSourceURL(fileName);
        StringBuilder builder = new StringBuilder();
        // Create some URL objects
        URL url = null;
        try
        {
            Log.info("Reading from '" + sourceURL +"'");

            url = new URL(sourceURL);

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = "";

            boolean startReading = false;
            while (line != null)
            {
                line = reader.readLine();
                if (startReading == false)
                {
                    if (StringUtils.contains(line, "mwdata"))
                    {
                        startReading = true;
                    }
                    continue;
                }

                else if(StringUtils.contains(line, "</dl>"))
                {
                    builder.append("</dl>\r\n");
                    break;
                }
                builder.append(line + "\r\n");
            }
        }
        catch (Exception e)
        {
            Log.info("WebPageReader URL Reading Exception", e);
            return null;
        }

        // Start Writing
        String name = Util.getFilePath(fileName);
        Log.info("Writing contents of URL to " + name);
        File file = null;
        BufferedOutputStream bos = null;
        try
        {
            file = new File(name);
            file.createNewFile();
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(builder.toString().getBytes());
        }
        catch (Exception e)
        {
            Log.info("WebPageReader, FileOutputStream Exception", e);
        }

        finally
        {
            try
            {
                if (bos != null) bos.close();
                if (file != null) file = null;
            }
            catch (Exception e)
            {
                Log.info(e.getMessage(), e);
            }

        }
        return builder.toString();
    }

}
