package com.example.thanhmovie.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.thanhmovie.R;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;

import java.text.MessageFormat;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView imgBackdrop, imgPoster;
    TextView txtTitle, txtRating, txtGenre, txtReleaseDate, txtOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        Movie movie = (Movie) getIntent().getSerializableExtra("movie_object");

        txtTitle = findViewById(R.id.txt_detail_title);
        txtRating = findViewById(R.id.txt_detail_rating);
        txtGenre = findViewById(R.id.txt_detail_genre);
        txtReleaseDate = findViewById(R.id.txt_detail_release);
        txtOverview = findViewById(R.id.txt_detail_overview);
        imgPoster = findViewById(R.id.img_detail_poster);
        imgBackdrop = findViewById(R.id.img_detail_backdrop);

        if (movie == null) return;

        txtTitle.setText(movie.getTitle());
        txtRating.setText(String.format("%.1f/10", movie.getVoteAverage()));
        txtReleaseDate.setText(movie.getReleaseDate());
        txtGenre.setText(Constants.getGenreName(movie.getGenreIds()));
        txtOverview.setText(movie.getOverview());

        Glide.with(this).load(Constants.IMAGE_BASE_URL + movie.getPosterPath())
                .transform(new CenterCrop(), new RoundedCorners(20)).into(imgPoster);
        Glide.with(this).load(Constants.IMAGE_BASE_URL + movie.getBackdropPath()).into(imgBackdrop);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}