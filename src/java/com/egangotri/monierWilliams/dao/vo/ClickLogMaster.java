package com.egangotri.monierWilliams.dao.vo;

import java.util.Calendar;

public class ClickLogMaster implements java.io.Serializable
{
    private long     id;

    private String   module;

    private Calendar time;

    private String   ipAddress;

    private String   country;

    private String   city;
    
    private String region;

    private String   searchTerms;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public Calendar getTime()
    {
        return time;
    }

    public void setTime(Calendar time)
    {
        this.time = time;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getSearchTerms()
    {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms)
    {
        this.searchTerms = searchTerms;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

}
