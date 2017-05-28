package com.bambina.redash.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class Dashboard extends RealmObject {

    /*
        Structure of mdData

        { “queryId+visualId”:
            {
                  “visualId”:””,
                "visualName":"",
                   “queryId”:””,
                  “queryAPI”:””,
                 “queryName”:””,
                “visualType”:””,
                 “chartType”:”"
                    “series”:”"
                     "xAxis":"",
                     "yAxis":"",
                 "xAxisType":"",
                  "yAxisTpe":"",
                "xAxisTitle":"",
                "yAxisTitle":"",
            "counterColName":"",
            },
          ....
        }
    */

    // TODO: Create a Widget class, it's gonna be extended by PieWidget, LineWidget etc.
    private static final String NAME = "name";
    private static final String WIDGETS = "widgets";
    private static final String VISUALIZATION = "visualization";
    private static final String ID = "id";
    private static final String QUERY = "query";
    private static final String TYPE = "type";
    private static final String OPTIONS = "options";
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String GLOBAL_SERIES_TYPE = "globalSeriesType";
    private static final String COLUMN_MAPPING = "columnMapping";
    private static final String X = "x";
    private static final String Y = "y";

    public static final String CHART = "CHART";
    public static final String COUNTER = "COUNTER";
    public static final String WIDGET_ID = "widgetID";
    public static final String VISUAL_ID = "visualID";
    public static final String VISUAL_NAME = "visualName";
    public static final String QUERY_ID = "queryID";
    public static final String QUERY_NAME = "queryName";
    public static final String VISUAL_TYPE = "visualType";
    public static final String CHART_TYPE = "chartType";
    public static final String SERIES = "series";
    public static final String X_AXIS = "xAxis";
    public static final String Y_AXIS = "yAxis";
    public static final String IS_MULTIPLE_Y_AXIS = "isMultipleYAxis";
    public static final String X_AXIS_TYPE = "xAxisType";
    public static final String Y_AXIS_TYPE = "yAxisType";
    public static final String X_AXIS_TITLE = "xAxisTitle";
    public static final String Y_AXIS_TITLE = "yAxisTitle";
    public static final String COUNTER_COL_NAME = "counterColName";

    @PrimaryKey
    private long id;

    @Required
    private String mUrl;

    private String mName;

    private long mRedashId;

    @Ignore
    public HashMap<String, HashMap<String, String>> mData = new HashMap<>();
    @Ignore
    public ArrayList<HashMap<String, String>> mVisualList = new ArrayList<>();
    @Ignore
    public List<String> mWidgetIds = new ArrayList<>();

    public Dashboard(){}

    public void setData(JSONObject json){
        try {
            mName = json.getString(NAME);
            JSONArray widgets = json.getJSONArray(WIDGETS);
            for(int i=0; i < widgets.length(); i++){
                JSONArray widget_row = widgets.getJSONArray(i);
                for(int n=0; n < widget_row.length(); n++){
                    JSONObject widget = widget_row.getJSONObject(n);
                    boolean isVisualization = widget.has(VISUALIZATION);
                    if(isVisualization){
                        addWidget(widget);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addWidget(JSONObject widget){
        try {
            JSONObject visualization = widget.getJSONObject(VISUALIZATION);
            String visualId = visualization.getString(ID);
            String visualName = visualization.getString(NAME);
            JSONObject query = visualization.getJSONObject(QUERY);
            String queryId = query.getString(ID);
            String queryName = query.getString(NAME);
            String visualType = visualization.getString(TYPE);
            String chartType = "";
            String series = "";
            String xAxis = "";
            String yAxis = "";
            String isMultipleYAxis = "false";
            String xAxisType = "";
            String yAxisType = "";
            String xAxisTitle = "";
            String yAxisTitle = "";
            if(visualType.equals(CHART)){
                JSONObject options = visualization.getJSONObject(OPTIONS);

                // X axis options
                if(options.has(X_AXIS)){
                    Object xOptions = options.get(X_AXIS);
                    if(xOptions instanceof JSONArray){
                        if(((JSONArray) xOptions).getJSONObject(0).has(TYPE)){
                            xAxisType = ((JSONArray) xOptions).getJSONObject(0).getString(TYPE);
                        }
                        if(((JSONArray) xOptions).getJSONObject(0).has(TITLE)){
                            xAxisTitle = ((JSONArray) xOptions).getJSONObject(0).getJSONObject(TITLE).getString(TEXT);
                        }
                    } else {
                        if(((JSONObject) xOptions).has(TYPE)){
                            xAxisType = ((JSONObject) xOptions).getString(TYPE);
                        }
                        if(((JSONObject) xOptions).has(TITLE)){
                            xAxisTitle = ((JSONObject) xOptions).getJSONObject(TITLE).getString(TEXT);
                        }
                    }
                }

                // Y axis options
                // TODO: It doesn't work, if few columns are designated as Y axis.
                if(options.has(Y_AXIS)){
                    Object yOptions = options.get(Y_AXIS);
                    if(yOptions instanceof JSONArray){
                        if(((JSONArray) yOptions).getJSONObject(0).has(TYPE)) {
                            yAxisType = ((JSONArray) yOptions).getJSONObject(0).getString(TYPE);
                        }
                        if(((JSONArray) yOptions).getJSONObject(0).has(TITLE)){
                            yAxisTitle = ((JSONArray) yOptions).getJSONObject(0).getJSONObject(TITLE).getString(TEXT);
                        }
                    } else {
                        if(((JSONObject) yOptions).has(TYPE)){
                            yAxisType = ((JSONObject) yOptions).getString(TYPE);
                        }
                        if(((JSONObject) yOptions).has(TITLE)){
                            yAxisTitle = ((JSONObject) yOptions).getJSONObject(TITLE).getString(TEXT);
                        }
                    }
                }

                chartType = options.getString(GLOBAL_SERIES_TYPE);

                JSONObject columnMapping = options.getJSONObject(COLUMN_MAPPING);
                Iterator<String> columns = columnMapping.keys();
                while(columns.hasNext()){
                    String key = columns.next();
                    if(columnMapping.getString(key).equals(X)){
                        xAxis = key;
                    } else if(columnMapping.getString(key).equals(Y)){
                        if(isMultipleYAxis.equals("false")){
                            yAxis = key;
                            isMultipleYAxis = "true";
                        } else {
                            yAxis += ",";
                            yAxis += key;
                        }
                    } else if(columnMapping.getString(key).equals(SERIES)){
                        series = key;
                    }
                }
            }

            String counterColName = "";
            if(visualType.equals(COUNTER)){
                JSONObject options = visualization.getJSONObject(OPTIONS);
                counterColName = options.getString(COUNTER_COL_NAME);
            }

            HashMap<String, String> visualData = new HashMap<>();
            visualData.put(VISUAL_ID, visualId);
            visualData.put(VISUAL_NAME, visualName);
            visualData.put(QUERY_ID, queryId);
            visualData.put(QUERY_NAME, queryName);
            visualData.put(VISUAL_TYPE, visualType);
            visualData.put(CHART_TYPE, chartType);
            visualData.put(SERIES, series);
            visualData.put(X_AXIS, xAxis);
            visualData.put(Y_AXIS, yAxis);
            visualData.put(IS_MULTIPLE_Y_AXIS, isMultipleYAxis);
            visualData.put(X_AXIS_TYPE, xAxisType);
            visualData.put(Y_AXIS_TYPE, yAxisType);
            visualData.put(X_AXIS_TITLE, xAxisTitle);
            visualData.put(Y_AXIS_TITLE, yAxisTitle);
            visualData.put(COUNTER_COL_NAME, counterColName);
            mData.put(queryId + visualId, visualData);

            HashMap<String, String> visualInfo = new HashMap<>();
            visualInfo.put(QUERY_NAME, queryName);
            visualInfo.put(QUERY_ID, queryId);
            visualInfo.put(WIDGET_ID, queryId+visualId);
            visualInfo.put(VISUAL_TYPE, visualType);
            visualInfo.put(CHART_TYPE, chartType);
            mVisualList.add(visualInfo);

            mWidgetIds.add(queryId + visualId);

            /*System.out.println("======= visual info ========");
            System.out.println("visualId: " + visualId);
            System.out.println("visualName: " + visualName);
            System.out.println("queryId: " + queryId);
            System.out.println("queryName: " + queryName);
            System.out.println("visualType: " + visualType);
            System.out.println("chartType: " + chartType);
            System.out.println("series: " + series);
            System.out.println("xAxis: " + xAxis);
            System.out.println("yAxis: " + yAxis);
            System.out.println("xAxisType: " + xAxisType);
            System.out.println("yAxisType " + yAxisType);
            System.out.println("xAxisTitle: " + xAxisTitle);
            System.out.println("yAxisTitle: " + yAxisTitle);
            System.out.println("counterColName: " + counterColName);*/

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getName(){
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public long getmRedashId() {
        return mRedashId;
    }

    public void setmRedashId(long mRedashId) {
        this.mRedashId = mRedashId;
    }

    public List<String> getWidgetIds(){
        return mWidgetIds;
    }

    static void create(Realm realm, String url, String name, long redashId){
        Dashboard dashboard = realm.createObject(Dashboard.class);
        dashboard.setId(incrementId(realm));
        dashboard.setmUrl(url);
        dashboard.setmName(name);
        dashboard.setmRedashId(redashId);
    }

    static void create(Realm realm, Dashboard dashboard){
        dashboard.setId(incrementId(realm));
        realm.insert(dashboard);
    }

    static void delete(Realm realm, long id){
        Dashboard dashboard = realm.where(Dashboard.class).equalTo(ID, id).findFirst();
        if (dashboard == null) return;

        dashboard.deleteFromRealm();
    }

    private static long incrementId(Realm realm){
        Number n = realm.where(Dashboard.class).max(ID);
        long id = (n != null) ? n.longValue() + 1 : 0;
        return id;
    }
}
