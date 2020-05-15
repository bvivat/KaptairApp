package com.example.kaptair.database.InterfacesMesures;

import java.util.Date;
import java.util.List;

// Interface utilisee pour lier les differents DAO
public interface MesureDao {

    List<? extends Mesure> getAll();
    Mesure getByDate(Date date);
    List<? extends Mesure> getAllByDate(Date dateDebut, Date dateFin);
}
