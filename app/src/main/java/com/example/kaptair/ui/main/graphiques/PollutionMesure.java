package com.example.kaptair.ui.main.graphiques;

import java.util.Date;

public interface PollutionMesure {

    Date getDate();
    double getPm1();
    double getPm25();
    double getPm10();
    double getCo2();
    float getFloatDate();
}
