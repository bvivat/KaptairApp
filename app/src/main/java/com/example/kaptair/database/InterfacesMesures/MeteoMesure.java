package com.example.kaptair.database.InterfacesMesures;

import java.util.Date;

public interface MeteoMesure extends Mesure{

    Date getDate();
    double getTemperature();
    double getHumidity();
    float getFloatDate();
}
