package com.example.kaptair.database.interfacesMesures;

import java.util.Date;

public interface MeteoMesure extends Mesure{

    Date getDate();
    double getTemperature();
    double getHumidity();
    float getFloatDate();
}
