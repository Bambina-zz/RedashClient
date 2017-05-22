package com.example.hirono_mayuko.redashclient2.model;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by hirono-mayuko on 2017/05/22.
 */

public class DataHelper {
    private static final String ID = "id";

    public static void addRedashAsync(Realm realm, final String url, final String apiKey,
                                      final boolean isProxy, final String proxyUrl,
                                      final String proxyPortNumber) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Redash.create(realm, url, apiKey, isProxy, proxyUrl, proxyPortNumber);
            }
        });
    }

    public static void deleteRedashAsync(Realm realm, final long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Redash redash = realm.where(Redash.class).equalTo(ID, id).findFirst();
                if(redash == null) return;

                RealmList<Dashboard> dashboards = redash.getDashboards();
                if(dashboards.isManaged()){
                    dashboards.deleteAllFromRealm();
                }
                Redash.delete(realm, id);
            }
        });
    }

    public static void addDashboardAsync(Realm realm, final Dashboard dashboard) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dashboard.create(realm, dashboard);
            }
        });
    }

    public static void updateDashboardName(Realm realm, final long dashboardId, final String name){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm){
                Dashboard dashboard = realm.where(Dashboard.class)
                        .equalTo("id", dashboardId)
                        .findFirst();

                dashboard.setmName(name);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Original queries and Realm objects are automatically updated.
            }
        });
    }

    public static void deleteDashboardAsync(Realm realm, final long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dashboard.delete(realm, id);
            }
        });
    }
}
