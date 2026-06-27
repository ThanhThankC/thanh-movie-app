package com.example.thanhmovie.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.database.AppDatabase;
import com.example.thanhmovie.database.FavoriteMovie;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.util.GridSpanUtils;
import com.example.thanhmovie.utils.OnScrollDirectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    List<Movie> favoriteMovies = new ArrayList<>();
    private RecyclerView recyclerView;
    private MovieAdapter favoriteAdapter;
    private OnScrollDirectionListener scrollListener;
    private boolean isHeaderVisible = true;
    private int currentSortIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = AppDatabase.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSpinnerSort(view, getContext());

        recyclerView = view.findViewById(R.id.recycler_favorite);
        int spanCount = GridSpanUtils.calculateSpanCount(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));

        favoriteAdapter = new MovieAdapter(favoriteMovies);
        recyclerView.setAdapter(favoriteAdapter);

        setupScrollBehavior(view);
        loadFavorites();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollDirectionListener) {
            scrollListener = (OnScrollDirectionListener) context;
        }
    }

    private void setupSpinnerSort(View view, Context context){
        Spinner spinnerSort = view.findViewById(R.id.spinner_favorite_sort);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                context, R.array.sort_options, R.layout.spinner_item_selected);
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortIndex = position;
                loadFavorites();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupScrollBehavior(View view){
        Spinner spinnerSort = view.findViewById(R.id.spinner_favorite_sort);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && isHeaderVisible) {
                    int headerHeight = spinnerSort.getHeight() + 80;
                    spinnerSort.animate().translationY(-headerHeight).setDuration(200).start();
                    isHeaderVisible = false;
                    if (scrollListener != null) scrollListener.onScrollUp();
                } else if (dy < 0 && !isHeaderVisible) {
                    spinnerSort.animate().translationY(0).setDuration(200).start();
                    isHeaderVisible = true;
                    if (scrollListener != null) scrollListener.onScrollDown();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFavorites();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFavorites(){
        executor.execute(() -> {
            var dao = db.favoriteDao();
            List<FavoriteMovie> favorites;

            switch (currentSortIndex) {
                case 1: favorites = dao.getAllSortByOldest(); break;
                case 2: favorites = dao.getAllSortByTitle(); break;
                case 3: favorites = dao.getAllSortByRating(); break;
                case 4: favorites = dao.getAllSortByPopularity(); break;
                default: favorites = dao.getAllSortByNewest();
            }

            List<Movie> newList = new ArrayList<>();
            for (FavoriteMovie f : favorites) {
                newList.add(convertToMovie(f));
            }

            requireActivity().runOnUiThread(() -> {
                favoriteMovies.clear();
                favoriteMovies.addAll(newList);
                favoriteAdapter.notifyDataSetChanged();

                View view = getView();
                if (view == null) return;
                TextView txtEmpty = view.findViewById(R.id.txt_empty_favorite);
                boolean isEmpty = favoriteMovies.isEmpty();
                txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
            });
        });
    }

    private Movie convertToMovie(FavoriteMovie fav){
        Movie movie = new Movie();
        movie.setId(fav.getId());
        movie.setTitle(fav.getTitle());
        movie.setPosterPath(fav.getPosterPath());
        movie.setBackdropPath(fav.getBackdropPath());
        movie.setOverview(fav.getOverview());
        movie.setVoteAverage(fav.getVoteAverage());
        movie.setReleaseDate(fav.getReleaseDate());
        movie.setPopularity(fav.getPopularity());
        return movie;
    }
}