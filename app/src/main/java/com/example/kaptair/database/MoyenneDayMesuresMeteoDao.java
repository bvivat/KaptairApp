package com.example.kaptair.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface MoyenneDayMesuresMeteoDao extends MesureDao{

    @Query("SELECT * FROM MoyenneDayMesuresMeteo")
    List<MoyenneDayMesuresMeteo> getAll();

    @Query("SELECT * FROM MoyenneDayMesuresMeteo WHERE date = :date")
    MoyenneDayMesuresMeteo getByDate(Date date);

    @Query("SELECT * FROM MoyenneDayMesuresMeteo WHERE date >= :dateDebut AND date < :dateFin")
    List<MoyenneDayMesuresMeteo> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MoyenneDayMesuresMeteo... mesure);

    @Update
    void update(MoyenneDayMesuresMeteo mesure);

    @Delete
    void delete(MoyenneDayMesuresMeteo mesure);

}
