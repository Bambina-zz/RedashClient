package com.example.hirono_mayuko.redashclient2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hirono_mayuko.redashclient2.model.Dashboard;

import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by hirono-mayuko on 2017/05/18.
 */

public class DashboardRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<Dashboard, DashboardViewHolder> {

    public DashboardRecyclerViewAdapter(RealmList<Dashboard> dashboards){
        super(dashboards, true);
        setHasStableIds(true);
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dashboard_item_view, viewGroup, false);
        return new DashboardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, int position) {
        Dashboard dashboard = getItem(position);
        holder.data = dashboard;
        holder.mName.setText(dashboard.getName());
        holder.position = position;
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }
}
