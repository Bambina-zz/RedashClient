package com.bambina.redash;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hirono-mayuko on 2017/04/26.
 */

public class PieChartRecyclerViewAdapter extends RecyclerView.Adapter<PieChartRecyclerViewAdapter.PieListViewHolder> {
    private List<PieChartListItem> mList;

    public static class PieListViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mValue;
        private TextView mPercentage;

        public PieListViewHolder(View v){
            super(v);
            this.mName = (TextView) v.findViewById(R.id.itemName);
            this.mValue = (TextView) v.findViewById(R.id.itemValue);
            this.mPercentage = (TextView) v.findViewById(R.id.itemPercentage);
        }
    }

    public PieChartRecyclerViewAdapter(List<PieChartListItem> list){
        this.mList = list;
    }

    @Override
    public PieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pie_list_item, parent, false);
        return new PieListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PieListViewHolder holder, int position){
        PieChartListItem item = mList.get(position);

        holder.mName.setText(item.mName);
        holder.mValue.setText(item.mValue);
        holder.mPercentage.setText(item.mPercentage);
    }

    @Override
    public int getItemCount(){
        return this.mList.size();
    }
}
