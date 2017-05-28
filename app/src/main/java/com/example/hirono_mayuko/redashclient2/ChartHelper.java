package com.example.hirono_mayuko.redashclient2;

import android.text.format.DateFormat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

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
                long diff = maxTime - minTime;
                String formatter = DateTimeHelper.getFormat(diff, value);
                return DateFormat.format(formatter, date).toString();
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
        lineChart.getLegend().setEnabled(false);
    }

    public static void barChartAxisOptions(BarChart barChart, final Long maxTime, final Long minTime){
        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Float time = minTime + value * (maxTime - minTime);
                Date date = new Date(time.longValue());
                long diff = maxTime - minTime;
                String formatter = DateTimeHelper.getFormat(diff, value);
                return DateFormat.format(formatter, date).toString();
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
        barChart.getLegend().setEnabled(false);
    }

    private static class DateTimeHelper {
        public static String getFormat(long diff, float value){
            String formatter = "yyyy-MM-dd";

            if(diff <= 1000L * 60 * 60 * 30){
                if(value < 0 || 1 < value){
                    formatter = "yyyy-MM-dd";
                } else {
                    formatter = "hh:mm";
                }
            } else if(diff <= 1000L * 60 * 60 * 24 * 30 * 12){
                if(value < 0 || 1 < value){
                    formatter = "yyyy-MM-dd";
                } else {
                    formatter = "MM-dd";
                }
            }
            return formatter;
        }
    }
}
