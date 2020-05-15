package com.example.kaptair.ui.main.graphiques;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeDayFormatter extends ValueFormatter {

    private static final int GRAN_1 = 14400;
    private static final int GRAN_2 = 3600;
    private static final int GRAN_3 = 900;
    private static final int GRAN_4 = 300;

    LineChart chart;

    SimpleDateFormat sdfHours = new SimpleDateFormat("HH:mm");

    public DatetimeDayFormatter(LineChart chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float dateSecondes) {
        // On gere ici l'affichage d'un string sur l'axe X en fonction de la valeur et du niveau de zoom \\
        XAxis x = chart.getXAxis();
        Date d = new Date((long) dateSecondes * 1000);

        sdfHours.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (chart.getVisibleXRange() < GRAN_3 + 1000) {
            x.setGranularity(GRAN_4);
            return sdfHours.format(d);
        } else if (chart.getVisibleXRange() < GRAN_2 + 2000) {
            x.setGranularity(GRAN_3);
            return sdfHours.format(d);
        } else if (chart.getVisibleXRange() < GRAN_1 + 5000) {
            x.setGranularity(GRAN_2);
            return sdfHours.format(d);
        } else {
            x.setGranularity(GRAN_1);
            return sdfHours.format(d);
        }

    }
}
