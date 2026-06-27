package com.example.thanhmovie.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.thanhmovie.database.AppDatabase;
import com.example.thanhmovie.database.FavoriteMovie;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Movie currentMovie;
    private boolean isFavorite;
    private static final long SHARE_CLICKED_DELAY = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        db = AppDatabase.getInstance(this);

        currentMovie = (Movie) getIntent().getSerializableExtra("movie_object");
        setupDetailScreen();
        setupFavoriteButton();
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
        txtGenre.setText(Constants.getGenreName(currentMovie.getGenreIds()));
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

        checkIfFavorite(iconFavorite, txtFavorite);

        btnFavorite.setOnClickListener(v -> toggleFavorite(iconFavorite, txtFavorite));
    }

    private void setupShareButton(){
        LinearLayout btnShare = findViewById(R.id.btn_share);
        ImageView iconShare = findViewById(R.id.icon_share);

        btnShare.setOnClickListener(v -> {
            iconShare.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(this, R.color.light_green)));

            shareMovie();

            new Handler().postDelayed(() -> {
                iconShare.setImageTintList(ColorStateList
                        .valueOf(ContextCompat.getColor(this, R.color.white)));
            }, SHARE_CLICKED_DELAY);
        });
    }

    private void toggleFavorite(ImageView iconFavorite, TextView txtFavorite){
        executor.execute(() -> {
            if (isFavorite){
                FavoriteMovie existing = db.favoriteDao().getFavoriteMovie(currentMovie.getId());
                db.favoriteDao().delete(existing);
                isFavorite = false;
            }
            else {
                if (currentMovie == null) return;
                FavoriteMovie newFavorite = new FavoriteMovie(
                        currentMovie.getId(),
                        currentMovie.getTitle(),
                        currentMovie.getPosterPath(),
                        currentMovie.getBackdropPath(),
                        currentMovie.getOverview(),
                        currentMovie.getVoteAverage(),
                        currentMovie.getReleaseDate(),
                        currentMovie.getPopularity(),
                        System.currentTimeMillis()
                );
                db.favoriteDao().insert(newFavorite);
                isFavorite = true;
            }
            runOnUiThread(() -> updateFavoriteVisual(iconFavorite, txtFavorite, true));
        });
    }

    private void checkIfFavorite(ImageView iconFavorite, TextView txtFavorite){
        executor.execute(() -> {
            FavoriteMovie existing = db.favoriteDao().getFavoriteMovie(currentMovie.getId());
            isFavorite = existing != null;

            runOnUiThread(() -> updateFavoriteVisual(iconFavorite, txtFavorite, false));
        });
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

    private void shareMovie(){
        String shareText = getString(R.string.share_movie_text,
                currentMovie.getTitle(),
                currentMovie.getVoteAverage(),
                currentMovie.getOverview()
        );
        String chooserTitle = getString(R.string.share_movie_title);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plant");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, chooserTitle));
    }
}