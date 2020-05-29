package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.database.InterfacesMesures.PollutionMesure;

import java.util.Date;

@Entity
public class MoyenneYearMesuresPollution implements PollutionMesure {

    @PrimaryKey
    public Date date;

    public double pm1;
    public double pm25;
    public double pm10;
    public double co2;
    public int nbMesures;

    public double maxPm1;
    public double maxPm25;
    public double maxPm10;
    public double maxCo2;
    public double maxLatitude;
    public double maxLongitude;

    public double minPm1;
    public double minPm25;
    public double minPm10;
    public double minCo2;
    public double minLatitude;
    public double minLongitude;

    public MoyenneYearMesuresPollution(Date date, double pm1, double pm25, double pm10, double co2) {
        this.date = new Date(date.getYear(), date.getMonth(), date.getDate()); //On garde à l'année près
        this.pm1 = pm1;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.co2 = co2;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneYearMesuresPollution(MesurePollution m) {

        this.date = new Date(m.date.getYear(), m.date.getMonth(), m.date.getDate()); //On garde à l'année près
        this.pm1 = m.pm1;
        this.pm25 = m.pm25;
        this.pm10 = m.pm10;
        this.co2 = m.co2;
        this.nbMesures = 1;

        this.maxPm1 = m.pm1;
        this.maxPm25 = m.pm25;
        this.maxPm10 = m.pm10;
        this.maxCo2 = m.co2;
        this.maxLatitude = m.latitude;
        this.maxLongitude = m.longitude;

        this.minPm1 = m.pm1;
        this.minPm25 = m.pm25;
        this.minPm10 = m.pm10;
        this.minCo2 = m.co2;
        this.minLatitude = m.latitude;
        this.minLongitude = m.longitude;
        
    }

    @Ignore
    public MoyenneYearMesuresPollution(MoyenneYearMesuresPollution mOld, MoyenneYearMesuresPollution mNew) {

        this.date = mOld.date;
        this.nbMesures = mOld.nbMesures + 1;
        this.pm1 = (mOld.pm1 * mOld.nbMesures + mNew.pm1) / this.nbMesures;
        this.pm25 = (mOld.pm25 * mOld.nbMesures + mNew.pm25) / this.nbMesures;
        this.pm10 = (mOld.pm10 * mOld.nbMesures + mNew.pm10) / this.nbMesures;
        this.co2 = (mOld.co2 * mOld.nbMesures + mNew.co2) / this.nbMesures;


        // TODO tout changer
        this.maxPm1 = mNew.pm1 > mOld.pm1 ? mNew.pm1 : mOld.pm1 ;
        this.maxPm25 = mNew.pm25 > mOld.pm25 ? mNew.pm25 : mOld.pm25 ;
        this.maxPm10 = mNew.pm10 > mOld.pm10 ? mNew.pm10 : mOld.pm10 ;
        this.maxCo2 = mNew.co2 > mOld.co2 ? mNew.co2 : mOld.co2 ;
        this.maxLatitude = mNew.maxLatitude > mOld.maxLatitude ? mNew.maxLatitude : mOld.maxLatitude ;
        this.maxLongitude = mNew.maxLongitude > mOld.maxLongitude ? mNew.maxLongitude : mOld.maxLongitude ;
        
        this.minPm1 = mNew.pm1 < mOld.pm1 ? mNew.pm1 : mOld.pm1 ;
        this.minPm25 = mNew.pm25 < mOld.pm25 ? mNew.pm25 : mOld.pm25 ;
        this.minPm10 = mNew.pm10 < mOld.pm10 ? mNew.pm10 : mOld.pm10 ;
        this.minCo2 = mNew.co2 < mOld.co2 ? mNew.co2 : mOld.co2 ;
        this.minLatitude = mNew.minLatitude < mOld.minLatitude ? mNew.minLatitude : mOld.minLatitude ;
        this.minLongitude = mNew.minLongitude < mOld.minLongitude ? mNew.minLongitude : mOld.minLongitude ;
    }

    @Ignore
    @Override
    public float getFloatDate() {
        int offset = 1 + date.getTimezoneOffset() / 60;
        float result = (date.getTime() - new Date(date.getYear(), 0, 1).getTime()) / 1000 / 60 / 60 - offset; // On ne garde que le jour de la mesure (independament de l'annee)
        return result;
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
        return maxLatitude;
    }

    @Override
    public double getLongitude() {
        return maxLongitude;
    }

}
