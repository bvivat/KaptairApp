package com.example.kaptair.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kaptair.database.interfacesMesures.MesureDao;

import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
@Dao
public interface MoyenneYearMesuresPollutionDao extends MesureDao {

    @Query("SELECT * FROM MoyenneYearMesuresPollution")
    List<MoyenneYearMesuresPollution> getAll();

    @Query("SELECT * FROM MoyenneYearMesuresPollution WHERE date = :date")
    MoyenneYearMesuresPollution getByDate(Date date);

    @Query("SELECT * FROM MoyenneYearMesuresPollution WHERE date >= :dateDebut AND date < :dateFin")
    List<MoyenneYearMesuresPollution> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MoyenneYearMesuresPollution... mesure);

    @Update
    void update(MoyenneYearMesuresPollution mesure);

    @Delete
    void delete(MoyenneYearMesuresPollution mesure);
}
