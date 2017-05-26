package com.example.hirono_mayuko.redashclient2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.hirono_mayuko.redashclient2.DashboardRecyclerViewAdapter;
import com.example.hirono_mayuko.redashclient2.DashboardViewHolder;
import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.RedashApplication;
import com.example.hirono_mayuko.redashclient2.model.Dashboard;
import com.example.hirono_mayuko.redashclient2.model.DataHelper;
import com.example.hirono_mayuko.redashclient2.model.Redash;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by hirono-mayuko on 2017/05/18.
 */

public class ManageDashboardActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Realm mRealm;
    private RealmList<Dashboard> mDashboards;
    private Context mContext;
    private boolean isManaged = false;
    private int selectedRedash;
    private int selectedDashboard;
    private static final String SELECTED_REDASH = "selectedRedash";
    private static final String SELECTED_DASHBOARD = "selectedDashboard";
    private static final int INITIAL_INDEX = 0;
    private static final String ID = "id";

    private class TouchHelperCallback extends ItemTouchHelper.SimpleCallback {
        TouchHelperCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(RecyclerView view, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder target){
            return true;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder holder, int direction){
            if(holder instanceof DashboardViewHolder){
                final DashboardViewHolder vh = (DashboardViewHolder) holder;
                Dashboard d = vh.data;
                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.delete_dashboard))
                        .setMessage(d.getName() + "\n\n"
                                + d.getmUrl())
                        .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataHelper.deleteDashboardAsync(mRealm, holder.getItemId());
                                isManaged = true;
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemChanged(vh.position);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dashboard);
        mContext = this;

        selectedRedash = getIntent().getIntExtra(SELECTED_REDASH, INITIAL_INDEX);
        selectedDashboard = getIntent().getIntExtra(SELECTED_DASHBOARD, INITIAL_INDEX);

        mRecyclerView = (RecyclerView) findViewById(R.id.dashboard_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRealm = Realm.getDefaultInstance();
        long redashId = RedashApplication.getmRedash().getId();
        mDashboards = mRealm.where(Redash.class).equalTo(ID, redashId).findFirst().getDashboards();
        mAdapter = new DashboardRecyclerViewAdapter(mDashboards);
        mRecyclerView.setAdapter(mAdapter);

        TouchHelperCallback cb = new TouchHelperCallback();
        ItemTouchHelper helper = new ItemTouchHelper(cb);
        helper.attachToRecyclerView(mRecyclerView);

        if(mDashboards.size() == 0){
           findViewById(R.id.noDashboard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noDashboard).setVisibility(View.GONE);
        }
    }

    @Override
    public  void onBackPressed(){
        if(isManaged){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(SELECTED_REDASH, selectedRedash);
            startActivityForResult(i, RESULT_OK);
        } else {
            Intent i = new Intent();
            setResult(RESULT_CANCELED, i);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        mRealm.close();
    }
}
