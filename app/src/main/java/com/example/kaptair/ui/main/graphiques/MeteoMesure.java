package com.example.kaptair.ui.main.graphiques;

import java.util.Date;

public interface MeteoMesure {

    Date getDate();
    double getTemperature();
    double getHumidity();
    float getFloatDate();
}
