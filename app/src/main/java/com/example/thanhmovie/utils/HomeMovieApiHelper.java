package com.example.thanhmovie.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMovieApiHelper {

    public interface OnSlideLoadedListener {
        void onLoaded(int count);
    }

    public static void loadSlideMovies(Context context , List<Movie> targetList,
                                       ViewPager2 viewPagerSlide, boolean isAdded, OnSlideLoadedListener loader){
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(Constants.API_KEY, Constants.getApiLanguage(context), 1)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (!isAdded || context == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            List<Movie> allMovies = response.body().getResults();

                            targetList.clear();
                            for (int i = 0; i < 7 && i < allMovies.size(); i++){
                                targetList.add(allMovies.get(i));
                            }

                            loader.onLoaded(targetList.size());

                            if (!targetList.isEmpty()) {
                                int startPosition = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % targetList.size();
                                viewPagerSlide.setCurrentItem(startPosition, false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context,context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadMoviesByGenre(Context context, int genreId, List<Movie> targetList, MovieAdapter adapter, boolean isAdded){
        RetrofitClient.getInstance().getApiService()
                .getMoviesByGenre(Constants.API_KEY, Constants.getApiLanguage(context), genreId, 1)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (!isAdded || context == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            targetList.addAll(response.body().getResults());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
