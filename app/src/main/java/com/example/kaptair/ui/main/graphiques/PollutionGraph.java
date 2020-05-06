package com.example.kaptair.ui.main.graphiques;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.kaptair.R;
import com.example.kaptair.ui.main.HistoriqueFrag;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PollutionGraph {

    public static final int HOUR=0;
    public static final int DAY=1;
    public static final int YEAR=2;

    public static final int INTERVALLE_HOUR=3600;
    public static final int INTERVALLE_DAY=86400;
    public static final int INTERVALLE_YEAR=8736;

    public static final long ONE_HOUR=3600000L;
    public static final long ONE_DAY=86400000L;
    public static final long ONE_YEAR=31536000000L;

    private final String TAG = "Pollution Graph";

    WeakReference<HistoriqueFrag> frag;
    LineChart chart;
    List<? extends PollutionMesure> mesures;

    int plageMin=0;
    int plageMax=0;
    int plage=0;
    ValueFormatter formatter;

    int anneeBissextile = 0;

    private long prevYear=0;
    private long nextYear=0;
    private long nextDay=0;
    private long nextHour=0;

    public PollutionGraph(HistoriqueFrag frag, List<? extends PollutionMesure> mesures, int plage) {
        this.frag=new WeakReference<>(frag);
        chart=this.frag.get().getView().findViewById(R.id.graphPollution);
        this.mesures=mesures;
        this.plage=plage;

        switch (plage){
            case HOUR:
                nextHour=ONE_HOUR;
                plageMin=0;
                plageMax=INTERVALLE_HOUR;
                formatter = new DatetimeHourFormatter(chart);
                break;
            case DAY:
                nextDay=ONE_DAY;
                plageMin=0;
                plageMax=INTERVALLE_DAY;
                formatter = new DatetimeDayFormatter(chart);
                break;
            case YEAR:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(frag.getC().getTimeInMillis()-ONE_YEAR); //L'annee precedente
                prevYear = (c.getActualMaximum(Calendar.DAY_OF_YEAR)>365 ? ONE_YEAR+ONE_DAY : ONE_YEAR); // Si bissextile, vaut 366 jours
                nextYear=ONE_YEAR;
                plageMin=0;
                plageMax=INTERVALLE_YEAR+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, false);
                break;
        }

    }

    public PollutionGraph(HistoriqueFrag frag, List<? extends PollutionMesure> mesures,int plage, boolean isBissextile) {
        this.frag=new WeakReference<>(frag);
        chart=this.frag.get().getView().findViewById(R.id.graphPollution);
        this.mesures=mesures;
        this.plage=plage;

        if (isBissextile){
            anneeBissextile=24;
        }else {
            anneeBissextile=0;
        }
        switch (plage){
            case HOUR:
                nextHour=ONE_HOUR;
                plageMin=0;
                plageMax=INTERVALLE_HOUR;
                formatter = new DatetimeHourFormatter(chart);
                break;
            case DAY:
                nextDay=ONE_DAY;
                plageMin=0;
                plageMax=INTERVALLE_DAY;
                formatter = new DatetimeDayFormatter(chart);
                break;
            case YEAR:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(frag.getC().getTimeInMillis()-ONE_YEAR); //L'annee precedente
                prevYear = (c.getActualMaximum(Calendar.DAY_OF_YEAR)>365 ? ONE_YEAR+ONE_DAY : ONE_YEAR); // Si bissextile, vaut 366 jours
                nextYear=ONE_YEAR+(long)anneeBissextile*ONE_HOUR;
                plageMin=0;
                plageMax=INTERVALLE_YEAR+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, isBissextile());
                break;
        }

    }

    public void draw(){
        List<Entry> entriesPM1 = new ArrayList<Entry>();
        for (PollutionMesure i : mesures) {
            entriesPM1.add(new Entry(i.getFloatDate(),(float)i.getPm1()));
        }

        LineDataSet dataSetPM1 = new LineDataSet(entriesPM1, "PM1"); // add entries to dataset
        dataSetPM1.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
        dataSetPM1.setDrawValues(false);
        dataSetPM1.setDrawHighlightIndicators(false);
        dataSetPM1.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetPM1.setCircleRadius(1f);
        dataSetPM1.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPM1.setDrawCircles(false);


        List<Entry> entriesPM25 = new ArrayList<Entry>();
        for (PollutionMesure i : mesures) {
            entriesPM25.add(new Entry(i.getFloatDate(),(float)i.getPm25()));
        }

        LineDataSet dataSetPM25 = new LineDataSet(entriesPM25, "PM2.5"); // add entries to dataset
        dataSetPM25.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
        dataSetPM25.setDrawValues(false);
        dataSetPM25.setDrawHighlightIndicators(false);
        dataSetPM25.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetPM25.setCircleRadius(1f);
        dataSetPM25.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPM25.setDrawCircles(false);


        List<Entry> entriesPM10 = new ArrayList<Entry>();
        for (PollutionMesure i : mesures) {
            entriesPM10.add(new Entry(i.getFloatDate(),(float)i.getPm10()));
        }

        LineDataSet dataSetPM10 = new LineDataSet(entriesPM10, "PM10"); // add entries to dataset
        dataSetPM10.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm10));
        dataSetPM10.setDrawValues(false);
        dataSetPM10.setDrawHighlightIndicators(false);
        dataSetPM10.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetPM10.setCircleRadius(1f);
        dataSetPM10.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPM10.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSetPM10.setDrawCircles(false);



        List<Entry> entriesCO2 = new ArrayList<Entry>();
        for (PollutionMesure i : mesures) {
            entriesCO2.add(new Entry(i.getFloatDate(),(float)i.getCo2()));
        }

        LineDataSet dataSetCO2 = new LineDataSet(entriesCO2, "CO2"); // add entries to dataset
        dataSetCO2.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCo2));
        dataSetCO2.setDrawValues(false);
        dataSetCO2.setDrawHighlightIndicators(false);
        dataSetCO2.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        dataSetCO2.setCircleRadius(1f);
        dataSetCO2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSetCO2.setDrawCircles(false);

        LineData lineData = new LineData(dataSetPM1,dataSetPM25,dataSetPM10,dataSetCO2);


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
                        if(chart.getHighestVisibleX()==chart.getXChartMax()){
                            Log.d(TAG,"Next by touch");
                        }
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouch(v,event);
                    default:
                        return super.onTouch(v,event);
                }
            }
        };
        chart.setOnTouchListener(chartListener);

        OnChartGestureListener gestureListener = new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                if (velocityX > 0) {
                    // fling to left
                    Log.d(TAG,"Prev");
                    showPrevPage(); // Your own implementation method.
                } else {
                    // fling to right
                    Log.d(TAG,"Next");
                    showNextPage(); // Your own implementation method.
                }

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        };

        chart.setOnChartGestureListener(gestureListener);

    }

    private void showPrevPage() {
        Calendar c = frag.get().getC();
        c.setTimeInMillis(c.getTimeInMillis()-nextHour-nextDay-prevYear);

        switch (plage){
            case HOUR:
                frag.get().graphPollutionHour();
                frag.get().setTitreGraphPollutionHour();
                break;
            case DAY:
                frag.get().graphPollutionDay();
                frag.get().setTitreGraphPollutionDay();
                break;
            case YEAR:
                frag.get().graphPollutionYear();
                frag.get().setTitreGraphPollutionYear();
                break;
        }

        chart.fitScreen();
        chart.invalidate();
    }

    private void showNextPage() {
        Calendar c = frag.get().getC();
        c.setTimeInMillis(c.getTimeInMillis()+nextHour+nextDay+nextYear);

        switch (plage){
            case HOUR:
                frag.get().graphPollutionHour();
                frag.get().setTitreGraphPollutionHour();
                break;
            case DAY:
                frag.get().graphPollutionDay();
                frag.get().setTitreGraphPollutionDay();
                break;
            case YEAR:
                frag.get().graphPollutionYear();
                frag.get().setTitreGraphPollutionYear();
                break;
        }

        chart.fitScreen();
        chart.invalidate();
    }


    public boolean isBissextile(){
        if (anneeBissextile >0){
            return true;
        }else{
            return false;
        }
    }


}
