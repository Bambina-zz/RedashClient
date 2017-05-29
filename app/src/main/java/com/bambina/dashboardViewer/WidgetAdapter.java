package com.bambina.dashboardViewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bambina.dashboardViewer.activity.MainActivity;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ViewHolder> {
    private String[] mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTv;
        public ViewHolder(View v){
            super(v);
            TextView tv = (TextView) v.findViewById(R.id.textView);
            this.mTv = tv;
        }
    }

    public WidgetAdapter(MainActivity mainActivity, String[] dataSet) {
        this.mDataSet = dataSet;
    }

    @Override
    public WidgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(WidgetAdapter.ViewHolder holder, int position){
        holder.mTv.setText(mDataSet[position]);
    }

    @Override
    public int getItemCount(){
        return mDataSet.length;
    }
}
