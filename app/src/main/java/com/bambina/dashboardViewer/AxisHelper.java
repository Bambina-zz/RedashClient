package com.bambina.dashboardViewer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hirono-mayuko on 2017/05/27.
 */

public class AxisHelper {
    public static String determineAxis(JSONObject obj, String strCandidates) throws JSONException {
        List<String> candidates = Arrays.asList(strCandidates.split(","));
        for(String y:candidates){
            if(obj.has(y)) return y;
        }
        return "";
    }
}
