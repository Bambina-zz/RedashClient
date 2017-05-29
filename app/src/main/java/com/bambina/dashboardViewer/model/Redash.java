package com.bambina.dashboardViewer.model;

import android.support.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class Redash extends RealmObject {
    @PrimaryKey
    public long id;

    @Required
    private String url;

    @Required
    private String apiKey;

    private boolean isProxy;

    @Nullable
    private String proxyUrl;

    @Nullable
    private String proxyPortNumber;

    @Nullable
    private RealmList<Dashboard> dashboards;

    private static final String ID = "id";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean proxy) {
        isProxy = proxy;
    }

    @Nullable
    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(@Nullable String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    @Nullable
    public String getProxyPortNumber() {
        return proxyPortNumber;
    }

    public void setProxyPortNumber(@Nullable String proxyPortNumber) {
        this.proxyPortNumber = proxyPortNumber;
    }

    @Nullable
    public RealmList<Dashboard> getDashboards() {
        return dashboards;
    }

    public void setDashboards(@Nullable RealmList<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }

    static void create(Realm realm, String url, String apiKey,
                       boolean isProxy, String proxyUrl, String proxyPortNumber){
        Redash redash = realm.createObject(Redash.class);
        redash.setId(incrementId(realm));
        redash.setUrl(url);
        redash.setApiKey(apiKey);
        redash.setProxy(isProxy);
        if(isProxy){
            redash.setProxyUrl(proxyUrl);
            redash.setProxyPortNumber(proxyPortNumber);
        }
    }

    static void delete(Realm realm, long id){
        Redash redash = realm.where(Redash.class).equalTo(ID, id).findFirst();
        if(redash == null) return;

        redash.deleteFromRealm();
    }

    private static long incrementId(Realm realm){
        Number n = realm.where(Redash.class).max(ID);
        long id = (n != null) ? n.longValue() + 1 : 0;
        return id;
    }
}
