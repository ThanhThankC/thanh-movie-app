package com.example.thanhmovie.activity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        db = AppDatabase.getInstance(this);

        currentMovie = (Movie) getIntent().getSerializableExtra("movie_object");
        setupDetailScreen();
        setupDetailFavorite();

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

        if (currentMovie == null) return;

        txtTitle.setText(currentMovie.getTitle());
        txtRating.setText(String.format("%.1f/10", currentMovie.getVoteAverage()));
        txtReleaseDate.setText(currentMovie.getReleaseDate());
        txtGenre.setText(Constants.getGenreName(currentMovie.getGenreIds()));
        txtOverview.setText(currentMovie.getOverview());

        Glide.with(this).load(Constants.IMAGE_BASE_URL + currentMovie.getPosterPath())
                .transform(new CenterCrop(), new RoundedCorners(20)).into(imgPoster);
        Glide.with(this).load(Constants.IMAGE_BASE_URL + currentMovie.getBackdropPath()).into(imgBackdrop);
    }

    private void setupDetailFavorite(){
        ImageView iconFavorite = findViewById(R.id.icon_favorite);
        LinearLayout btnFavorite = findViewById(R.id.btn_favorite);

        checkIfFavorite(iconFavorite);

        btnFavorite.setOnClickListener(v -> toggleFavorite(iconFavorite));
    }

    private void checkIfFavorite(ImageView iconFavorite){
        executor.execute(() -> {
            FavoriteMovie existing = db.favoriteDao().getFavoriteMovie(currentMovie.getId());
            isFavorite = existing != null;

            runOnUiThread(() -> updateFavoriteIcon(iconFavorite, false));
        });
    }

    private void toggleFavorite(ImageView iconFavorite){
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
            runOnUiThread(() -> updateFavoriteIcon(iconFavorite, true));
        });
    }

    private void updateFavoriteIcon(ImageView iconFavorite, boolean isClicked){
        int color = isFavorite ? ContextCompat.getColor(this, R.color.light_green)
                : ContextCompat.getColor(this, R.color.white);
        iconFavorite.setImageTintList(ColorStateList.valueOf(color));

        String notice = isFavorite ? getString(R.string.add_favorite)
                : getString(R.string.remove_favorite);

        if (isClicked)
            Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
    }
}