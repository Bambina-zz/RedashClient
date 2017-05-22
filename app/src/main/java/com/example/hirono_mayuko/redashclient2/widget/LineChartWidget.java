package com.example.hirono_mayuko.redashclient2.widget;

import com.example.hirono_mayuko.redashclient2.ConvertDateFromString;
import com.example.hirono_mayuko.redashclient2.model.Dashboard;

import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.item.LineChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.model.Widget;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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

public class LineChartWidget extends Widget {
    public LineData mLineData;
    public Long maxTime = 0L;
    public Long minTime = 0L;
    private HashMap<String, String> mVisualData;
    private MainActivity mainActivity;
    private LineChartWidgetItem mItem;
    private HashMap<String, List<HashMap<String, Object>>> mData = new HashMap<>();
    private static final String X_MILLI_SEC = "xmsec";
    private static final String LABEL = "label";
    private static final String NORMALIZED_X = "normalizedX";
    private static final String Y = "y";

    public LineChartWidget(HashMap<String, String> visualData, MainActivity activity, LineChartWidgetItem item){
        mVisualData = visualData;
        mainActivity = activity;
        mItem = item;
        mainActivity.queryData(mVisualData.get(Dashboard.QUERY_ID), this);
    }

    public void setData(JSONArray dataArray){
        // Get axis information.
        String xAxis = mVisualData.get(Dashboard.X_AXIS);
        String yAxis = mVisualData.get(Dashboard.Y_AXIS);
        // TODO: DashboardResponse isn't able to parse yAxis correctly.
        // TODO: For debugging, select iprosDemo flavor.
        // TODO: String yAxis = "count";
        String series = mVisualData.get(Dashboard.SERIES);

        // TODO: In this function xAxisType is supposed to be "datetime".
        try {
            for(int i=0; i<dataArray.length(); i++){
                JSONObject obj = dataArray.getJSONObject(i);
                String x = obj.getString(xAxis);
                String y = obj.getString(yAxis);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Long xMilliSec = ConvertDateFromString.parse(x, format).getTime();

                // Create a HashMap of an entry.
                HashMap<String, Object> entry = new HashMap<>(3);
                entry.put(LABEL, x);
                entry.put(X_MILLI_SEC, xMilliSec);
                entry.put(Y, Float.parseFloat(y));

                String groupName;
                if(series.equals("")){
                    groupName = Dashboard.SERIES;
                } else {
                    groupName = obj.getString(series);
                }

                if(mData.containsKey(groupName)){
                    mData.get(groupName).add(entry);
                } else {
                    List<HashMap<String, Object>> list = new ArrayList<>();
                    list.add(entry);
                    mData.put(groupName, list);
                }
            }

            // Sort entries and get maxTime and minTime.
            for(String key: mData.keySet()){
                List<HashMap<String, Object>> entries = mData.get(key);
                Collections.sort(entries, new Comparator<HashMap<String, Object>>(){
                    @Override
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        Long l1 = (Long) o1.get(X_MILLI_SEC);
                        Long l2 = (Long) o2.get(X_MILLI_SEC);
                        return l1.compareTo(l2);
                    }
                });
                Long firstTime = (Long) entries.get(0).get(X_MILLI_SEC);
                Long lastTime = (Long) entries.get(entries.size()-1).get(X_MILLI_SEC);

                if(minTime == 0 || minTime > firstTime){
                    minTime = firstTime;
                }

                if(maxTime == 0 || maxTime < lastTime){
                    maxTime = lastTime;
                }
            }

            // Normalize x value(DateTime). Like (xmsec - min) / (max - min).
            for(String key: mData.keySet()){
                List<HashMap<String, Object>> entries = mData.get(key);
                for(HashMap<String, Object> entry: entries){
                    Long xmsec = (Long) entry.get(X_MILLI_SEC);
                    float normalizedX =  (float) (xmsec - minTime) / (maxTime - minTime);
                    entry.put(NORMALIZED_X, normalizedX);
                }
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            int index = 0;
            for(String groupName: mData.keySet()){
                List<HashMap<String, Object>> group = mData.get(groupName);
                ArrayList<Entry> list = new ArrayList<>();
                for(HashMap<String, Object> entry:group){
                    list.add(new Entry((Float) entry.get(NORMALIZED_X), (Float) entry.get(Y)));
                }
                LineDataSet a = new LineDataSet(list, groupName);
                int[] color = mainActivity.getChartColor(index);
                a.setColors(color, mainActivity.getContext());
                a.setDrawCircles(false);
                dataSets.add(a);
                index++;
            }
            mLineData = new LineData(dataSets);
        } catch (JSONException e){
            e.printStackTrace();
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
