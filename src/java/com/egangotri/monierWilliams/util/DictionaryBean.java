package com.egangotri.monierWilliams.util;

public class DictionaryBean
{
    
private String entry; 

private String transformedEntry;

private String encoding;

private String searchType;

private String meaning;

public DictionaryBean(){
    
}

public DictionaryBean(String entry, String transformedEntry, String encoding, String searchType, String meaning ){
    this.entry = entry;
    this.transformedEntry = transformedEntry;
    this.encoding = encoding;
    this.meaning = meaning;
    this.searchType = searchType;
}

public String getEntry()
{
    return entry;
}

public void setEntry(String entry)
{
    this.entry = entry;
}

public String getTransformedEntry()
{
    return transformedEntry;
}

public void setTransformedEntry(String transformedEntry)
{
    this.transformedEntry = transformedEntry;
}

public String getEncoding()
{
    return encoding;
}

public void setEncoding(String encoding)
{
    this.encoding = encoding;
}

public String getSearchType()
{
    return searchType;
}

public void setSearchType(String searchType)
{
    this.searchType = searchType;
}

public String getMeaning()
{
    return meaning;
}

public void setMeaning(String meaning)
{
    this.meaning = meaning;
}


}
