package com.example.kaptair.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kaptair.database.InterfacesMesures.MesureDao;

import java.util.Date;
import java.util.List;

@Dao
public interface MesureMeteoDao extends MesureDao {

    @Query("SELECT * FROM MesureMeteo")
    List<MesureMeteo> getAll();

    @Query("SELECT * FROM MesureMeteo WHERE date = :date")
    MesureMeteo getByDate(Date date);

    @Query("SELECT * FROM MesureMeteo WHERE date >= :dateDebut AND date < :dateFin")
    List<MesureMeteo> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MesureMeteo... mesures);

    @Update
    void update(MesureMeteo mesure);

    @Delete
    void delete(MesureMeteo mesure);
}
