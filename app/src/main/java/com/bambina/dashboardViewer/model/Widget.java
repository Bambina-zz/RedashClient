package com.bambina.dashboardViewer.model;

import org.json.JSONArray;

/**
 * Created by hirono-mayuko on 2017/05/12.
 */

public abstract class Widget {
    public abstract void callback(JSONArray dataArray);
}
