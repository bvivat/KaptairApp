package com.example.kaptair.database.interfacesMesures;

import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 *
 * Interface utilisee pour lier les differents DAO
 */
public interface MesureDao {

    List<? extends Mesure> getAll();
    Mesure getByDate(Date date);
    List<? extends Mesure> getAllByDate(Date dateDebut, Date dateFin);
}
