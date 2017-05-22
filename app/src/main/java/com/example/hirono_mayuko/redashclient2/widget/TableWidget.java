package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.item.TableWidgetItem;
import com.example.hirono_mayuko.redashclient2.model.Widget;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class TableWidget extends Widget {
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private TableWidgetItem mItem;

    public String mVisualName;
    public String mQueryName;

    public TableWidget(HashMap<String, String> visualData, MainActivity activity, TableWidgetItem item){
        mVisualData = visualData;
        mainActivity = activity;
        mItem = item;
        mainActivity.queryData(visualData.get(Dashboard.QUERY_ID), this);
    }

    public void setData(JSONArray dataArray) {
        mVisualName = mVisualData.get(Dashboard.VISUAL_NAME);
        mQueryName = mVisualData.get(Dashboard.QUERY_NAME);
        mItem.notifyWidgetChanged();
    }

    public void callback(JSONArray dataArray){
        this.setData(dataArray);
    }
}
