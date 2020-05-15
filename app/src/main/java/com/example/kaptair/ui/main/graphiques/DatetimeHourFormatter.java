package com.example.kaptair.ui.main.graphiques;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeHourFormatter extends ValueFormatter {

    private static final int GRAN_1 = 900;
    private static final int GRAN_2 = 300;
    private static final int GRAN_3 = 60;
    private static final int GRAN_4 = 15;
    private static final int GRAN_5 = 5;
    private static final int GRAN_6 = 1;

    LineChart chart;

    SimpleDateFormat sdfSeconds = new SimpleDateFormat("ss");
    SimpleDateFormat sdfHours = new SimpleDateFormat("HH:mm");


    public DatetimeHourFormatter(LineChart chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float dateSecondes) {
        // On gere ici l'affichage d'un string sur l'axe X en fonction de la valeur et du niveau de zoom \\
        XAxis x = chart.getXAxis();
        Date d = new Date((long) dateSecondes * 1000);

        sdfHours.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (chart.getVisibleXRange() < GRAN_5 + 5) {
            x.setGranularity(GRAN_6);
            if (dateSecondes % 60 == 0) {
                return sdfHours.format(d);
            } else {
                return sdfSeconds.format(d);
            }

        } else if (chart.getVisibleXRange() < GRAN_4 + 10) {
            x.setGranularity(GRAN_5);
            if (dateSecondes % 60 == 0) {
                return sdfHours.format(d);
            } else {
                return sdfSeconds.format(d);
            }

        } else if (chart.getVisibleXRange() < GRAN_3 + 10) {
            x.setGranularity(GRAN_4);
            if (dateSecondes % 60 == 0) {
                return sdfHours.format(d);
            } else {
                return sdfSeconds.format(d);
            }

        } else if (chart.getVisibleXRange() < GRAN_2 + 100) {
            x.setGranularity(GRAN_3);
            return sdfHours.format(d);
        } else if (chart.getVisibleXRange() < GRAN_1 + 1000) {
            x.setGranularity(GRAN_2);
            return sdfHours.format(d);
        } else {
            x.setGranularity(GRAN_1);
            return sdfHours.format(d);
        }

    }
}
