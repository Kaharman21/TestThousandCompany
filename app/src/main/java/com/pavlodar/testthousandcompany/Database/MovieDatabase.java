package com.pavlodar.testthousandcompany.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = MovieItem.class, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDAO movieDAO();

    private static MovieDatabase instance;

    public static MovieDatabase getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context, MovieDatabase.class, "MovieList").build();
        return instance;
    }
}
