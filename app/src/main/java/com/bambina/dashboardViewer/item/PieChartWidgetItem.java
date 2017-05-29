package com.bambina.dashboardViewer.item;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bambina.dashboardViewer.DimensionHelper;
import com.bambina.dashboardViewer.activity.MainActivity;
import com.bambina.dashboardViewer.PieChartListItem;
import com.bambina.dashboardViewer.PieChartMarkerView;
import com.bambina.dashboardViewer.PieChartRecyclerViewAdapter;
import com.bambina.dashboardViewer.widget.PieChartWidget;
import com.bambina.dashboardViewer.R;
import com.bambina.dashboardViewer.databinding.ItemPieChartBinding;
import com.xwray.groupie.Item;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class PieChartWidgetItem extends Item<ItemPieChartBinding> {
    public String mWidgetId;
    private  PieChartWidget mWidget;
    private MainActivity mainActivity;

    public PieChartWidgetItem(String widgetId, HashMap<String, String> visualData, MainActivity activity){
        super();
        mWidgetId = widgetId;
        mainActivity = activity;
        mWidget = new PieChartWidget(visualData, activity, this);
    }

    @Override
    public void bind(ItemPieChartBinding binding, int position){
        binding.setPieChartWidget(mWidget);

        binding.errMsg.setVisibility(View.GONE);
        binding.title.setVisibility(View.GONE);
        binding.chart.setVisibility(View.GONE);
        binding.selectedItem.setVisibility(View.GONE);
        binding.pieRecyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        if(mWidget.mPieData == null){
            return;
        }

        if(mWidget.isFailed) {
            Context c = mainActivity.getContext();
            int layoutHeight = Math.round(DimensionHelper.convertDpToPx(c, 300f));
            binding.widgetWrapper.getLayoutParams().height = layoutHeight;
            binding.progressBar.setVisibility(View.GONE);
            binding.errMsg.setText(c.getResources().getString(R.string.data_parse_error));
            binding.errMsg.setVisibility(View.VISIBLE);
            return;
        }

        PieChartListItem item = mWidget.mPieListItems.get(0);
        binding.selectedName.setText(item.mName);
        binding.selectedValue.setText(item.mValue);
        binding.selectedPercentage.setText(item.mPercentage);

        // Set marker to pie chart
        PieChartMarkerView marker = new PieChartMarkerView(mainActivity.getContext(), R.layout.marker_view, binding.selectedItem);
        binding.chart.setMarker(marker);

        // Set chart data
        binding.chart.getDescription().setEnabled(false);
        binding.chart.getLegend().setEnabled(false);
        binding.chart.setUsePercentValues(true);
        binding.chart.setData(mWidget.mPieData);
        binding.chart.invalidate();

        // Set list data
        binding.pieRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainActivity.getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.pieRecyclerView.setLayoutManager(mLayoutManager);
        PieChartRecyclerViewAdapter mAdapter = new PieChartRecyclerViewAdapter(mWidget.mPieListItems);
        binding.pieRecyclerView.setAdapter(mAdapter);

        // Hide progress bar and display contents
        binding.widgetWrapper.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.progressBar.setVisibility(View.GONE);
        binding.title.setVisibility(View.VISIBLE);
        binding.selectedItem.setVisibility(View.VISIBLE);
        binding.chart.setVisibility(View.VISIBLE);
        binding.pieRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override public int getLayout() {
        return R.layout.item_pie_chart;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
