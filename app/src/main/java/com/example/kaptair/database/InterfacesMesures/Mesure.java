package com.example.kaptair.database.InterfacesMesures;

import java.util.Date;

// Interface utilisee pour lier les differentes mesures
public interface Mesure {

    Date getDate();
    float getFloatDate(); // Retourne une date sous forme de float, adaptee a l'utilisation par le graph associe
}
