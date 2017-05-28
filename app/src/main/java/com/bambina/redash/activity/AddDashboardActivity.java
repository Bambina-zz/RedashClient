package com.bambina.redash.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bambina.redash.DashboardDataLoader;
import com.bambina.redash.R;
import com.bambina.redash.RedashApplication;
import com.bambina.redash.model.Dashboard;
import com.bambina.redash.model.DashboardResponse;
import com.bambina.redash.model.Redash;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by hirono-mayuko on 2017/05/01.
 */

public class AddDashboardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<DashboardResponse> {
    private Context mContext;
    private Realm mRealm;
    private DashboardDataLoader mLoader = null;
    private EditText mDashboardView;
    private View mProgressView;
    private View mDashboardFormView;
    private int selectedRedash;
    private int selectedDashboard;
    private static final String SELECTED_REDASH = "selectedRedash";
    private static final String SELECTED_DASHBOARD = "selectedDashboard";
    private static final String DASHBOARD = "dashboard";
    private static final String REDASHID = "mRedashId";
    private static final String URL = "mUrl";
    private static final String ID = "id";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dashboard);
        mContext = this;
        mRealm = Realm.getDefaultInstance();
        selectedRedash = getIntent().getIntExtra(SELECTED_REDASH, 0);
        selectedDashboard = getIntent().getIntExtra(SELECTED_DASHBOARD, 0);

        mDashboardView = (EditText) findViewById(R.id.dashboard);
        mDashboardView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mDashboardFormView = findViewById(R.id.dashboard_register_form);
        mProgressView = findViewById(R.id.register_progress);
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister(){
        if(mLoader != null) return;

        mDashboardView.setError(null);
        String dashboard = mDashboardView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(dashboard)) {
            mDashboardView.setError(getString(R.string.error_invalid_dashboard));
            focusView = mDashboardView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Bundle b = new Bundle();
            b.putString(DASHBOARD, dashboard);
            getLoaderManager().initLoader(0, b, this).forceLoad();
        }
    }

    @Override
    public Loader<DashboardResponse> onCreateLoader(int id, Bundle args){
        mLoader = new DashboardDataLoader(mContext, args.getString(DASHBOARD));
        mLoader.forceLoad();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<DashboardResponse> loader, DashboardResponse response){
        if(response.isSuccessful()){
            if(response.isArchived()){
                mDashboardView.setError(getResources().getString(R.string.dashboard_archived_error));
            } else {
                long redashId = RedashApplication.getmRedash().getId();
                Dashboard dashboard = response.getmDashboard();
                Dashboard dashboardSaved = mRealm.where(Dashboard.class)
                        .equalTo(REDASHID,redashId)
                        .equalTo(URL, dashboard.getmUrl())
                        .findFirst();
                mRealm.beginTransaction();
                if(dashboardSaved == null){
                    Redash redash = mRealm.where(Redash.class).equalTo(ID, redashId).findFirst();
                    RealmList<Dashboard> l = redash.getDashboards();
                    dashboard.setId(incrementId(Dashboard.class));
                    l.add(dashboard);
                    mRealm.insert(redash);
                } else {
                    dashboardSaved.setmName(dashboard.getName());
                    mRealm.insertOrUpdate(dashboardSaved);
                }
                mRealm.commitTransaction();
                goBackMainActivity();
            }
        } else {
            if(response.getErrorMessage() != null){
                mDashboardView.setError(response.getErrorMessage());
            } else {
                mDashboardView.setError(getResources().getString(R.string.dashboard_existence_error));
            }
        }

        mLoader = null;
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<DashboardResponse> loader){}

    public void goBackMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(SELECTED_REDASH, selectedRedash);
        setResult(RESULT_OK, i);
        finish();
    }

    public long incrementId(Class c){
        Number n = mRealm.where(c).max(ID);
        long id = (n != null) ? n.longValue() + 1 : 0;
        return id;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(SELECTED_DASHBOARD, selectedDashboard);
        setResult(RESULT_CANCELED, i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
