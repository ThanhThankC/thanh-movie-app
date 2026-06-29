package com.example.thanhmovie.utils;

import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.adapter.SuggestAdapter;
import com.example.thanhmovie.model.Movie;

import java.util.List;

public class SearchState {
    public String currentQuery = "";
    public int currentPage = 1;
    public List<Movie> searchResultList;
    public List<Movie> suggestResultList;
    public MovieAdapter searchAdapter;
    public SuggestAdapter suggestAdapter;

    public SearchState(List<Movie> searchList, MovieAdapter searchAdapter,
                       List<Movie> suggestList, SuggestAdapter suggestAdapter) {
        this.searchResultList = searchList;
        this.searchAdapter = searchAdapter;
        this.suggestResultList = suggestList;
        this.suggestAdapter = suggestAdapter;
    }

    public void setCurrentQuery (String query) {currentQuery = query;}
    public void resetPage() { currentPage = 1; }
    public void nextPage()  { currentPage++;   }
}