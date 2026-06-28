package com.example.thanhmovie.database;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insert(FavoriteMovie movie);

    @Delete
    void delete(FavoriteMovie movie);

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt ASC")
    List<FavoriteMovie> getAllSortByOldest();

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    List<FavoriteMovie> getAllSortByNewest();

    @Query("SELECT * FROM favorite_movies ORDER BY titleVi ASC")
    List<FavoriteMovie> getAllSortByTitleVi();

    @Query("SELECT * FROM favorite_movies ORDER BY titleEn ASC")
    List<FavoriteMovie> getAllSortByTitleEn();

    @Query("SELECT * FROM favorite_movies ORDER BY voteAverage DESC")
    List<FavoriteMovie> getAllSortByRating();

    @Query("SELECT * FROM favorite_movies ORDER BY popularity DESC")
    List<FavoriteMovie> getAllSortByPopularity();

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1")
    FavoriteMovie getFavoriteMovie(int movieId);
}
