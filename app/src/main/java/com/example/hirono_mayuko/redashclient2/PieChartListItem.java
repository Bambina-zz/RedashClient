package com.example.hirono_mayuko.redashclient2;

/**
 * Created by hirono-mayuko on 2017/04/26.
 */

public class PieChartListItem {
    public String mName;
    public String mValue;
    public String mPercentage;

    public PieChartListItem(String name, String value, String percentage){
        mName = name;
        mValue = value;
        mPercentage = percentage;
    }
}
