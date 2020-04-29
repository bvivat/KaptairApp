package com.example.kaptair.ui.main.graphiques;

import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.kaptair.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.ChartTouchListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MeteoGraph {

    public static final int HOUR=0;
    public static final int DAY=1;
    public static final int YEAR=2;


    WeakReference<Fragment> frag;
    LineChart chart;
    List<? extends MeteoMesure> mesures;

    int plageMin=0;
    int plageMax=0;
    ValueFormatter formatter;

    int anneeBissextile = 0;

    public MeteoGraph(Fragment frag, List<? extends MeteoMesure> mesures, int plage) {
        this.frag=new WeakReference<>(frag);
        chart=this.frag.get().getView().findViewById(R.id.graphMeteo);
        this.mesures=mesures;
        switch (plage){
            case HOUR:
                plageMin=0;
                plageMax=3600;
                formatter = new DatetimeHourFormatter(chart);
                break;
            case DAY:
                plageMin=0;
                plageMax=86400;
                formatter = new DatetimeDayFormatter(chart);
                break;
            case YEAR:
                plageMin=0;
                plageMax=8736+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, false);
                break;
        }

    }

    public MeteoGraph(Fragment frag, List<? extends MeteoMesure> mesures, int plage, boolean isBissextile) {
        this.frag=new WeakReference<>(frag);
        chart=this.frag.get().getView().findViewById(R.id.graphMeteo);
        this.mesures=mesures;
        if (isBissextile){
            anneeBissextile=24;
        }else {
            anneeBissextile=0;
        }
        switch (plage){
            case HOUR:
                plageMin=0;
                plageMax=3600;
                formatter = new DatetimeHourFormatter(chart);
                break;
            case DAY:
                plageMin=0;
                plageMax=86400;
                formatter = new DatetimeDayFormatter(chart);
                break;
            case YEAR:
                plageMin=0;
                plageMax=8736+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, isBissextile());
                break;
        }

    }

    public void draw(){
        List<Entry> entriesTemperature = new ArrayList<Entry>();
        for (MeteoMesure i : mesures) {
            entriesTemperature.add(new Entry(i.getFloatDate(),(float)i.getTemperature()));
        }

        LineDataSet dataSetTemperature = new LineDataSet(entriesTemperature,  frag.get().getString(R.string.temperature)); // add entries to dataset
        dataSetTemperature.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
        dataSetTemperature.setDrawValues(false);
        dataSetTemperature.setDrawHighlightIndicators(false);
        dataSetTemperature.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetTemperature.setCircleRadius(1f);
        dataSetTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);


        List<Entry> entriesHumidity = new ArrayList<Entry>();
        for (MeteoMesure i : mesures) {
            entriesHumidity.add(new Entry(i.getFloatDate(),(float)i.getHumidity()));
        }

        LineDataSet dataSetHumidity = new LineDataSet(entriesHumidity,  frag.get().getString(R.string.humidite)); // add entries to dataset
        dataSetHumidity.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
        dataSetHumidity.setDrawValues(false);
        dataSetHumidity.setDrawHighlightIndicators(false);
        dataSetHumidity.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetHumidity.setCircleRadius(1f);
        dataSetHumidity.setAxisDependency(YAxis.AxisDependency.RIGHT);


        LineData lineData = new LineData(dataSetTemperature,dataSetHumidity);


        chart.setData(lineData);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.setKeepPositionOnRotation(true);

        Legend legend = chart.getLegend();
        legend.setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphLegend));
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setXEntrySpace(15f);
        //chart.setExtraBottomOffset(10f);

        chart.setViewPortOffsets(75,10,75,110); //TODO PROBLEMES AFFICHAGE ?

        XAxis x = chart.getXAxis();

        x.setAxisMinimum(plageMin);
        x.setAxisMaximum(plageMax);

        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        x.setValueFormatter(formatter);

        chart.getDescription().setEnabled(false);


        chart.fitScreen();
        chart.notifyDataSetChanged();
        chart.postInvalidate(); // refresh


        ChartTouchListener chartListener = new BarLineChartTouchListener(chart,chart.getMatrix(), 3F){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouch(v,event);
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return super.onTouch(v,event);
                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouch(v,event);
                    default:
                        return super.onTouch(v,event);
                }
            }
        };
        chart.setOnTouchListener(chartListener);

    }

    public boolean isBissextile(){
        if (anneeBissextile >0){
            return true;
        }else{
            return false;
        }
    }


}
