package com.example.kaptair.database.InterfacesMesures;

import java.util.Date;

public interface PollutionMesure extends Mesure{

    Date getDate();
    double getPm1();
    double getPm25();
    double getPm10();
    double getCo2();
    float getFloatDate();
}
