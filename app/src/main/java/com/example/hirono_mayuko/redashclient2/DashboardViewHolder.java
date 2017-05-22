package com.example.hirono_mayuko.redashclient2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hirono_mayuko.redashclient2.model.Dashboard;

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
