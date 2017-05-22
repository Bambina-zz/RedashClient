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

import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.RedashRecyclerViewAdapter;
import com.example.hirono_mayuko.redashclient2.RedashViewHolder;
import com.example.hirono_mayuko.redashclient2.model.DataHelper;
import com.example.hirono_mayuko.redashclient2.model.Redash;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hirono-mayuko on 2017/05/17.
 */

public class ManageRedashActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RedashRecyclerViewAdapter mAdapter;
    private Realm mRealm;
    private RealmResults<Redash> mRedashes;
    private Context mContext;
    private boolean isManaged = false;
    private static final String SELECTED_REDASH = "selectedRedash";
    private static final String SELECTED_DASHBOARD = "selectedDashboard";
    private static final int INITIAL_INDEX = 0;

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
        public void onSwiped(RecyclerView.ViewHolder holder, int direction){
            if(holder instanceof RedashViewHolder){
                final RedashViewHolder vh = (RedashViewHolder) holder;
                Redash r = vh.data;
                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.delete_instance))
                        .setMessage(r.getUrl() + "\n\n"
                                + r.getApiKey() + "\n\n"
                                + r.getProxyUrl() + "\n\n"
                                + r.getProxyPortNumber())
                        .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataHelper.deleteRedashAsync(mRealm, vh.getItemId());
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
        setContentView(R.layout.activity_manage_redash);
        mContext = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.redash_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRealm = Realm.getDefaultInstance();
        mRedashes = mRealm.where(Redash.class).findAllSorted("id");

        mAdapter = new RedashRecyclerViewAdapter(mRedashes);
        mRecyclerView.setAdapter(mAdapter);

        ManageRedashActivity.TouchHelperCallback cb = new ManageRedashActivity.TouchHelperCallback();
        ItemTouchHelper helper = new ItemTouchHelper(cb);
        helper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onBackPressed(){
        if(isManaged){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(SELECTED_REDASH, INITIAL_INDEX);
            i.putExtra(SELECTED_DASHBOARD, INITIAL_INDEX);
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
