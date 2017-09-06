package com.egangotri.monierWilliams.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.egangotri.monierWilliams.dao.DictionaryDAOSupport;

public class SpringUtil
{
    private static final String APPLICATION_CONTEXT_FILE = "spring.xml";

    public static DictionaryDAOSupport getDAO()
    {
        ApplicationContext context;
        context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
        DictionaryDAOSupport dao = (DictionaryDAOSupport) context.getBean("DictionaryDAOSupport");
        return dao;
    }
    
    public static Object getBean(String id)
    {
        ApplicationContext context;
        context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
        return  context.getBean(id);

    }
}
