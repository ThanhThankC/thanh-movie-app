package com.example.thanhmovie.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.thanhmovie.R;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;
import com.example.thanhmovie.utils.MovieDetailFavoriteHelper;
import com.example.thanhmovie.utils.GenreHelper;
import com.example.thanhmovie.utils.LocaleManager;
import com.example.thanhmovie.utils.MovieDetailShareHelper;
import com.example.thanhmovie.utils.MovieDetailTrailerHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie currentMovie;
    private boolean isFavorite;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        currentMovie = (Movie) getIntent().getSerializableExtra("movie_object");
        setupDetailScreen();
        setupFavoriteButton();
        setupTrailerButton();
        setupShareButton();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupDetailScreen(){
        TextView txtTitle = findViewById(R.id.txt_detail_title);
        TextView txtRating = findViewById(R.id.txt_detail_rating);
        TextView txtGenre = findViewById(R.id.txt_detail_genre);
        TextView txtReleaseDate = findViewById(R.id.txt_detail_release);
        TextView txtOverview = findViewById(R.id.txt_detail_overview);
        ImageView imgPoster = findViewById(R.id.img_detail_poster);
        ImageView imgBackdrop = findViewById(R.id.img_detail_backdrop);
        ImageView imgStarFill = findViewById(R.id.img_star_fill);

        if (currentMovie == null) return;

        txtTitle.setText(currentMovie.getTitle());
        txtRating.setText(String.format("%.1f/10", currentMovie.getVoteAverage()));
        txtReleaseDate.setText(currentMovie.getReleaseDate());
        txtGenre.setText(GenreHelper.getGenreName(this, currentMovie.getGenreIds()));
        txtOverview.setText(currentMovie.getOverview());

        double rating = currentMovie.getVoteAverage();
        int level = (int) (rating / 10f * 10000);
        imgStarFill.setImageLevel(level);

        Glide.with(this).load(Constants.IMAGE_BASE_URL + currentMovie.getPosterPath())
                .transform(new CenterCrop(), new RoundedCorners(20)).into(imgPoster);
        Glide.with(this).load(Constants.IMAGE_BASE_URL + currentMovie.getBackdropPath()).into(imgBackdrop);
    }

    private void setupFavoriteButton(){
        ImageView iconFavorite = findViewById(R.id.icon_btn_favorite);
        TextView txtFavorite = findViewById(R.id.txt_btn_favorite);
        LinearLayout btnFavorite = findViewById(R.id.btn_favorite);

        MovieDetailFavoriteHelper.checkIfFavorite(this, currentMovie, new MovieDetailFavoriteHelper.OnFavoriteStateListener() {
            @Override
            public void onStateChanged(boolean isFav, boolean isClicked) {
                runOnUiThread(() -> {isFavorite = isFav; updateFavoriteVisual(iconFavorite, txtFavorite, isClicked);});
            }
        });

        btnFavorite.setOnClickListener(v -> MovieDetailFavoriteHelper.toggleFavorite(this, currentMovie, isFavorite,
            new MovieDetailFavoriteHelper.OnFavoriteStateListener() {
                @Override
                public void onStateChanged(boolean isFav, boolean isClicked) {
                    runOnUiThread(() -> {isFavorite = isFav; updateFavoriteVisual(iconFavorite, txtFavorite, isClicked);});
                }
            }));
    }

    private void setupTrailerButton(){
        LinearLayout btnTrailer = findViewById(R.id.btn_trailer);
        ImageView iconTrailer = findViewById(R.id.icon_trailer);

        btnTrailer.setOnClickListener(v -> {
            MovieDetailTrailerHelper.handleTrailerButtonClick(this, iconTrailer);
            MovieDetailTrailerHelper.loadAndOpenTrailer(this, currentMovie.getId(), new MovieDetailTrailerHelper.OnTrailerResultListener() {
                @Override
                public void onTrailerNotFound() {
                    runOnUiThread(() -> Toast.makeText(MovieDetailActivity.this, getString(R.string.trailer_not_found), Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void setupShareButton(){
        LinearLayout btnShare = findViewById(R.id.btn_share);
        ImageView iconShare = findViewById(R.id.icon_share);

        btnShare.setOnClickListener(v -> MovieDetailShareHelper.handleShareButtonClick(iconShare, this, currentMovie));
    }


    private void updateFavoriteVisual(ImageView iconFavorite,TextView txtFavorite, boolean isClicked){
        int color = isFavorite ? ContextCompat.getColor(this, R.color.light_green)
                : ContextCompat.getColor(this, R.color.white);
        iconFavorite.setImageTintList(ColorStateList.valueOf(color));

        String txtButton = isFavorite ? getString(R.string.favorited) : getString(R.string.add_favorite);
        txtFavorite.setText(txtButton);

        String notice = isFavorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_from_favorites);

        if (isClicked)
            Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
    }
}