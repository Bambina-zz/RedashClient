package com.bambina.dashboardViewer;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bambina.dashboardViewer.model.Dashboard;
import com.bambina.dashboardViewer.model.DashboardResponse;
import com.bambina.dashboardViewer.model.Redash;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by hirono-mayuko on 2017/05/01.
 */

public class DashboardDataLoader extends AsyncTaskLoader<DashboardResponse> {
    private String mDashboardName;
    private long redashId;
    private String redashApiKey;
    private String redashDomain;
    private static final String DASHBOARD_API = "%1$s/api/dashboards/%2$s?api_key=%3$s";
    private static final String IS_ARCHIVED = "is_archived";
    private static final String NAME = "name";
    private static final String MESSAGE = "message";

    public DashboardDataLoader(Context context, String dashboardName){
        super(context);
        Redash redash = RedashApplication.getmRedash();
        redashId = redash.getId();
        redashApiKey = redash.getApiKey();
        redashDomain = redash.getUrl();
        this.mDashboardName = dashboardName;
    }

    @Override
    public DashboardResponse loadInBackground(){
        String url = String.format(DASHBOARD_API, redashDomain, mDashboardName, redashApiKey);
        OkHttpClient c = RedashApplication.getmClient();
        Request req = new Request.Builder().url(url).build();
        DashboardResponse dashboardResponse = new DashboardResponse();
        Dashboard dashboard = dashboardResponse.getmDashboard();
        dashboard.setmUrl(mDashboardName);
        try {
            Response response = c.newCall(req).execute();
            dashboardResponse.setSuccessful(response.isSuccessful());
            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            if(dashboardResponse.isSuccessful()){
                dashboardResponse.setArchived(json.getBoolean(IS_ARCHIVED));
                dashboard.setmName(json.getString(NAME));
                dashboard.setmRedashId(redashId);
            } else {
                dashboardResponse.setErrorMessage(json.getString(MESSAGE));
            }
            response.close();
        } catch (Exception e) {
            dashboardResponse.setSuccessful(false);
            e.printStackTrace();
        }
        return dashboardResponse;
    }
}
