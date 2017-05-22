package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.PieChartListItem;
import com.example.hirono_mayuko.redashclient2.ServiceNameFormatter;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.item.PieChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.model.Widget;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class PieChartWidget extends Widget {
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private PieChartWidgetItem mItem;
    public PieData mPieData;
    public List<PieChartListItem> mPieListItems = new ArrayList<>();

    public PieChartWidget(HashMap<String, String> visualData, MainActivity activity, PieChartWidgetItem item){
        mVisualData = visualData;
        mainActivity = activity;
        mItem = item;
        mainActivity.queryData(mVisualData.get(Dashboard.QUERY_ID), this);
    }

    public void setData(JSONArray dataArray){
        // Get axis information.
        String xAxis = mVisualData.get(Dashboard.X_AXIS);
        String yAxis = mVisualData.get(Dashboard.Y_AXIS);

        // Collect data in rows into HashMap.
        // There are rows which has the same x-axis value.
        HashMap<String, Float> pairsOfXY = new HashMap<>();
        Float mSum = 0f;
        try {
            for(int i=0; i < dataArray.length(); i++){
                JSONObject obj = dataArray.getJSONObject(i);
                String name = obj.getString(xAxis);
                Float value = Float.parseFloat(obj.getString(yAxis));
                mSum += value;
                if(pairsOfXY.containsKey(name)){
                    Float y = pairsOfXY.get(name);
                    pairsOfXY.put(name, y + value);
                } else {
                    pairsOfXY.put(name, value);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        // Sort hashmap of pairs(x, y).
        List<Map.Entry<String, Float>> sortedPairsOfXY =  sort(pairsOfXY);

        // Put hashmap into member variables.
        List<PieEntry> mPieEntries = new ArrayList<>();
        for(Map.Entry<String, Float> pair:sortedPairsOfXY){
            String nameX = ServiceNameFormatter.getServiceName(mainActivity.getContext(), pair.getKey());
            Float valueY = pair.getValue();
            // TODO: Some of them are hard coding.
            String formatValueY = String.format(Locale.US, "%1$,3d円", Math.round(pair.getValue()));
            Float percentageF = 100 * valueY / mSum;
            String percentage = String.format(Locale.US, "%.2f%%", percentageF);

            // For list view.
            PieChartListItem item = new PieChartListItem(nameX, formatValueY, percentage);
            mPieListItems.add(item);

            Map<String, String> obj = new HashMap<>(2);
            obj.put("percentage", percentage);
            obj.put("name", nameX);

            // For labels on the chart.
            if(percentageF < 5f){
                // A label will be displayed on the chart, when its value is more than 5%.
                nameX = "";
            }
            PieEntry entry = new PieEntry(valueY, nameX, obj);
            mPieEntries.add(entry);
        }

        // TODO: Some of them are hard coding.
        PieDataSet dataSet = new PieDataSet(mPieEntries, "");
        dataSet.setColors(MainActivity.CHART_COLOR, mainActivity.getContext());
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(0xFFFFFFFF);
        mPieData = new PieData(dataSet);
        mPieData.setHighlightEnabled(true);
        mPieData.setValueFormatter(new PercentFormatter());
        mPieData.setDrawValues(false);

        mItem.notifyWidgetChanged();
    }

    private List<Map.Entry<String, Float>> sort(HashMap<String, Float> pairsOfXY){
        List<Map.Entry<String, Float>> sortedPairsOfXY =  new ArrayList<>(pairsOfXY.entrySet());
        Collections.sort(sortedPairsOfXY, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String,Float> entry1, Map.Entry<String,Float> entry2) {
                return (entry2.getValue()).compareTo(entry1.getValue());
            }
        });
        return sortedPairsOfXY;
    }

    public void callback(JSONArray dataArray){
        this.setData(dataArray);
    }

    public String getQueryName(){
        return mVisualData.get(Dashboard.QUERY_NAME);
    }
}
