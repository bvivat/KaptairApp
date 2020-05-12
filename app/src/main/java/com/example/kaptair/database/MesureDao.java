package com.example.kaptair.database;

import com.example.kaptair.ui.main.graphiques.Mesure;

import java.util.Date;
import java.util.List;

public interface MesureDao {

    List<? extends Mesure> getAll();
    Mesure getByDate(Date date);
    List<? extends Mesure> getAllByDate(Date dateDebut, Date dateFin);
}
