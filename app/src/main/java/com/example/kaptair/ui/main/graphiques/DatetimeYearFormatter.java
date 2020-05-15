package com.example.kaptair.ui.main.graphiques;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeYearFormatter extends ValueFormatter {

    private static final int GRAN_1 = 1464;
    private static final int GRAN_2 = 731;
    private static final int GRAN_3 = 240;
    private static final int GRAN_4 = 120;
    private static final int GRAN_5 = 24;


    LineChart chart;
    boolean bissextile;

    SimpleDateFormat sdfDays = new SimpleDateFormat("dd/MM");
    SimpleDateFormat sdfMonths = new SimpleDateFormat("MMMM");

    public DatetimeYearFormatter(LineChart chart, boolean bissextile) {
        this.chart = chart;
        this.bissextile = bissextile;
    }

    @Override
    public String getFormattedValue(float dateHeures) {
        // On gere ici l'affichage d'un string sur l'axe X en fonction de la valeur et du niveau de zoom \\
        XAxis x = chart.getXAxis();
        Date d;

        if (bissextile) {
            d = new Date(((long) dateHeures * 1000 * 60 * 60) + 63072000000L); //On ajoute 2 ans pour être sur une base bissextile (1972)
        } else {
            d = new Date((long) dateHeures * 1000 * 60 * 60);
        }

        sdfDays.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (chart.getVisibleXRange() < GRAN_4 + 30) {
            x.setGranularity(GRAN_5);
            return sdfDays.format(d);

        } else if (chart.getVisibleXRange() < GRAN_3 + 300) {
            x.setGranularity(GRAN_4);
            return sdfDays.format(d);

        } else if (chart.getVisibleXRange() < GRAN_2 + 300) {
            x.setGranularity(GRAN_3);
            return sdfDays.format(d);

        } else if (chart.getVisibleXRange() < GRAN_1 + 1000) {
            x.setGranularity(GRAN_2);
            d.setDate(d.getDate() + 1); //Pallier probleme affichage (fevrier trop court)
            return sdfMonths.format(d);
        } else {
            x.setGranularity(GRAN_1);
            return sdfMonths.format(d);
        }
    }
}
