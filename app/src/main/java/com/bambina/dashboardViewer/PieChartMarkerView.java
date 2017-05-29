package com.bambina.dashboardViewer;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hirono-mayuko on 2017/04/26.
 */

public class PieChartMarkerView extends MarkerView {
    private RelativeLayout mRelativeLayout;
    private TextView mName;
    private TextView mValue;
    private TextView mPercentage;
    private MPPointF mOffset;

    public PieChartMarkerView(Context context, int layoutResource, LinearLayout view) {
        super(context, layoutResource);

        // find your layout components
        mName = (TextView) view.findViewById(R.id.selectedName);
        mValue = (TextView) view.findViewById(R.id.selectedValue);
        mPercentage = (TextView) view.findViewById(R.id.selectedPercentage);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.markerLayout);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        PieEntry pe = (PieEntry) e;
        Object data = pe.getData();

        HashMap<String, String> obj;
        if(data instanceof Map){
            obj = (HashMap<String, String>) data;
            mName.setText(obj.get("name"));
            mValue.setText(obj.get("value"));
            mPercentage.setText(obj.get("percentage"));
        }

        mRelativeLayout.setVisibility(View.GONE);
        // this will perform necessary layouting
        super.refreshContent(e, highlight);

    }

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
