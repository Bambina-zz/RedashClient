package com.bambina.redash.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bambina.redash.ChartHelper;
import com.bambina.redash.DimensionHelper;
import com.bambina.redash.widget.ColumnChartWidget;
import com.bambina.redash.activity.MainActivity;
import com.bambina.redash.R;
import com.bambina.redash.databinding.ItemColumnChartBinding;
import com.xwray.groupie.Item;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class ColumnChartWidgetItem extends Item<ItemColumnChartBinding> {
    public String mWidgetId;
    private ColumnChartWidget mWidget;
    private MainActivity mainActivity;

    public ColumnChartWidgetItem(String widgetId, HashMap<String, String> visualData, MainActivity activity){
        super();
        mWidgetId = widgetId;
        mainActivity = activity;
        mWidget = new ColumnChartWidget(visualData, activity, this);
    }

    @Override
    public void bind(ItemColumnChartBinding binding, int position){
        binding.setColumnChartWidget(mWidget);

        binding.errMsg.setVisibility(View.GONE);
        binding.title.setVisibility(View.GONE);
        binding.chart.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        if(mWidget.mBarData == null){
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

        ChartHelper.barChartAxisOptions(binding.chart, mWidget);
        // TODO: This is the problem, how I can organize width of a bar in a group?
        float groupSpace = 0.5f / mWidget.numSeries;
        float barSpace = 0.5f / mWidget.numBars;
        float barWidth = 0.5f / mWidget.numBars;
        if(mWidget.numSeries > 1){
            mWidget.mBarData.groupBars(0f,groupSpace, barSpace);
        }
        mWidget.mBarData.setBarWidth(barWidth);
        binding.chart.setData(mWidget.mBarData);
        binding.chart.invalidate();
        binding.widgetWrapper.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.progressBar.setVisibility(View.GONE);
        binding.title.setVisibility(View.VISIBLE);
        binding.chart.setVisibility(View.VISIBLE);
    }

    @Override public int getLayout() {
        return R.layout.item_column_chart;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
