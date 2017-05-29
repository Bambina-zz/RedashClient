package com.bambina.dashboardViewer.model;


/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class DashboardResponse {

    private boolean isArchived;
    private boolean isSuccessful;
    private String mErrorMessage;
    private Dashboard mDashboard;

    public DashboardResponse(){
        mDashboard = new Dashboard();
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

    public Dashboard getmDashboard() {
        return mDashboard;
    }

    public void setmDashboard(Dashboard mDashboard) {
        this.mDashboard = mDashboard;
    }

}
