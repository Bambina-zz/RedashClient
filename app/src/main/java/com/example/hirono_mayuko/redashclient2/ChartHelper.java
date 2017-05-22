package com.example.hirono_mayuko.redashclient2;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hirono-mayuko on 2017/04/25.
 */

public class ChartHelper {
    public static void lineChartAxisOptions(LineChart lineChart, final Long maxTime, final Long minTime){
        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float time = minTime + value * (maxTime - minTime);
                Date date = new Date((long) time);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return formatter.format(date);
            }

        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(xFormatter);
        xAxis.setTextSize(11);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new LargeValueFormatter());
        leftYAxis.setTextSize(12);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setDrawLabels(false);
        lineChart.getDescription().setText("");
    }

    public static void barChartAxisOptions(BarChart barChart, final Long maxTime, final Long minTime){
        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float time = minTime + value * (maxTime - minTime);
                Date date = new Date((long) time);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return formatter.format(date);
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(xFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.05f);
        xAxis.setAxisMaximum(1.05f);
        barChart.getDescription().setText("");

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setValueFormatter(new LargeValueFormatter());
        yAxisLeft.setAxisMinimum(0f);
    }
}
