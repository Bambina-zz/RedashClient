package com.example.hirono_mayuko.redashclient2;

import org.json.JSONArray;

/**
 * Created by hirono-mayuko on 2017/04/27.
 */

public interface WidgetItem {
    public abstract void callback(JSONArray dataArray);
}
