package com.example.kaptair.bluetooth;

public interface TypeDangerDonnees {
    // Definir ici les seuils pour les differentes donnees
    Double PM1_WARNING = 1000.0;
    Double PM1_DANGER = 1000.0;

    Double PM25_WARNING = 10.0;
    Double PM25_DANGER = 25.0;

    Double PM10_WARNING = 25.0;
    Double PM10_DANGER = 50.0;

    Double CO2_WARNING = 800.0;
    Double CO2_DANGER = 2000.0;

    Double TEMP_WARNING = 100.0;
    Double TEMP_DANGER = 100.0;

    Double HUMIDITY_WARNING = 100.0;
    Double HUMIDITY_DANGER = 100.0;
}
