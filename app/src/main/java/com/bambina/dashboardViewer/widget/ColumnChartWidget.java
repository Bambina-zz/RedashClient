package com.bambina.dashboardViewer.widget;

import com.bambina.dashboardViewer.AxisHelper;
import com.bambina.dashboardViewer.ConvertDateFromString;
import com.bambina.dashboardViewer.model.Dashboard;
import com.bambina.dashboardViewer.activity.MainActivity;
import com.bambina.dashboardViewer.item.ColumnChartWidgetItem;
import com.bambina.dashboardViewer.model.Widget;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class ColumnChartWidget extends Widget {
    public BarData mBarData;
    public Long minTime = 0L;
    public Long maxTime = 0L;
    public boolean isJsonException = false;
    public boolean isDateTime = true;
    public int numBars = 0;
    public int numSeries = 0;
    public String xAxisType;
    public ArrayList<String> xLabels = new ArrayList<>();
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private ColumnChartWidgetItem mItem;
    private static final String X_MILLI_SEC = "xmsec";
    private static final String LABEL = "label";
    private static final String NORMALIZED_X = "normalizedX";
    private static final String X = "x";
    private static final String Y = "y";

    public ColumnChartWidget(HashMap<String, String> visualData, MainActivity activity, ColumnChartWidgetItem item){
        mVisualData = visualData;
        mainActivity = activity;
        mItem = item;
        mainActivity.queryData(mVisualData.get(Dashboard.QUERY_ID), this);
    }

    public void setData(JSONArray dataArray){
        String xAxis = mVisualData.get(Dashboard.X_AXIS);
        String yAxis = mVisualData.get(Dashboard.Y_AXIS);
        String isMultipleYAxis = mVisualData.get(Dashboard.IS_MULTIPLE_Y_AXIS);
        if(isMultipleYAxis.equals("true")){
            // Determine y axis from candidates.
            try {
                yAxis = AxisHelper.determineAxis(dataArray.getJSONObject(0), yAxis);
            } catch (JSONException e){
                e.printStackTrace();
                isJsonException = true;
            }
        }

        String series = mVisualData.get(Dashboard.SERIES);
        HashMap<String, List<HashMap<String, Object>>> mData = new HashMap<>();
        mBarData = new BarData();

        // TODO: In this function xAxisType is supposed to be "datetime".
        xAxisType = mVisualData.get(Dashboard.X_AXIS_TYPE);
        if(xAxisType.equals("datetime")) {
            Locale locale = Locale.getDefault();
            // TODO: Data type of both "2017-5-26" and "2017-05-23T03:15:00+00:00" is datetime.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", locale);
            try {
                String x = dataArray.getJSONObject(0).getString(xAxis);
                if (x.contains("T")) {
                    format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+'", locale);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                isJsonException = true;
            }

            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String x = obj.getString(xAxis);
                    long xMillisec = ConvertDateFromString.parse(x, format).getTime();
                    float y = Float.parseFloat(obj.getString(yAxis));

                    // Create a HashMap of an entry.
                    HashMap<String, Object> entry = new HashMap<>();
                    entry.put(LABEL, x);
                    entry.put(X_MILLI_SEC, xMillisec);
                    entry.put(Y, y);

                    String seriesName;
                    if (series.equals("")) {
                        seriesName = Dashboard.SERIES;
                    } else {
                        seriesName = obj.getString(series);
                    }

                    if (mData.containsKey(seriesName)) {
                        List<HashMap<String, Object>> dataSet = mData.get(seriesName);
                        dataSet.add(entry);
                    } else {
                        List<HashMap<String, Object>> dataSet = new ArrayList<>();
                        dataSet.add(entry);
                        mData.put(seriesName, dataSet);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isJsonException = true;
                    break;
                }
            }

            // Standardize datetime(millisecond)
            for (String key : mData.keySet()) {
                List<HashMap<String, Object>> dataSet = mData.get(key);
                Collections.sort(dataSet, new Comparator<HashMap<String, Object>>() {
                    @Override
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        Long f1 = (Long) o1.get(X_MILLI_SEC);
                        Long f2 = (Long) o2.get(X_MILLI_SEC);
                        return f1.compareTo(f2);
                    }
                });

                long firstTime = (long) dataSet.get(0).get(X_MILLI_SEC);
                long lastTime = (long) dataSet.get(dataSet.size() - 1).get(X_MILLI_SEC);

                if (minTime == 0 || firstTime < minTime) {
                    minTime = firstTime;
                }
                if (maxTime == 0 || lastTime > maxTime) {
                    maxTime = lastTime;
                }
            }

            // Normalize x value(DateTime).
            for (String key : mData.keySet()) {
                List<HashMap<String, Object>> dataSet = mData.get(key);
                for (HashMap<String, Object> entry : dataSet) {
                    float normalizedX;
                    if(minTime.equals(maxTime)){
                        normalizedX = 0.5f;
                    } else {
                        long xmsec = (long) entry.get(X_MILLI_SEC);
                        normalizedX = (float) (xmsec - minTime) / (maxTime - minTime);
                    }
                    entry.put(NORMALIZED_X, normalizedX);
                }
            }
            for (String key : mData.keySet()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                List<HashMap<String, Object>> dataSet = mData.get(key);
                for (HashMap<String, Object> entry : dataSet) {
                    float xVal = (float) entry.get(NORMALIZED_X);
                    float yVal = (float) entry.get(Y);
                    entries.add(new BarEntry(xVal, yVal));
                    numBars++;
                }
                BarDataSet set = new BarDataSet(entries, key);
                int[] color = mainActivity.getChartColor(numSeries);
                set.setColors(color, mainActivity.getContext());
                mBarData.addDataSet(set);
                numSeries++;
            }
        } else if(xAxisType.equals("category")) {
            // TODO: Refactoring!!!
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String label = obj.getString(xAxis);
                    float y = Float.parseFloat(obj.getString(yAxis));

                    xLabels.add(label);

                    // Create a HashMap of an entry.
                    HashMap<String, Object> entry = new HashMap<>();
                    entry.put(X, i);
                    entry.put(Y, y);

                    if (mData.containsKey(Dashboard.SERIES)) {
                        List<HashMap<String, Object>> dataSet = mData.get(Dashboard.SERIES);
                        dataSet.add(entry);
                    } else {
                        List<HashMap<String, Object>> dataSet = new ArrayList<>();
                        dataSet.add(entry);
                        mData.put(Dashboard.SERIES, dataSet);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isJsonException = true;
                    break;
                }
            }

            numBars = mData.get(Dashboard.SERIES).size();
            float xMin = 0f;
            float xMax = numBars - 1f;

            // Normalize x value(DateTime).
            for (String key : mData.keySet()) {
                List<HashMap<String, Object>> dataSet = mData.get(key);
                for (HashMap<String, Object> entry : dataSet) {
                    float normalizedX;
                    if(xMin == xMax){
                        normalizedX = 0.5f;
                    } else {
                        float x = (int) entry.get(X);
                        normalizedX = (x - xMin) / (xMax - xMin);
                    }
                    entry.put(NORMALIZED_X, normalizedX);
                }
            }
            for (String key : mData.keySet()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                List<HashMap<String, Object>> dataSet = mData.get(key);
                for (HashMap<String, Object> entry : dataSet) {
                    float xVal = (float) entry.get(NORMALIZED_X);
                    float yVal = (float) entry.get(Y);
                    entries.add(new BarEntry(xVal, yVal));
                }
                BarDataSet set = new BarDataSet(entries, key);
                int[] color = mainActivity.getChartColor(numSeries);
                set.setColors(color, mainActivity.getContext());
                mBarData.addDataSet(set);
                numSeries++;
            }
        } else {
            isDateTime = false;
        }

        mItem.notifyWidgetChanged();
    }

    public void callback(JSONArray dataArray){
        this.setData(dataArray);
    }

    public String getQueryName(){
        return mVisualData.get(Dashboard.QUERY_NAME);
    }
}
