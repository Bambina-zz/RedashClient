package com.example.hirono_mayuko.redashclient2.item;

import com.example.hirono_mayuko.redashclient2.R;
import com.example.hirono_mayuko.redashclient2.activity.MainActivity;
import com.example.hirono_mayuko.redashclient2.widget.TableWidget;
import com.example.hirono_mayuko.redashclient2.databinding.ItemTableBinding;
import com.xwray.groupie.Item;

import java.util.HashMap;

/**
 * Created by hirono-mayuko on 2017/04/24.
 */

public class TableWidgetItem extends Item<ItemTableBinding> {
    public String mWidgetId;
    private TableWidget mWidget;
    private MainActivity mainActivity;

    public TableWidgetItem(String widgetId, HashMap<String, String> visualData, MainActivity activity){
        super();
        mWidgetId = widgetId;
        mainActivity = activity;
        mWidget = new TableWidget(visualData, activity, this);
    }

    @Override
    public void bind(ItemTableBinding binding, int position){
        binding.setTableWidget(mWidget);
        if(mWidget.mVisualName == null){
            return;
        }
    }

    @Override public int getLayout() {
        return R.layout.item_table;
    }

    public void notifyWidgetChanged(){
        mainActivity.notifyItemChanged(this);
    }
}
