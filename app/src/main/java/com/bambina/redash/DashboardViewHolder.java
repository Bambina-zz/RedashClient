package com.bambina.redash;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bambina.redash.model.Dashboard;

/**
 * Created by hirono-mayuko on 2017/05/18.
 */

public class DashboardViewHolder extends RecyclerView.ViewHolder {
    public Dashboard data;
    public TextView mName;
    public int position;

    public DashboardViewHolder(View container){
        super(container);
        mName = (TextView) container.findViewById(R.id.name);
    }
}
