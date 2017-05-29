package com.bambina.dashboardViewer.item;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bambina.dashboardViewer.ChartHelper;
import com.bambina.dashboardViewer.DimensionHelper;
import com.bambina.dashboardViewer.widget.LineChartWidget;
import com.bambina.dashboardViewer.activity.MainActivity;
import com.bambina.dashboardViewer.R;
import com.bambina.dashboardViewer.databinding.ItemLineChartBinding;
import com.xwray.groupie.Item;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class LineChartWidgetItem extends Item<ItemLineChartBinding> {
    public String mWidgetId;
    public LineChartWidget mWidget;
    public MainActivity mainActivity;

    public LineChartWidgetItem(String widgetId, HashMap<String, String> visualData,  MainActivity activity){
        super();
        mWidgetId = widgetId;
        mWidget = new LineChartWidget(visualData, activity, this);
        mainActivity = activity;
    }

    @Override
    public void bind(ItemLineChartBinding binding, int position){
        binding.setLineChartWidget(mWidget);

        binding.errMsg.setVisibility(View.GONE);
        binding.title.setVisibility(View.GONE);
        binding.chart.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        if(mWidget.mLineData == null) {
            return;
        }

        if(mWidget.isJsonException || !mWidget.isDateTime){
            Context c = mainActivity.getContext();
            int layoutHeight = Math.round(DimensionHelper.convertDpToPx(c, 300f));
            binding.widgetWrapper.getLayoutParams().height = layoutHeight;
            String errMsg;
            if(mWidget.isJsonException){
                errMsg = c.getResources().getString(R.string.data_parse_error);
            } else {
                errMsg = c.getResources().getString(R.string.data_not_supported);
            }
            binding.progressBar.setVisibility(View.GONE);
            binding.errMsg.setText(errMsg);
            binding.errMsg.setVisibility(View.VISIBLE);
            return;
        }

        ChartHelper.lineChartAxisOptions(binding.chart, mWidget.maxTime, mWidget.minTime);
        binding.chart.setData(mWidget.mLineData);
        binding.chart.invalidate();
        binding.widgetWrapper.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.progressBar.setVisibility(View.GONE);
        binding.title.setVisibility(View.VISIBLE);
        binding.chart.setVisibility(View.VISIBLE);
    }

    @Override public int getLayout() {
        return R.layout.item_line_chart;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
