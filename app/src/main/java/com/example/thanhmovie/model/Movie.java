package com.example.thanhmovie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public String getBackdropPath() { return backdropPath; }
    public double getPopularity() { return popularity; }
    public List<Integer> getGenreIds() { return genreIds; }
}