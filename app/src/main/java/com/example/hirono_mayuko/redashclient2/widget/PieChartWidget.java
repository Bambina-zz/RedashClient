package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.AxisHelper;
import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.PieChartListItem;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class PieChartWidget extends Widget {
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private PieChartWidgetItem mItem;
    public boolean isFailed = false;
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
        String isMultipleYAxis = mVisualData.get(Dashboard.IS_MULTIPLE_Y_AXIS);
        if(isMultipleYAxis.equals("true")){
            // Determine y axis from candidates.
            try {
                yAxis = AxisHelper.determineAxis(dataArray.getJSONObject(0), yAxis);
            } catch (JSONException e){
                e.printStackTrace();
                isFailed = true;
            }
        }

        // Collect data in rows into HashMap.
        // There are rows which has the same x-axis value.
        HashMap<String, BigDecimal> pairsOfXY = new HashMap<>();
        BigDecimal mSum = new BigDecimal(0);

        for(int i=0; i < dataArray.length(); i++){
            try {
                JSONObject obj = dataArray.getJSONObject(i);
                String name = obj.getString(xAxis);
                BigDecimal value = new BigDecimal(obj.getString(yAxis));
                mSum = mSum.add(value);
                if(pairsOfXY.containsKey(name)){
                    BigDecimal y = pairsOfXY.get(name);
                    pairsOfXY.put(name, y.add(value));
                } else {
                    pairsOfXY.put(name, value);
                }
            } catch (JSONException e){
                e.printStackTrace();
                isFailed = true;
                break;
            }
        }

        // Sort hashmap of pairs(x, y).
        List<Map.Entry<String, BigDecimal>> sortedPairsOfXY =  sort(pairsOfXY);

        // Put hashmap into member variables.
        List<PieEntry> mPieEntries = new ArrayList<>();
        for(Map.Entry<String, BigDecimal> pair:sortedPairsOfXY){
            String nameX = pair.getKey();
            BigDecimal valueY = pair.getValue();
            // TODO: Some of them are hard coding.
            DecimalFormat df = new DecimalFormat("#,###");
            String formatValueY = df.format(valueY);

            BigDecimal percentageBD = (valueY.divide(mSum, 4, BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(100));
            DecimalFormat df2 = new DecimalFormat(" #,##0.00 '%'");
            String percentage = df2.format(percentageBD);

            // For list view.
            PieChartListItem item = new PieChartListItem(nameX, formatValueY, percentage);
            mPieListItems.add(item);

            Map<String, String> obj = new HashMap<>(3);
            obj.put("percentage", percentage);
            obj.put("value", formatValueY);
            obj.put("name", nameX);

            // For labels on the chart.
            if(0 > percentageBD.compareTo(new BigDecimal(5))){
                // A label will be displayed on the chart, when its value is more than 5%.
                nameX = "";
            }
            PieEntry entry = new PieEntry(valueY.floatValue(), nameX, obj);
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

    private List<Map.Entry<String, BigDecimal>> sort(HashMap<String, BigDecimal> pairsOfXY){
        List<Map.Entry<String, BigDecimal>> sortedPairsOfXY =  new ArrayList<>(pairsOfXY.entrySet());
        Collections.sort(sortedPairsOfXY, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String,BigDecimal> entry1, Map.Entry<String,BigDecimal> entry2) {
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
