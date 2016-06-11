package com.egangotri.panel;

import java.awt.event.ActionEvent;

import com.egangotri.dict.MWSktEngDictionary;

public class EncodingPanelForDictionary extends EncodingPanel
{
    MWSktEngDictionary dict;
    public EncodingPanelForDictionary(MWSktEngDictionary dict){
        super();
        this.dict = dict;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        super.actionPerformed(e);
        dict.clearTextFields();
    }
}
