package com.example.thanhmovie.database;


import android.content.Context;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.thanhmovie.utils.LocaleManager;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey
    private final int id;
    private final String titleVi;
    private final String titleEn;
    private final String posterPath;
    private final String backdropPath;
    private final String overviewVi;
    private final String overviewEn;
    private final double voteAverage;
    private final String releaseDate;
    private final double popularity;
    private final long addedAt;

    public FavoriteMovie(int id, String titleVi, String titleEn, String posterPath, String backdropPath,
                         String overviewVi, String overviewEn, double voteAverage, String releaseDate, double popularity, long addedAt) {
        this.id = id;
        this.titleVi = titleVi;
        this.titleEn = titleEn;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overviewVi = overviewVi;
        this.overviewEn = overviewEn;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }
    public String getTitleVi() { return titleVi; }
    public String getTitleEn() { return titleEn; }
    public String getTitleByLocale(Context context) {
        String lang = LocaleManager.getLocale(context);
        return lang.equals("en") ? titleEn : titleVi;
    }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getOverviewVi() { return overviewVi; }
    public String getOverviewEn() { return overviewEn; }
    public String getOverviewByLocale(Context context) {
        String lang = LocaleManager.getLocale(context);
        return lang.equals("en") ? overviewEn : overviewVi;
    }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public double getPopularity() { return popularity; }
    public long getAddedAt() { return addedAt; }
}
