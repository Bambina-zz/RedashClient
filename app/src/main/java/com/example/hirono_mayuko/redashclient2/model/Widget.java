package com.example.hirono_mayuko.redashclient2.model;

import org.json.JSONArray;

/**
 * Created by hirono-mayuko on 2017/05/12.
 */

public abstract class Widget {
    public abstract void callback(JSONArray dataArray);
}
