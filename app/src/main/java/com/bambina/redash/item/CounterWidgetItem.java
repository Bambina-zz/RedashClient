package com.bambina.redash.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bambina.redash.DimensionHelper;
import com.bambina.redash.widget.CounterWidget;
import com.bambina.redash.activity.MainActivity;
import com.bambina.redash.R;
import com.bambina.redash.databinding.ItemCounterBinding;
import com.xwray.groupie.Item;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class CounterWidgetItem extends Item<ItemCounterBinding> {
    public String mWidgetId;
    private CounterWidget mWidget;
    private MainActivity mainActivity;

    public CounterWidgetItem(String widgetId, HashMap<String, String> visualData, MainActivity activity){
        super();
        mWidgetId = widgetId;
        mainActivity = activity;
        mWidget = new CounterWidget(visualData, activity, this);
    }

    @Override
    public void bind(ItemCounterBinding binding, int position){
        binding.setCounterWidget(mWidget);

        binding.errMsg.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.queryName.setVisibility(View.GONE);
        binding.textVal.setVisibility(View.GONE);
        binding.visualName.setVisibility(View.GONE);
        if(mWidget.mValue == null){
            return;
        }

        if(mWidget.isFailed){
            Context c = mainActivity.getContext();
            int layoutHeight = Math.round(DimensionHelper.convertDpToPx(c, 300f));
            binding.widgetWrapper.getLayoutParams().height = layoutHeight;
            binding.progressBar.setVisibility(View.GONE);
            binding.errMsg.setText(c.getResources().getString(R.string.data_parse_error));
            binding.errMsg.setVisibility(View.VISIBLE);
            return;
        }

        binding.widgetWrapper.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.progressBar.setVisibility(View.GONE);
        binding.queryName.setVisibility(View.VISIBLE);
        binding.textVal.setVisibility(View.VISIBLE);
        binding.visualName.setVisibility(View.VISIBLE);
    }

    @Override public int getLayout() {
        return R.layout.item_counter;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
