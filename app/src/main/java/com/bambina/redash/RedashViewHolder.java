package com.bambina.redash;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bambina.redash.model.Redash;

/**
 * Created by hirono-mayuko on 2017/05/17.
 */

public class RedashViewHolder extends RecyclerView.ViewHolder {
    public Redash data;
    public int position;
    public TextView mUrl;

    public RedashViewHolder(View container){
        super(container);
        mUrl = (TextView) container.findViewById(R.id.url);
    }
}
