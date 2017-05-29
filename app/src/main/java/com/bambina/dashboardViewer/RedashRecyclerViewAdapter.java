package com.bambina.dashboardViewer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bambina.dashboardViewer.model.Redash;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by hirono-mayuko on 2017/05/17.
 */

public class RedashRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<Redash, RedashViewHolder> {

    public RedashRecyclerViewAdapter(RealmResults<Redash> redashes){
        super(redashes, true);
        setHasStableIds(true);
    }

    @Override
    public RedashViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.redash_item_view, viewGroup, false);
        return new RedashViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RedashViewHolder holder, int position) {
        Redash redash = getItem(position);
        holder.data = redash;
        holder.mUrl.setText(redash.getUrl());
        holder.position = position;
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }
}
