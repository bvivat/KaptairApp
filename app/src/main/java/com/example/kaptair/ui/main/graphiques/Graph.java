package com.example.kaptair.ui.main.graphiques;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import com.example.kaptair.R;
import com.example.kaptair.database.InterfacesMesures.Mesure;
import com.example.kaptair.database.InterfacesMesures.MeteoMesure;
import com.example.kaptair.database.InterfacesMesures.PollutionMesure;
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

    public static final int HOUR = 0;
    public static final int DAY = 1;
    public static final int YEAR = 2;

    public static final int GRAPH_POLLUTION = 0;
    public static final int GRAPH_METEO = 1;

    public static final int INTERVALLE_HOUR = 3600;
    public static final int INTERVALLE_DAY = 86400;
    public static final int INTERVALLE_YEAR = 8736;

    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_YEAR = 31536000000L;

    private final String TAG = "Pollution Graph";

    int type_graph;

    WeakReference<HistoriqueFrag> frag;
    LineChart chart;
    List<? extends Mesure> mesures;
    CardGraph card;

    int plageMin = 0;
    int plageMax = 0;
    int plage = 0;
    ValueFormatter formatter;

    int anneeBissextile = 0;

    private long prevYear = 0;
    private long nextYear = 0;
    private long nextDay = 0;
    private long nextHour = 0;

    public Graph(HistoriqueFrag frag, CardGraph _card, List<? extends Mesure> mesures, int plage) {

        this(frag, _card, mesures, plage, false);

    }

    public Graph(HistoriqueFrag frag, CardGraph _card, List<? extends Mesure> mesures, int plage, boolean isBissextile) {
        this.frag = new WeakReference<>(frag);
        this.card = _card;
        chart = card.getChart();
        this.mesures = mesures;
        this.plage = plage;

        //On regarde de quel type de mesures  il s'agit
        if (!mesures.isEmpty() && mesures.get(0) instanceof PollutionMesure) {
            this.type_graph = GRAPH_POLLUTION;

        } else if (!mesures.isEmpty() && mesures.get(0) instanceof MeteoMesure) {
            this.type_graph = GRAPH_METEO;

        } else {
            this.type_graph = -1;
        }


        if (isBissextile) {
            anneeBissextile = 24;
        } else {
            anneeBissextile = 0;
        }

        // On definit la plage et le formatter du graph
        switch (plage) {
            case HOUR:
                nextHour = ONE_HOUR;
                plageMin = 0;
                plageMax = INTERVALLE_HOUR;
                formatter = new DatetimeHourFormatter(chart);
                break;
            case DAY:
                nextDay = ONE_DAY;
                plageMin = 0;
                plageMax = INTERVALLE_DAY;
                formatter = new DatetimeDayFormatter(chart);
                break;
            case YEAR:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(card.getCalendrier().getTimeInMillis() - ONE_YEAR); //L'annee precedente
                prevYear = (c.getActualMaximum(Calendar.DAY_OF_YEAR) > 365 ? ONE_YEAR + ONE_DAY : ONE_YEAR); // Si bissextile, vaut 366 jours
                nextYear = ONE_YEAR + (long) anneeBissextile * ONE_HOUR;

                plageMin = 0;
                plageMax = INTERVALLE_YEAR + anneeBissextile;
                formatter = new DatetimeYearFormatter(chart, isBissextile());
                break;
        }

    }

    public void draw() {

        final HashMap<AppCompatCheckBox, DataSet> chkBoxs = new HashMap<>();
        LineData lineData = new LineData();

        //On initialise le graph en fonction du type
        switch (type_graph) {

            case GRAPH_POLLUTION:

                ArrayList<PollutionMesure> mesuresPoll = (ArrayList<PollutionMesure>) mesures;

                // On cree la liste des mesures PM1 contenue dans les mesures envoyees
                List<Entry> entriesPM1 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM1.add(new Entry(i.getFloatDate(), (float) i.getPm1()));
                }

                //On cree un dataSet reposant sur la liste des PM1
                final LineDataSet dataSetPM1 = new LineDataSet(entriesPM1, "PM1"); // add entries to dataset
                dataSetPM1.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
                dataSetPM1.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM1);

                // IDEM \\

                List<Entry> entriesPM25 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM25.add(new Entry(i.getFloatDate(), (float) i.getPm25()));
                }

                LineDataSet dataSetPM25 = new LineDataSet(entriesPM25, "PM2.5"); // add entries to dataset
                dataSetPM25.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
                dataSetPM25.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM25);


                List<Entry> entriesPM10 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesPM10.add(new Entry(i.getFloatDate(), (float) i.getPm10()));
                }

                LineDataSet dataSetPM10 = new LineDataSet(entriesPM10, "PM10"); // add entries to dataset
                dataSetPM10.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm10));
                dataSetPM10.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetPM10);


                List<Entry> entriesCO2 = new ArrayList<Entry>();
                for (PollutionMesure i : mesuresPoll) {
                    entriesCO2.add(new Entry(i.getFloatDate(), (float) i.getCo2()));
                }

                LineDataSet dataSetCO2 = new LineDataSet(entriesCO2, "CO2"); // add entries to dataset
                dataSetCO2.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCo2));
                dataSetCO2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                formatDataSet(dataSetCO2);

                // On cree un lineData a partir des dataSets
                lineData = new LineData(dataSetPM1, dataSetPM25, dataSetPM10, dataSetCO2);

                // On definit les axis Y
                chart.getAxisLeft().setAxisMinimum(0);
                if (lineData.getYMax(YAxis.AxisDependency.LEFT) < 150) {
                    chart.getAxisLeft().setAxisMaximum(165); // Le maximum affiche sera toujours d'au moins 150 + 10%
                } else {
                    chart.getAxisLeft().resetAxisMaximum(); // Si superieur, on calcule automatiquement
                }


                chart.getAxisRight().setAxisMinimum(0);
                if (lineData.getYMax(YAxis.AxisDependency.RIGHT) < 2500) {
                    chart.getAxisRight().setAxisMaximum(2750); // Le maximum affiche sera toujours d'au moins 2500 + 10%
                } else {
                    chart.getAxisRight().resetAxisMaximum(); // Si superieur, on calcule automatiquement
                }

                // On recupere les chkBoxs de la legende
                AppCompatCheckBox chkPm1 = frag.get().getView().findViewById(R.id.chkPM1);
                AppCompatCheckBox chkPm25 = frag.get().getView().findViewById(R.id.chkPM25);
                AppCompatCheckBox chkPm10 = frag.get().getView().findViewById(R.id.chkPM10);
                AppCompatCheckBox chkCo2 = frag.get().getView().findViewById(R.id.chkCO2);

                chkBoxs.put(chkPm1, dataSetPM1);
                chkBoxs.put(chkPm25, dataSetPM25);
                chkBoxs.put(chkPm10, dataSetPM10);
                chkBoxs.put(chkCo2, dataSetCO2);
                break;

            case GRAPH_METEO:
                // IDEM \\

                ArrayList<MeteoMesure> mesuresMeteo = (ArrayList<MeteoMesure>) mesures;

                List<Entry> entriesTemperature = new ArrayList<Entry>();
                for (MeteoMesure i : mesuresMeteo) {
                    entriesTemperature.add(new Entry(i.getFloatDate(), (float) i.getTemperature()));
                }

                LineDataSet dataSetTemperature = new LineDataSet(entriesTemperature, frag.get().getString(R.string.temperature)); // add entries to dataset
                dataSetTemperature.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm1));
                dataSetTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
                formatDataSet(dataSetTemperature);


                List<Entry> entriesHumidity = new ArrayList<Entry>();
                for (MeteoMesure i : mesuresMeteo) {
                    entriesHumidity.add(new Entry(i.getFloatDate(), (float) i.getHumidity()));
                }

                LineDataSet dataSetHumidity = new LineDataSet(entriesHumidity, frag.get().getString(R.string.humidite)); // add entries to dataset
                dataSetHumidity.setColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphPm25));
                dataSetHumidity.setAxisDependency(YAxis.AxisDependency.RIGHT);
                formatDataSet(dataSetHumidity);


                lineData = new LineData(dataSetTemperature, dataSetHumidity);

                // On definit les axis Y
                chart.getAxisLeft().setAxisMinimum(0);
                chart.getAxisLeft().setAxisMaximum(110);

                chart.getAxisRight().setAxisMinimum(0);
                chart.getAxisRight().setAxisMaximum(110);

                // On recupere les chkBoxs de la legende
                AppCompatCheckBox chkTemperature = frag.get().getView().findViewById(R.id.chkTemperature);
                AppCompatCheckBox chkHumidite = frag.get().getView().findViewById(R.id.chkHumidite);

                chkBoxs.put(chkTemperature, dataSetTemperature);
                chkBoxs.put(chkHumidite, dataSetHumidity);

                break;

            default:
                break;
        }


        // On affecte les donnees au graph
        chart.setData(lineData);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        chart.setKeepPositionOnRotation(true);

        // Graph Legend \\

        // On fait le listener des checkBoxs
        CompoundButton.OnCheckedChangeListener chkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkBoxs.get(buttonView).setVisible(true);
                    chart.invalidate();
                } else {
                    chkBoxs.get(buttonView).setVisible(false);
                    chart.invalidate();
                }
            }
        };

        // On affecte le listener a toutes les checkBoxs
        for (Map.Entry<AppCompatCheckBox, DataSet> entry : chkBoxs.entrySet()) {
            entry.getKey().setOnCheckedChangeListener(chkListener);
            if (!entry.getKey().isChecked()) {
                entry.getValue().setVisible(false);
            }
        }

        // On desactive la legende integree du graph
        chart.getLegend().setEnabled(false);


        // Configuration du graph \\

        // On aligne le graph comme on le souhaite
        chart.setViewPortOffsets(75, 10, 75, 45); //TODO PROBLEMES AFFICHAGE ?


        XAxis x = chart.getXAxis();

        x.setAxisMinimum(plageMin);
        x.setAxisMaximum(plageMax);

        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setTextColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphGraduations));

        x.setValueFormatter(formatter);

        chart.getDescription().setEnabled(false);

        chart.setDoubleTapToZoomEnabled(false);

        chart.fitScreen();
        chart.notifyDataSetChanged();
        chart.postInvalidate(); // refresh

        ChartTouchListener chartListener = new BarLineChartTouchListener(chart, chart.getViewPortHandler().getMatrixTouch(), 3F) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //On desactive les mouvements du viewPager lors des mouvements dans le graph
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouch(v, event);
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return super.onTouch(v, event);
                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouch(v, event);
                    default:
                        return super.onTouch(v, event);
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
                // /!\ Triggered when zooming multiple times
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // On gere le swip lorsque le graph est dezoome au maximum
                if (velocityX > 0) {
                    // fling to left
                    Log.d(TAG, "Prev");
                    showPrevPage();
                } else {
                    // fling to right
                    Log.d(TAG, "Next");
                    showNextPage();
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
        // On enleve le temps qu'il faut pour arriver a la plage precedente
        Calendar c = card.getCalendrier();
        c.setTimeInMillis(c.getTimeInMillis() - nextHour - nextDay - prevYear);

        // On cree le nouveau graph
        switch (plage) {
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
    }

    private void showNextPage() {
        // On ajoute le temps qu'il faut pour arriver a la plage suivante
        Calendar c = card.getCalendrier();
        c.setTimeInMillis(c.getTimeInMillis() + nextHour + nextDay + nextYear);

        // On cree le nouveau graph
        switch (plage) {
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
    }


    public boolean isBissextile() {
        if (anneeBissextile > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void formatDataSet(LineDataSet d) {
        d.setDrawValues(false);
        d.setDrawHighlightIndicators(false);
        //d.setCircleColor(ContextCompat.getColor(frag.get().getContext(), R.color.colorGraphCircle));
        //d.setCircleRadius(1f);
        d.setDrawCircles(false);
        d.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
    }


}
