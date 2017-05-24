package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.item.TableWidgetItem;
import com.example.hirono_mayuko.redashclient2.model.Widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class TableWidget extends Widget {
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private TableWidgetItem mItem;

    private ArrayList<String> mColNames;
    private static final int NUM_ROWS = 6;
    private static final int NUM_PAGES = 5;
    private ArrayList<ArrayList<ArrayList<String>>> mData;
    public String mVisualName;
    public String mQueryName;

    public TableWidget(HashMap<String, String> visualData, MainActivity activity, TableWidgetItem item){
        mVisualData = visualData;
        mainActivity = activity;
        mItem = item;
        mainActivity.queryData(visualData.get(Dashboard.QUERY_ID), this);
    }

    public void setData(JSONArray dataArray) {
        // Get column names.
        try {
            mColNames = getColNames(dataArray.getJSONObject(0));
        } catch (JSONException e){
            e.printStackTrace();
        }
        mData = new ArrayList<>(NUM_PAGES);
        int len = dataArray.length();
        if(len > 25) len = 25;

        ArrayList<ArrayList<String>> page = new ArrayList<>(NUM_ROWS);
        page.add(mColNames);
        for(int i=0; i < len; i++){
            if(i != 0 && i%NUM_PAGES == 0){
                mData.add(page);
                page = new ArrayList<>(NUM_ROWS);
                page.add(mColNames);
            }

            ArrayList<String> row = new ArrayList<>();
            try {
                JSONObject obj = dataArray.getJSONObject(i);
                for(String col:mColNames){
                    String val = obj.getString(col);
                    row.add(val);
                }
                page.add(row);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        mData.add(page);

        mVisualName = mVisualData.get(Dashboard.VISUAL_NAME);
        mQueryName = mVisualData.get(Dashboard.QUERY_NAME);
        mItem.notifyWidgetChanged();
    }

    public void callback(JSONArray dataArray){
        this.setData(dataArray);
    }

    private ArrayList<String> getColNames(JSONObject obj){
        Iterator<String> keys = obj.keys();
        ArrayList<String> colNames = new ArrayList<>();
        while(keys.hasNext()){
            String name = keys.next();
            colNames.add(name);
            System.out.println(name);
        }
        return colNames;
    }

    public ArrayList<ArrayList<String>> getPageData(int position){
        return mData.get(position);
    }

    public ArrayList<ArrayList<ArrayList<String>>> getData(){
        return mData;
    }
}
