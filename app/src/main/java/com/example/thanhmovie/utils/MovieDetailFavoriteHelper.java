package com.example.thanhmovie.utils;

import android.content.Context;

import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.database.AppDatabase;
import com.example.thanhmovie.database.FavoriteMovie;
import com.example.thanhmovie.model.Movie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFavoriteHelper {

    public interface OnFavoriteStateListener {
        void onStateChanged(boolean isFavorite, boolean isClicked);
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void checkIfFavorite(Context context, Movie currentMovie, OnFavoriteStateListener listener){
        AppDatabase db = AppDatabase.getInstance(context);
        executor.execute(() -> {
            FavoriteMovie existing = db.favoriteDao().getFavoriteMovie(currentMovie.getId());
            listener.onStateChanged(existing != null, false);
        });
    }

    public static void toggleFavorite(Context context, Movie currentMovie, boolean isFavorite, OnFavoriteStateListener listener){
        AppDatabase db = AppDatabase.getInstance(context);
        executor.execute(() -> {
            if (isFavorite){
                FavoriteMovie existing = db.favoriteDao().getFavoriteMovie(currentMovie.getId());
                db.favoriteDao().delete(existing);
                listener.onStateChanged(false, true);
            }
            else {
                if (currentMovie == null) return;
                fetchBothLanguageAndSave(db, currentMovie, listener);
            }
        });
    }

    private static void fetchBothLanguageAndSave(AppDatabase db, Movie currentMovie, OnFavoriteStateListener listener){
        RetrofitClient.getInstance().getApiService()
                .getMovieDetail(currentMovie.getId(), Constants.API_KEY, Constants.LANGUAGE_VI)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> responseVi) {
                        if (!responseVi.isSuccessful() || responseVi.body() == null) return;
                        String titleVi = responseVi.body().getTitle();
                        String overviewVi = responseVi.body().getOverview();

                        RetrofitClient.getInstance().getApiService()
                                .getMovieDetail(currentMovie.getId(), Constants.API_KEY, Constants.LANGUAGE_EN)
                                .enqueue(new Callback<>() {
                                    @Override
                                    public void onResponse(Call<Movie> call, Response<Movie> responseEn) {
                                        if (!responseEn.isSuccessful() || responseEn.body() == null) return;
                                        String titleEn = responseEn.body().getTitle();
                                        String overviewEn = responseEn.body().getOverview();

                                        FavoriteMovie favMovie = new FavoriteMovie(
                                                currentMovie.getId(), titleVi, titleEn,
                                                currentMovie.getPosterPath(), currentMovie.getBackdropPath(),
                                                overviewVi, overviewEn,
                                                currentMovie.getVoteAverage(), currentMovie.getReleaseDate(),
                                                currentMovie.getPopularity(), System.currentTimeMillis()
                                        );
                                        executor.execute(() -> {
                                            db.favoriteDao().insert(favMovie);
                                            listener.onStateChanged(true, true);
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<Movie> call, Throwable t) {}
                                });
                    }
                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {}
                });
    }
}
