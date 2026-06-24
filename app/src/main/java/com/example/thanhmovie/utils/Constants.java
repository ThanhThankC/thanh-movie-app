package com.example.thanhmovie.utils;

import java.util.List;

public class Constants {
    public static final String API_KEY = "98eb6e953b75d453b78cce03db9f03df";
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    public static final String LANGUAGE_VI = "vi-VN";
    public static final String LANGUAGE_EN = "en-US";

    public static String getGenreName(List<Integer> genreIds){
        if (genreIds == null || genreIds.isEmpty()) return "Khác";
        int id = genreIds.get(0);

        if (id == 28) return "Hành động";
        if (id == 35) return "Hài";
        if (id == 10749) return "Tình cảm";
        if (id == 27) return "Kinh dị";
        if (id == 18) return "Tâm lý";
        return "Khác";
    }
}