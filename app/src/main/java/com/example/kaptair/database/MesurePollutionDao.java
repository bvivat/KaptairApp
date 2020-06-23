package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.database.interfacesMesures.MesureDao;

import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
@Dao
public interface MesurePollutionDao extends MesureDao {

    @Query("SELECT * FROM MesurePollution")
    List<MesurePollution> getAll();

    @Query("SELECT * FROM MesurePollution WHERE date = :date")
    MesurePollution getByDate(Date date);

    @Query("SELECT * FROM MesurePollution WHERE date >= :dateDebut AND date < :dateFin")
    List<MesurePollution> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MesurePollution... mesure);

    @Update
    void update(MesurePollution mesure);

    @Delete
    void delete(MesurePollution mesure);
}
