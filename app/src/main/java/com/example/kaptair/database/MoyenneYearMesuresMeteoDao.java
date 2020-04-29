package com.example.kaptair.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface MoyenneYearMesuresMeteoDao {

    @Query("SELECT * FROM MoyenneYearMesuresMeteo")
    List<MoyenneYearMesuresMeteo> getAll();

    @Query("SELECT * FROM MoyenneYearMesuresMeteo WHERE date = :date")
    MoyenneYearMesuresMeteo getByDate(Date date);

    @Query("SELECT * FROM MoyenneYearMesuresMeteo WHERE date >= :dateDebut AND date < :dateFin")
    List<MoyenneYearMesuresMeteo> getAllByDate(Date dateDebut, Date dateFin);

    @Insert
    void insertAll(MoyenneYearMesuresMeteo... mesure);

    @Update
    void update(MoyenneYearMesuresMeteo mesure);

    @Delete
    void delete(MoyenneYearMesuresMeteo mesure);
}
