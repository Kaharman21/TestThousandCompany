package com.pavlodar.testthousandcompany.Database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pavlodar.testthousandcompany.Model.MovieListModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM Movie")
    Single<List<MovieItem>> getMovies();

    @Insert (onConflict = OnConflictStrategy.REPLACE) //If conflict foodId, we will update information
    Completable insert(MovieItem... movieItem);


    @Delete
    Single<Integer> deleteMovieList(MovieItem movieItem);

    @Update(onConflict =  OnConflictStrategy.REPLACE)
    Single<Integer> update(MovieItem movieItem);

    @Query("DELETE FROM Movie")
    Single<Integer> deleteAll();
}
