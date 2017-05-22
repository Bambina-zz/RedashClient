package com.example.hirono_mayuko.redashclient2.item;

import android.view.View;

import com.example.hirono_mayuko.redashclient2.ChartHelper;
import com.example.hirono_mayuko.redashclient2.widget.ColumnChartWidget;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.databinding.ItemColumnChartBinding;
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

        if(mWidget.mBarData == null){
            return;
        }

        ChartHelper.barChartAxisOptions(binding.chart, mWidget.maxTime, mWidget.minTime);
        // TODO: This is the problem, how I can organize width of a bar in a group?
        //float groupSpace = 0.03f;
        //float barSpace = 0f;
        float barWidth = 0.03f;
        // (0.00 + 0.17) *2 + 0.03 = 0.37
        mWidget.mBarData.setBarWidth(barWidth);
        binding.chart.setData(mWidget.mBarData);
        binding.chart.invalidate();
        binding.progressBar.setVisibility(View.GONE);
        binding.chart.setVisibility(View.VISIBLE);
    }

    @Override public int getLayout() {
        return R.layout.item_column_chart;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
