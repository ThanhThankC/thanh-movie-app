package com.example.thanhmovie.api;

import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.model.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("with_genres") int genreId,
            @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page
    );
}