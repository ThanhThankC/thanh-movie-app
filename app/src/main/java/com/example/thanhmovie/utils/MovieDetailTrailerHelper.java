package com.example.thanhmovie.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.thanhmovie.R;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.model.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailTrailerHelper {
    private static final long SHARE_CLICKED_DELAY = 500;

    public interface OnTrailerResultListener {
        void onTrailerNotFound();
    }

    public static void loadAndOpenTrailer(Context context, int movieId, OnTrailerResultListener listener) {
        RetrofitClient.getInstance().getApiService()
                .getMovieVideos(movieId, Constants.API_KEY)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        String videoKey = findBestTrailerKey(response.body().getResults());

                        if (videoKey != null)
                            openYoutube(context, videoKey);
                        else
                            listener.onTrailerNotFound();
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static String findBestTrailerKey(List<VideoResponse.VideoResult> videos){
        if (videos == null) return null;
        for (var v: videos){
            if ("YouTube".equals(v.getSite()) && ("Trailer").equals(v.getType())) return v.getKey();
        }
        for (var v: videos){
            if ("YouTube".equals(v.getSite())) return v.getKey();
        }
        return null;
    }

    private static void openYoutube(Context context, String videoKey){
        try{
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
            context.startActivity(appIntent);
        }
        catch (ActivityNotFoundException ex){
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoKey));
            context.startActivity(webIntent);
        }
    }

    public static void handleTrailerButtonClick(Context context, ImageView iconTrailer){
        iconTrailer.setImageTintList(ColorStateList
                .valueOf(ContextCompat.getColor(context, R.color.light_green)));

        new Handler().postDelayed(() -> {
            iconTrailer.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.white)));
        }, SHARE_CLICKED_DELAY);
    }
}
