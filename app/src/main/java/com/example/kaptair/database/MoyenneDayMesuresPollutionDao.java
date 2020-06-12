package com.example.kaptair.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kaptair.database.interfacesMesures.MesureDao;

import java.util.Date;
import java.util.List;

@Dao
public interface MoyenneDayMesuresPollutionDao extends MesureDao {

    @Query("SELECT * FROM MoyenneDayMesuresPollution")
    List<MoyenneDayMesuresPollution> getAll();

    @Query("SELECT * FROM MoyenneDayMesuresPollution WHERE date = :date")
    MoyenneDayMesuresPollution getByDate(Date date);

    @Query("SELECT * FROM MoyenneDayMesuresPollution WHERE date >= :dateDebut AND date < :dateFin")
    List<MoyenneDayMesuresPollution> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MoyenneDayMesuresPollution... mesure);

    @Update
    void update(MoyenneDayMesuresPollution mesure);

    @Delete
    void delete(MoyenneDayMesuresPollution mesure);

}
