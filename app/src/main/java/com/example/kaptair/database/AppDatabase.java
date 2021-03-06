package com.example.kaptair.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
@Database(entities = {MesurePollution.class,MoyenneDayMesuresPollution.class,MoyenneYearMesuresPollution.class, MesureMeteo.class, MoyenneDayMesuresMeteo.class,MoyenneYearMesuresMeteo.class}, version=1)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static final String DB_NAME = "MesuresDB.db";

    public abstract MesurePollutionDao mesurePollutionDao();
    public abstract MoyenneDayMesuresPollutionDao moyenneDayMesuresPollutionDao();
    public abstract MoyenneYearMesuresPollutionDao moyenneYearMesuresPollutionDao();

    public abstract MesureMeteoDao mesureMeteoDao();
    public abstract MoyenneDayMesuresMeteoDao moyenneDayMesuresMeteoDao();
    public abstract MoyenneYearMesuresMeteoDao moyenneYearMesuresMeteoDao();

    public static AppDatabase getInstance(Context context) {
        // Singleton pattern, pour n'avoir qu'une instance pour l'ensemble des classes
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
