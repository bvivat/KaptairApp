package com.example.kaptair.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 *
 * Classe requise pour convertir une date en Long stockable dans la BD
 */
public class Converter {

        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }

}
