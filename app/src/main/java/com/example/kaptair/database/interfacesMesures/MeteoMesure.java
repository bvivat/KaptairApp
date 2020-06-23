package com.example.kaptair.database.interfacesMesures;

import java.util.Date;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public interface MeteoMesure extends Mesure{

    Date getDate();
    double getTemperature();
    double getHumidity();
    float getFloatDate();
}
