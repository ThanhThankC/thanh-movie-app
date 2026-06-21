package com.example.thanhmovie.model;

public class GenreModel {
    private final int genreId;
    private final String genreName;

    public GenreModel(int genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public int getGenreId() { return genreId; }
    public String getGenreName() { return genreName; }
}
