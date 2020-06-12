package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.database.interfacesMesures.PollutionMesure;

import java.util.Date;

@Entity
public class MoyenneDayMesuresPollution implements PollutionMesure {

    @PrimaryKey
    public Date date;

    public double pm1;
    public double pm25;
    public double pm10;
    public double co2;
    public int nbMesures;

    public MoyenneDayMesuresPollution(Date date, double pm1, double pm25, double pm10, double co2) {
        int minutes = date.getMinutes() - (date.getMinutes() % 5); //On garde aux 5 minutes près
        this.date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), minutes);
        this.pm1 = pm1;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.co2 = co2;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneDayMesuresPollution(MesurePollution m) {

        int minutes = m.date.getMinutes() - (m.date.getMinutes() % 5); //On garde aux 5 minutes près
        this.date = new Date(m.date.getYear(), m.date.getMonth(), m.date.getDate(), m.date.getHours(), minutes);
        this.pm1 = m.pm1;
        this.pm25 = m.pm25;
        this.pm10 = m.pm10;
        this.co2 = m.co2;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneDayMesuresPollution(MoyenneDayMesuresPollution mOld, MoyenneDayMesuresPollution mNew) {

        this.date = mOld.date;
        this.nbMesures = mOld.nbMesures + 1;
        this.pm1 = (mOld.pm1 * mOld.nbMesures + mNew.pm1) / this.nbMesures;
        this.pm25 = (mOld.pm25 * mOld.nbMesures + mNew.pm25) / this.nbMesures;
        this.pm10 = (mOld.pm10 * mOld.nbMesures + mNew.pm10) / this.nbMesures;
        this.co2 = (mOld.co2 * mOld.nbMesures + mNew.co2) / this.nbMesures;
    }

    @Ignore
    @Override
    public float getFloatDate() {
        Date day = new Date(date.getYear(), date.getMonth(), date.getDate());
        return (date.getTime() - day.getTime()) / 1000; // On ne garde que le nombre d'heure de la mesure (independament du jour)
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public double getPm1() {
        return pm1;
    }

    public void setPm1(double pm1) {
        this.pm1 = pm1;
    }

    @Override
    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    @Override
    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    @Override
    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public int getNbMesures() {
        return nbMesures;
    }


    @Override
    public double getLatitude() {
        return 0.0;
    }

    @Override
    public double getLongitude() {
        return 0.0;
    }

}
