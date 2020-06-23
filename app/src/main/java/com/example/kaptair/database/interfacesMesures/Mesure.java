package com.example.kaptair.database.interfacesMesures;

import java.util.Date;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 *
 * Interface utilisee pour lier les differentes mesures
 */
public interface Mesure {

    Date getDate();
    float getFloatDate(); // Retourne une date sous forme de float, adaptee a l'utilisation par le graph associe
}
