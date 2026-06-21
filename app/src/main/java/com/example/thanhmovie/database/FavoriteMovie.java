package com.example.thanhmovie.database;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey
    private final int id;
    private final String title;
    private final String posterPath;
    private final String backdropPath;
    private final String overview;
    private final double voteAverage;
    private final String releaseDate;
    private final double popularity;
    private final long addedAt;

    public FavoriteMovie(int id, String title, String posterPath, String backdropPath, String overview,
                         double voteAverage, String releaseDate, double popularity, long addedAt) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getOverview() { return overview; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public double getPopularity() { return popularity; }
    public long getAddedAt() { return addedAt; }
}
