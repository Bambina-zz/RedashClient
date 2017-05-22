package com.example.hirono_mayuko.redashclient2.item;

import android.view.View;

import com.example.hirono_mayuko.redashclient2.widget.CounterWidget;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.databinding.ItemCounterBinding;
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

        if(mWidget.mValue == null){
            return;
        }

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
