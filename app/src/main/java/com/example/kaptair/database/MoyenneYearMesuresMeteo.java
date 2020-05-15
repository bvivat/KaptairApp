package com.example.kaptair.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.kaptair.database.InterfacesMesures.MeteoMesure;

import java.util.Date;

@Entity
public class MoyenneYearMesuresMeteo implements MeteoMesure {

    @PrimaryKey
    public Date date;

    public double temperature;
    public double humidity;
    public int nbMesures;

    public MoyenneYearMesuresMeteo(Date date, double temperature, double humidity) {
        this.date = new Date(date.getYear(), date.getMonth(), date.getDate()); //On garde au jour près
        this.temperature = temperature;
        this.humidity = humidity;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneYearMesuresMeteo(MesureMeteo m) {

        this.date = new Date(m.date.getYear(), m.date.getMonth(), m.date.getDate()); //On garde au jour près
        this.temperature = m.temperature;
        this.humidity = m.humidity;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneYearMesuresMeteo(MoyenneYearMesuresMeteo mOld, MoyenneYearMesuresMeteo mNew) {

        this.date = mOld.date;
        this.nbMesures = mOld.nbMesures + 1;
        this.temperature = (mOld.temperature * mOld.nbMesures + mNew.temperature) / this.nbMesures;
        this.humidity = (mOld.humidity * mOld.nbMesures + mNew.humidity) / this.nbMesures;

    }

    @Override
    @Ignore
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
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getNbMesures() {
        return nbMesures;
    }

}
