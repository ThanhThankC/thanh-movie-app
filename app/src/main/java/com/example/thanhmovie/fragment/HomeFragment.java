package com.example.thanhmovie.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private final List<Movie> movieList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.recycler_movies);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        movieAdapter = new MovieAdapter(movieList);
        recyclerView.setAdapter(movieAdapter);

        loadMovies();

        return view;
    }

    private void loadMovies(){
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(Constants.API_KEY, Constants.LANGUAGE_VI, 1)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call,@NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null){
                            int oldSize = movieList.size();
                            movieList.addAll(response.body().getResults());
                            int newSize = movieList.size();
                            movieAdapter.notifyItemRangeInserted(oldSize, newSize - oldSize);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call,@NonNull Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}