package com.example.kaptair.ui.main.graphiques;

import java.util.Date;

public interface MeteoMesure extends Mesure{

    Date getDate();
    double getTemperature();
    double getHumidity();
    float getFloatDate();
}
