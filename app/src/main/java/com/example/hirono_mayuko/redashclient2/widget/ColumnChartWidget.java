package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.ConvertDateFromString;
import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.item.ColumnChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.model.Widget;
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

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class ColumnChartWidget extends Widget {
    public BarData mBarData;
    public Long minTime = 0L;
    public Long maxTime = 0L;
    public boolean isFailed = false;
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private ColumnChartWidgetItem mItem;
    private static final String X_MILLI_SEC = "xmsec";
    private static final String LABEL = "label";
    private static final String NORMALIZED_X = "normalizedX";
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
        String series = mVisualData.get(Dashboard.SERIES);
        HashMap<String, List<HashMap<String, Object>>> mData = new HashMap<>();

        for(int i=0; i < dataArray.length(); i++){
            try {
                JSONObject obj = dataArray.getJSONObject(i);

                String x = obj.getString(xAxis);
                // TODO: Data type of "2017-5-26" and "2017-05-23T03:15:00+00:00" is datetime.
                SimpleDateFormat format;
                if(x.contains("T")){
                    format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+'");
                } else {
                    format = new SimpleDateFormat("yyyy-MM-dd");
                }
                long xMillisec = ConvertDateFromString.parse(x, format).getTime();
                float y = Float.parseFloat(obj.getString(yAxis));

                // Create a HashMap of an entry.
                HashMap<String, Object> entry = new HashMap<>();
                entry.put(LABEL, x);
                entry.put(X_MILLI_SEC, xMillisec);
                entry.put(Y, y);

                String seriesName;
                if(series.equals("")){
                    seriesName = Dashboard.SERIES;
                } else {
                    seriesName = obj.getString(series);
                }
                if(mData.containsKey(seriesName)){
                    List<HashMap<String, Object>> dataSet = mData.get(seriesName);
                    dataSet.add(entry);
                } else {
                    List<HashMap<String, Object>> dataSet = new ArrayList<>();
                    dataSet.add(entry);
                    mData.put(seriesName, dataSet);
                }
            } catch (JSONException e){
                e.printStackTrace();
                isFailed = true;
                break;
            }
        }

        // Standardize datetime(millisecond)
        for(String key: mData.keySet()){
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
            long lastTime = (long) dataSet.get(dataSet.size()-1).get(X_MILLI_SEC);

            if(minTime == 0 || firstTime < minTime){
                minTime = firstTime;
            }
            if(maxTime == 0 || lastTime > maxTime){
                maxTime = lastTime;
            }
        }

        // Normalize x value(DateTime).
        for(String key: mData.keySet()){
            List<HashMap<String, Object>> dataSet = mData.get(key);
            for(HashMap<String, Object> entry: dataSet){
                long xmsec = (long) entry.get(X_MILLI_SEC);
                float normalizedX = (float) (xmsec - minTime) / (maxTime - minTime);
                entry.put(NORMALIZED_X, normalizedX);
            }
        }

        mBarData = new BarData();
        int index = 0;
        for(String key: mData.keySet()){
            ArrayList<BarEntry> entries = new ArrayList<>();
            List<HashMap<String, Object>> dataSet = mData.get(key);
            for(HashMap<String, Object> entry: dataSet){
                float xVal = (float) entry.get(NORMALIZED_X);
                float yVal = (float) entry.get(Y);
                entries.add(new BarEntry(xVal, yVal));
            }

            BarDataSet set = new BarDataSet(entries, key);
            int[] color = mainActivity.getChartColor(index);
            set.setColors(color, mainActivity.getContext());
            index++;
            mBarData.addDataSet(set);
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
