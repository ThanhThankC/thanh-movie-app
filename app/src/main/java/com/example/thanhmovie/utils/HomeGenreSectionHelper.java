package com.example.thanhmovie.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.model.GenreModel;
import com.example.thanhmovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class HomeGenreSectionHelper {

    public interface OnMovieLoadRequest{
        void load(int genreId, List<Movie> list, MovieAdapter adapter);
    }

    public static List<GenreModel> buildGenreList(Context context) {
        List<GenreModel> genres = new ArrayList<>();
        genres.add(new GenreModel(16,    context.getString(R.string.genre_animation)));
        genres.add(new GenreModel(27,    context.getString(R.string.genre_horror)));
        genres.add(new GenreModel(28,    context.getString(R.string.genre_action)));
        genres.add(new GenreModel(10749, context.getString(R.string.genre_romance)));
        genres.add(new GenreModel(35,    context.getString(R.string.genre_comedy)));
        genres.add(new GenreModel(878,   context.getString(R.string.genre_fiction)));
        return genres;
    }

    public static void renderGenreSections(Context context, LinearLayout layoutGenresContainer, List<GenreModel> genreSections, OnMovieLoadRequest loader){
        LayoutInflater inflater = LayoutInflater.from(context);

        for (GenreModel genre : genreSections) {
            View view = inflater.inflate(R.layout.item_genre_section, layoutGenresContainer, false);

            TextView txtTitle = view.findViewById(R.id.txt_genre_title);
            RecyclerView recyclerGenre = view.findViewById(R.id.recycler_genre);

            txtTitle.setText(genre.getGenreName());

            recyclerGenre.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            );

            List<Movie> genreMovieList = new ArrayList<>();
            MovieAdapter movieAdapter = new MovieAdapter(genreMovieList);
            recyclerGenre.setAdapter(movieAdapter);

            loader.load(genre.getGenreId(), genreMovieList, movieAdapter);

            layoutGenresContainer.addView(view);
        }
    }
}
