package com.example.hirono_mayuko.redashclient2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hirono_mayuko.redashclient2.RedashApplication;
import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.model.Redash;
import com.example.hirono_mayuko.redashclient2.model.Widget;
import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.item.ColumnChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.item.CounterWidgetItem;
import com.example.hirono_mayuko.redashclient2.item.LineChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.item.PieChartWidgetItem;
import com.example.hirono_mayuko.redashclient2.item.TableWidgetItem;
import com.github.mikephil.charting.utils.Utils;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Realm mRealm;
    private RealmResults<Redash> redashes;
    private RealmResults<Dashboard> dashboards;

    // Re:dash API
    private String redash_api_key = "";
    private String redash_domain = "";

    public Handler mHandler = new Handler();
    public Dashboard mDashboard;

    // Navigation view
    private TextView mNavigationHeaderText;
    public NavigationView mNavigationView;
    private DrawerLayout drawer;
    private static Integer selectedRedash = 0;
    private static Integer selectedDashboard = 0;
    private static final String SELECTED_REDASH = "selectedRedash";
    private static final String SELECTED_DASHBOARD = "selectedDashboard";
    private boolean isVisibleRedashMenu;

    // Recycler view UI references.
    private LinearLayout mContentMain;
    private GroupAdapter mAdapter;

    // Re:dash API format
    private static final String DASHBOARD_API = "%1$s/api/dashboards/%2$s?api_key=%3$s";
    private static final String QUERY_API = "%1$s/api/queries/%2$s/results.json?api_key=%3$s";

    // Re:dash visual types
    public static final String CHART = "CHART";
    public static final String COUNTER = "COUNTER";

    // Re:dash chart types
    public static final String PIE = "pie";
    public static final String LINE = "line";
    public static final String COLUMN = "column";

    private static final String QUERY_RESULT = "query_result";
    private static final String ROWS = "rows";
    private static final String DATA = "data";
    private static final String IS_ARCHIVED = "is_archived";
    private static final String MESSAGE = "message";

    public static final int[] CHART_COLOR = new int[] { R.color.colorBlue, R.color.colorGreen, R.color.colorYellow, R.color.colorRed};
    private static final int INITIAL_INDEX = 0;
    private static final int INTENT_ADD_REDASH = 1;
    private static final int INTENT_ADD_DASHBOARD = 2;
    private static final int INTENT_MANAGE_REDASH = 3;
    private static final int INTENT_MANAGE_DASHBOARD = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentMain = (LinearLayout) findViewById(R.id.content_main);
        mRealm = Realm.getDefaultInstance();

        selectedRedash = getIntent().getIntExtra(SELECTED_REDASH, INITIAL_INDEX);
        selectedDashboard = getIntent().getIntExtra(SELECTED_DASHBOARD, INITIAL_INDEX);

        // For MPAndroidChart.
        Utils.init(getContext());

        // UI references.
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        mNavigationHeaderText = (TextView) headerView.findViewById(R.id.env_redash);
        mNavigationView.setNavigationItemSelectedListener(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GroupAdapter();
        mRecyclerView.setAdapter(mAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Check if Redash object exists in Realm.
        redashes = mRealm.where(Redash.class).findAll();
        if(redashes.size() <= 0){
            // Show Re:dash registration page.
            Intent i = new Intent(this, RegisterActivity.class);
            startActivityForResult(i, INTENT_ADD_REDASH);
        } else {
            if(savedInstanceState == null) {
                initVIew();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putInt(SELECTED_REDASH, selectedRedash);
        savedInstanceState.putInt(SELECTED_DASHBOARD, selectedDashboard);
    }

    @Override
    public void onRestoreInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        selectedRedash = outState.getInt(SELECTED_REDASH);
        selectedDashboard = outState.getInt(SELECTED_DASHBOARD);
        initVIew();
    }

    private void initVIew(){
        initializeDrawerMenu(selectedRedash, selectedDashboard);
        setConnectionProperties(selectedRedash);

        mAdapter.clear();
        if (dashboards.isEmpty()) {
            TextView tv = (TextView) findViewById(R.id.noDashboard);
            tv.setVisibility(View.VISIBLE);
        } else {
            // Load the dashboard data.
            loadDashboardData(selectedDashboard);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_ADD_REDASH){
            switch (resultCode){
                case RESULT_OK:
                    selectedRedash = redashes.size()-1;
                    initializeDrawerMenu(selectedRedash, INITIAL_INDEX);
                    setConnectionProperties(selectedRedash);
                    mAdapter.clear();
                    // Load the dashboard data.
                    if(!dashboards.isEmpty()){
                        loadDashboardData(INITIAL_INDEX);
                    }
                    break;
                case RESULT_CANCELED:
                    int index = data.getIntExtra(SELECTED_REDASH, 0);
                    selectMenuItem(false, index);
                    break;
            }
        } else if (requestCode == INTENT_ADD_DASHBOARD) {
            switch (resultCode){
                case RESULT_OK:
                    drawer.closeDrawer(GravityCompat.START);
                    findViewById(R.id.noDashboard).setVisibility(View.GONE);
                    selectedRedash = data.getIntExtra(SELECTED_REDASH, 0);
                    initializeDrawerMenu(selectedRedash, dashboards.size()-1);
                    mAdapter.clear();
                    loadDashboardData(dashboards.size()-1);
                    break;
                case RESULT_CANCELED:
                    int index = data.getIntExtra(SELECTED_DASHBOARD, 0);
                    selectMenuItem(true, index);
                    break;
            }
        } else if (requestCode == INTENT_MANAGE_REDASH){
            switch(resultCode){
                case RESULT_OK:
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case RESULT_CANCELED:
                    selectMenuItem(false, selectedRedash);
                    break;
            }
        } else if (requestCode == INTENT_MANAGE_DASHBOARD){
            switch(resultCode){
                case RESULT_OK:
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case RESULT_CANCELED:
                    selectMenuItem(true, selectedDashboard);
                    selectMenuItem(false, selectedRedash);
                    break;
            }
        }
    }

    private void initializeDrawerMenu(int redashIndex, int dashboardIndex){
        Redash r = redashes.get(redashIndex);
        mNavigationHeaderText.setText(r.getUrl());
        dashboards = mRealm.where(Dashboard.class).equalTo("mRedashId", r.getId()).findAll();

        // Add them as the items for drawer menu.
        mNavigationView.getMenu().clear();
        addMenuRedashItems(redashIndex);
        addMenuDashboardItems(dashboardIndex);

        // Initializing drawer menu.
        isVisibleRedashMenu = false;
        mNavigationView.getMenu().setGroupVisible(R.id.group_redash, false);
        mNavigationHeaderText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_white_24px, 0);
        mNavigationHeaderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisibleRedashMenu){
                    mNavigationView.getMenu().setGroupVisible(R.id.group_redash, false);
                    mNavigationView.getMenu().setGroupVisible(R.id.group_dashboard, true);
                    if(dashboards.size() > 0){
                        selectMenuItem(true, selectedDashboard);
                    }
                    mNavigationHeaderText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_white_24px, 0);
                    isVisibleRedashMenu = false;
                } else {
                    mNavigationView.getMenu().setGroupVisible(R.id.group_redash, true);
                    mNavigationView.getMenu().setGroupVisible(R.id.group_dashboard, false);
                    selectMenuItem(false, selectedRedash);
                    mNavigationHeaderText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_white_24px, 0);
                    isVisibleRedashMenu = true;
                }
            }
        });
    }

    private void setConnectionProperties(int redashIndex){
        Redash r = redashes.get(redashIndex);
        redash_api_key = r.getApiKey();
        redash_domain = r.getUrl();
        OkHttpClient c;
        if (r.isProxy()) {
            String redash_proxy_address = r.getProxyUrl();
            int redash_proxy_port = Integer.parseInt(r.getProxyPortNumber());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(redash_proxy_address, redash_proxy_port));
            c = new OkHttpClient.Builder().proxy(proxy).build();
        } else {
            c = new OkHttpClient();
        }

        RedashApplication.setmRedash(r);
        RedashApplication.setmClient(c);
    }

    public void loadDashboardData(final int index){
        final MainActivity mainActivity = this;
        Dashboard dashboard = dashboards.get(index);
        String dashboardUrl = dashboard.getmUrl();
        String url = String.format(DASHBOARD_API, redash_domain, dashboardUrl, redash_api_key);
        Request r = new Request.Builder().url(url).build();
        Call call = RedashApplication.getmClient().newCall(r);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean isSuccessful = response.isSuccessful();
                String body = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(body);
                    boolean isArchived = json.getBoolean(IS_ARCHIVED);
                    if(!isSuccessful){
                        response.close();
                        String m = json.getString(MESSAGE);
                        showMsg(m, Color.RED);
                        return;
                    }
                    if(isArchived){
                        showMsg(getResources().getString(R.string.dashboard_archived), Color.RED);
                        removeDashboard(index);
                    }
                    mDashboard = new Dashboard();
                    mDashboard.setData(json);
                    List<String> widgetIds =  mDashboard.mWidgetIds;
                    List<Group> groups = new ArrayList<>();
                    for(String widgetId: widgetIds){
                        HashMap<String, String> visualData = mDashboard.mData.get(widgetId);
                        Item i = createItem(widgetId, visualData, mainActivity);
                        groups.add(i);
                    }
                    mAdapter.addAll(groups);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                response.close();
            }
        });
    }

    private Item createItem(String widgetId, HashMap<String, String> visualData, MainActivity mainActivity){
        String visualType = visualData.get(Dashboard.VISUAL_TYPE);
        String chartType = visualData.get(Dashboard.CHART_TYPE);
        Item i;
        if(visualType.equals(CHART)){
            if(chartType.equals(PIE)){
                i = new PieChartWidgetItem(widgetId, visualData, mainActivity);
            } else if(chartType.equals(LINE)){
                i = new LineChartWidgetItem(widgetId, visualData, mainActivity);
            } else {
                // chartType is COLUMN
                i = new ColumnChartWidgetItem(widgetId, visualData, mainActivity);
            }
        } else if(visualType.equals(COUNTER)){
            i = new CounterWidgetItem(widgetId, visualData, mainActivity);
        } else {
            // visualType is TABLE
            i = new TableWidgetItem(widgetId, visualData, mainActivity);
        }
        return i;
    }

    public void queryData(final String queryId, final Widget widget){
        String url = String.format(QUERY_API, redash_domain, queryId, redash_api_key);
        Request r = new Request.Builder().url(url).build();
        Call c = RedashApplication.getmClient().newCall(r);
        c.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(body);
                    JSONObject data = json.getJSONObject(QUERY_RESULT).getJSONObject(DATA);
                    JSONArray dataArray = data.getJSONArray(ROWS);
                    widget.callback(dataArray);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void notifyItemChanged(final Item item){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                item.notifyChanged();
            }
        });
        mHandler.post(t);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(isVisibleRedashMenu){
            if(id == redashes.size()){
                Intent i = new Intent(this, RegisterActivity.class);
                i.putExtra(SELECTED_REDASH, selectedRedash);
                startActivityForResult(i, INTENT_ADD_REDASH);
            } else if(id == redashes.size() + 1){
                Intent i = new Intent(this, ManageRedashActivity.class);
                i.putExtra(SELECTED_REDASH, selectedRedash);
                startActivityForResult(i, INTENT_MANAGE_REDASH);
            } else {
                mAdapter.clear();
                initializeDrawerMenu(id, INITIAL_INDEX);
                setConnectionProperties(id);
                if(!dashboards.isEmpty()){
                    loadDashboardData(INITIAL_INDEX);
                }
                selectedRedash = id;
                selectedDashboard = INITIAL_INDEX;
            }
        } else {
            if(id == dashboards.size()){
                Intent i = new Intent(this, AddDashboardActivity.class);
                i.putExtra(SELECTED_REDASH, selectedRedash);
                i.putExtra(SELECTED_DASHBOARD, selectedDashboard);
                startActivityForResult(i, INTENT_ADD_DASHBOARD);
            } else if(id == dashboards.size() + 1){
                Intent i = new Intent(this, ManageDashboardActivity.class);
                i.putExtra(SELECTED_REDASH, selectedRedash);
                i.putExtra(SELECTED_DASHBOARD, selectedDashboard);
                startActivityForResult(i, INTENT_MANAGE_DASHBOARD);
            } else {
                mAdapter.clear();
                loadDashboardData(id);
                selectedDashboard = id;
                drawer.closeDrawer(GravityCompat.START);
            }
        }

        return true;
    }

    public void addMenuDashboardItems(@Nullable final Integer selected){
        Menu menu = mNavigationView.getMenu();
        int i = 0;
        if(dashboards.size() > 0){
            for(Dashboard dashboard: dashboards){
                String name = dashboard.getName();
                MenuItem menuItem = menu.add(R.id.group_dashboard, i, Menu.NONE, name);
                menuItem.setIcon(R.drawable.ic_compact_black_24dp).setCheckable(true);
                if(selected != null && selected == i) menuItem.setChecked(true);
                i++;
            }
            if(selected != null){
                menu.getItem(redashes.size() + 2 + selected).setChecked(true);
                selectedDashboard = selected;
            }
        }
        MenuItem addDashboardItem = menu.add(R.id.group_dashboard, i, Menu.NONE, R.string.add_dashboard);
        addDashboardItem.setIcon(R.drawable.ic_add_black_24dp).setCheckable(true);
        MenuItem settingItem = menu.add(R.id.group_dashboard, ++i, Menu.NONE, R.string.manage_dashboard);
        settingItem.setIcon(R.drawable.ic_settings_24dp).setCheckable(true);
    }

    public void addMenuRedashItems(@Nullable final Integer selected){
        Menu menu = mNavigationView.getMenu();
        int i = 0;
        for(Redash redash: redashes){
            String url = redash.getUrl();
            MenuItem menuItem = menu.add(R.id.group_redash, i, Menu.NONE, url);
            menuItem.setIcon(R.drawable.ic_insert_chart_24dp).setCheckable(true);
            if(selected != null && selected == i) menuItem.setChecked(true);
            i++;
        }
        MenuItem addRedashItem = menu.add(R.id.group_redash, i, Menu.NONE, R.string.add_redash);
        addRedashItem.setIcon(R.drawable.ic_add_black_24dp).setCheckable(true);
        MenuItem settingItem = menu.add(R.id.group_redash, ++i, Menu.NONE, R.string.manage_redash);
        settingItem.setIcon(R.drawable.ic_settings_24dp).setCheckable(true);
        if(selected != null){
            menu.getItem(selected).setChecked(true);
            selectedRedash = selected;
        }
    }

    public void selectMenuItem(boolean isDashboard, Integer itemIndex){
        if(isDashboard){
            if(redashes.size() == 0) return;
            itemIndex += redashes.size() + 2;
        }
        MenuItem item = mNavigationView.getMenu().getItem(itemIndex);
        item.setChecked(true);
    }

    private void removeDashboard(int index){
        dashboards.deleteFromRealm(index);
        // TODO: Haven't been tested well.
        addMenuDashboardItems(null);
    }

    public Context getContext(){
        return this;
    }

    public int[] getChartColor(int i){
        return new int[]{ CHART_COLOR[i%4] };
    }

    private void showMsg(String msg, int c){
        Snackbar bar = Snackbar.make(mContentMain, msg, Snackbar.LENGTH_LONG);
        bar.getView().setBackgroundColor(c);
        bar.setAction("", null).show();
    }
}
