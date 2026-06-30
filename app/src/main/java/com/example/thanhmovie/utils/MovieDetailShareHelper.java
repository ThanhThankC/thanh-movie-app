package com.example.thanhmovie.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.example.thanhmovie.R;
import com.example.thanhmovie.model.Movie;

public class MovieDetailShareHelper {
    private static final long SHARE_CLICKED_DELAY = 600;

    public static void handleShareButtonClick(ImageView iconShare, Context context, Movie currentMovie){
        iconShare.setImageTintList(ColorStateList
                .valueOf(ContextCompat.getColor(context, R.color.light_green)));

        shareMovie(context, currentMovie);

        new Handler().postDelayed(() -> {
            iconShare.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.white)));
        }, SHARE_CLICKED_DELAY);
    }

    private static void shareMovie(Context context, Movie currentMovie){
        String shareText = context.getString(R.string.share_movie_text,
                currentMovie.getTitle(),
                currentMovie.getVoteAverage(),
                currentMovie.getOverview()
        );
        String chooserTitle = context.getString(R.string.share_movie_title);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, chooserTitle));
    }
}
