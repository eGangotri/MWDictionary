package com.egangotri.monierWilliams.dao.vo.main;

import java.io.File;
import java.util.Iterator;

import com.egangotri.util.Log;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class LookupMeaning
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        LookupMeaning l = new LookupMeaning();
        File file = new File("src//xml//a.xml");
        Log.info(" file::: " + file.getAbsolutePath());
        try
        {
            Document document = new SAXReader().read(file);
            Element root = document.getRootElement();

            boolean breakOut  = false;
            // iterate through child elements of root
            for (Iterator i = root.elementIterator(); i.hasNext();)
            {
                Element element = (Element) i.next();
                // l.p(list.toString());
                 for (int j = 0; j < element.nodeCount(); j++)
                {
                    Node node = element.node(j);
                    String StringValue = node.getStringValue().trim();
                    if(StringValue.length() == 0) continue;
                    
                    if(node.getName().equalsIgnoreCase("word"))
                    {
                        
                        if(StringValue.equalsIgnoreCase("ahna"))
                        {
                            l.p("Node:" + node.getName() + ": String Value["+j+"]" + "*" + StringValue + "*");
                            l.p("Found String ahna");
                            breakOut = true;
                            break;
                        }
                    }
                    
                    l.p("Node:" + node.getName() + ": String Value["+j+"]" + "*" + StringValue + "*");
                  
                }
                 if( breakOut) break;
                l.p("\n");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void p(String s)
    {
        Log.info(s);
    }
}
