package com.example.thanhmovie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public List<Movie> getResults() {return results;}
    public int getTotalPages() {return totalPages;}
    public int getTotalResults() {return totalResults;}
}
