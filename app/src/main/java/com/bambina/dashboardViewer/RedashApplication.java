package com.bambina.dashboardViewer;

import android.app.Application;

import com.bambina.dashboardViewer.model.Redash;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class RedashApplication extends Application {

    private static OkHttpClient mClient;
    private static Redash mRedash;

    @Override
    public void onCreate(){
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration c = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(c);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    public static OkHttpClient getmClient() {
        return mClient;
    }

    public static void setmClient(OkHttpClient mClient) {
        RedashApplication.mClient = mClient;
    }

    public static Redash getmRedash() {
        return mRedash;
    }

    public static void setmRedash(Redash mRedash) {
        RedashApplication.mRedash = mRedash;
    }
}
