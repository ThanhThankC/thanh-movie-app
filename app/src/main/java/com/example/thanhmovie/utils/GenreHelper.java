package com.example.thanhmovie.utils;

import android.content.Context;
import com.example.thanhmovie.R;
import java.util.List;

public class GenreHelper {

    public static String getGenreName(Context context, List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return context.getString(R.string.genre_other);
        int id = genreIds.get(0);

        if (id == 28) return context.getString(R.string.genre_action);
        if (id == 12) return context.getString(R.string.genre_adventure);
        if (id == 16) return context.getString(R.string.genre_animation);
        if (id == 35) return context.getString(R.string.genre_comedy);
        if (id == 80) return context.getString(R.string.genre_crime);
        if (id == 99) return context.getString(R.string.genre_documentary);
        if (id == 18) return context.getString(R.string.genre_drama);
        if (id == 10751) return context.getString(R.string.genre_family);
        if (id == 14) return context.getString(R.string.genre_fantasy);
        if (id == 27) return context.getString(R.string.genre_horror);
        if (id == 9648) return context.getString(R.string.genre_mystery);
        if (id == 10749) return context.getString(R.string.genre_romance);
        if (id == 878) return context.getString(R.string.genre_scifi);
        if (id == 53) return context.getString(R.string.genre_thriller);
        if (id == 10752) return context.getString(R.string.genre_war);
        return context.getString(R.string.genre_other);
    }
}