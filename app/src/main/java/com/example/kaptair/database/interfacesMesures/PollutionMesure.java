package com.example.kaptair.database.interfacesMesures;

import java.util.Date;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public interface PollutionMesure extends Mesure{

    Date getDate();
    double getPm1();
    double getPm25();
    double getPm10();
    double getCo2();
    float getFloatDate();

    double getLatitude();
    double getLongitude();
}
