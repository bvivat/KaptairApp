package com.example.kaptair.ui.main.graphiques;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import com.example.kaptair.R;
import com.example.kaptair.ui.main.HistoriqueFrag;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {

    public static final int HOUR=0;
    public static final int DAY=1;
    public static final int YEAR=2;

    public static final int GRAPH_POLLUTION=0;
    public static final int GRAPH_METEO=1;

    public static final int INTERVALLE_HOUR=3600;
    public static final int INTERVALLE_DAY=86400;
    public static final int INTERVALLE_YEAR=8736;

    public static final long ONE_HOUR=3600000L;
    public static final long ONE_DAY=86400000L;
    public static final long ONE_YEAR=31536000000L;

    private final String TAG = "Pollution Graph";

    int type_graph;

    WeakReference<HistoriqueFrag> frag;
    LineChart chart;
    List<? extends Mesure> mesures;
    CardGraph card;

    int plageMin=0;
    int plageMax=0;
    int plage=0;
    ValueFormatter formatter;

    int anneeBissextile = 0;

    private long prevYear=0;
    private long nextYear=0;
    private long nextDay=0;
    private long nextHour=0;

    public Graph(HistoriqueFrag frag, CardGraph _card, List<? extends Mesure> mesures, int plage, int type_graph) {

        this.frag=new WeakReference<>(frag);
        this.card=_card;

        chart=card.getChart();
        this.type_graph=type_graph;

        this.mesures=mesures;
        this.plage=plage;

        switch (plage){ //TODO Fusionner cette partie entre les deux constructeurs (fonction externe ? , argument optionnel ?)
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
                c.setTimeInMillis(card.getCalendrier().getTimeInMillis()-ONE_YEAR); //L'annee precedente
                prevYear = (c.getActualMaximum(Calendar.DAY_OF_YEAR)>365 ? ONE_YEAR+ONE_DAY : ONE_YEAR); // Si bissextile, vaut 366 jours
                nextYear=ONE_YEAR;
                plageMin=0;
                plageMax=INTERVALLE_YEAR+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, false);
                break;
        }

    }

    public Graph(HistoriqueFrag frag, CardGraph _card, List<? extends Mesure> mesures, int plage, boolean isBissextile, int type_graph) {
        this.frag=new WeakReference<>(frag);
        this.card=_card;
        chart=card.getChart();
        this.type_graph=type_graph;
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
                c.setTimeInMillis(card.getCalendrier().getTimeInMillis()-ONE_YEAR); //L'annee precedente
                prevYear = (c.getActualMaximum(Calendar.DAY_OF_YEAR)>365 ? ONE_YEAR+ONE_DAY : ONE_YEAR); // Si bissextile, vaut 366 jours
                nextYear=ONE_YEAR+(long)anneeBissextile*ONE_HOUR;
                plageMin=0;
                plageMax=INTERVALLE_YEAR+ anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, isBissextile());
                break;
        }

    }

    public void draw(){

        final HashMap<AppCompatCheckBox, DataSet> chkBoxs = new HashMap<>();
        LineData lineData = new LineData();
        switch (type_graph){

            case GRAPH_POLLUTION:
                //TODO Verifier instanceof et lancer exception
                ArrayList<PollutionMesure> mesuresPoll = (ArrayList<PollutionMesure>) mesures;
                List<Entry> entriesPM1 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM1.add(new Entry(i.getFloatDate(),(float)i.getPm1()));
                }

                final LineDataSet dataSetPM1 = new LineDataSet(entriesPM1, "PM1"); // add entries to dataset
                dataSetPM1.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
                dataSetPM1.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM1);


                List<Entry> entriesPM25 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM25.add(new Entry(i.getFloatDate(),(float)i.getPm25()));
                }

                LineDataSet dataSetPM25 = new LineDataSet(entriesPM25, "PM2.5"); // add entries to dataset
                dataSetPM25.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
                dataSetPM25.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM25);


                List<Entry> entriesPM10 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM10.add(new Entry(i.getFloatDate(),(float)i.getPm10()));
                }

                LineDataSet dataSetPM10 = new LineDataSet(entriesPM10, "PM10"); // add entries to dataset
                dataSetPM10.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm10));
                dataSetPM10.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM10);


                List<Entry> entriesCO2 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesCO2.add(new Entry(i.getFloatDate(),(float)i.getCo2()));
                }

                LineDataSet dataSetCO2 = new LineDataSet(entriesCO2, "CO2"); // add entries to dataset
                dataSetCO2.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCo2));
                dataSetCO2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                formatDataSet(dataSetCO2);

                lineData = new LineData(dataSetPM1,dataSetPM25,dataSetPM10,dataSetCO2);

                AppCompatCheckBox chkPm1 = frag.get().getView().findViewById(R.id.chkPM1);
                AppCompatCheckBox chkPm25 = frag.get().getView().findViewById(R.id.chkPM25);
                AppCompatCheckBox chkPm10 = frag.get().getView().findViewById(R.id.chkPM10);
                AppCompatCheckBox chkCo2 = frag.get().getView().findViewById(R.id.chkCO2);

                chkBoxs.put(chkPm1,dataSetPM1);
                chkBoxs.put(chkPm25,dataSetPM25);
                chkBoxs.put(chkPm10,dataSetPM10);
                chkBoxs.put(chkCo2,dataSetCO2);
                break;

            case GRAPH_METEO:

                //TODO Verifier instanceof et lancer exception
                ArrayList<MeteoMesure> mesuresMeteo = (ArrayList<MeteoMesure>) mesures;

                List<Entry> entriesTemperature = new ArrayList<Entry>();
                for (MeteoMesure i : mesuresMeteo) {
                    entriesTemperature.add(new Entry(i.getFloatDate(),(float)i.getTemperature()));
                }

                LineDataSet dataSetTemperature = new LineDataSet(entriesTemperature,  frag.get().getString(R.string.temperature)); // add entries to dataset
                dataSetTemperature.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
                dataSetTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetTemperature);


                List<Entry> entriesHumidity = new ArrayList<Entry>();
                for (MeteoMesure i : mesuresMeteo) {
                    entriesHumidity.add(new Entry(i.getFloatDate(),(float)i.getHumidity()));
                }

                LineDataSet dataSetHumidity = new LineDataSet(entriesHumidity,  frag.get().getString(R.string.humidite)); // add entries to dataset
                dataSetHumidity.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
                dataSetHumidity.setAxisDependency(YAxis.AxisDependency.RIGHT);
                formatDataSet(dataSetHumidity);


                lineData = new LineData(dataSetTemperature,dataSetHumidity);
                break;


        }




        chart.setData(lineData);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.setKeepPositionOnRotation(true);

        /*
        Legend legend = chart.getLegend();
        legend.setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphLegend));
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setXEntrySpace(15f);
        //chart.setExtraBottomOffset(10f);

         */
        //Graphs Legend


        CompoundButton.OnCheckedChangeListener chkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    chkBoxs.get(buttonView).setVisible(true);
                    chart.invalidate();
                }else{
                    chkBoxs.get(buttonView).setVisible(false);
                    chart.invalidate();
                }
            }
        };

        for (Map.Entry<AppCompatCheckBox, DataSet> entry : chkBoxs.entrySet()) {
            entry.getKey().setOnCheckedChangeListener(chkListener);
            if (!entry.getKey().isChecked()){
                entry.getValue().setVisible(false);
            }
        }

        chart.getLegend().setEnabled(false);

        chart.setViewPortOffsets(75,10,75,45); //TODO PROBLEMES AFFICHAGE ?

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
            public void onChartDoubleTapped(MotionEvent me) { //TODO Dezoomer

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
        Calendar c = card.getCalendrier();
        c.setTimeInMillis(c.getTimeInMillis()-nextHour-nextDay-prevYear);

        switch (plage){
            case HOUR:
                card.graphHour();
                card.setTitreHour();
                break;
            case DAY:
                card.graphDay();
                card.setTitreDay();
                break;
            case YEAR:
                card.graphYear();
                card.setTitreYear();
                break;
        }

        chart.fitScreen();
        chart.invalidate();
    }

    private void showNextPage() {
        Calendar c = card.getCalendrier();
        c.setTimeInMillis(c.getTimeInMillis()+nextHour+nextDay+nextYear);

        switch (plage){
            case HOUR:
                card.graphHour();
                card.setTitreHour();
                break;
            case DAY:
                card.graphDay();
                card.setTitreDay();
                break;
            case YEAR:
                card.graphYear();
                card.setTitreYear();
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

    private void formatDataSet(LineDataSet d){
        d.setDrawValues(false);
        d.setDrawHighlightIndicators(false);
        //d.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        //d.setCircleRadius(1f);
        d.setDrawCircles(false);
        d.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
    }


}
