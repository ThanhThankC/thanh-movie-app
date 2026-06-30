package com.example.thanhmovie.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.thanhmovie.R;
import com.example.thanhmovie.activity.MovieDetailActivity;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<Movie> movieList;
    public MovieAdapter(List<Movie> movieList){
        this.movieList = movieList;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerPoster;

        ImageView imgPoster;
        TextView txtTitle;

        public  MovieViewHolder(View itemView){
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_poster);
            txtTitle = itemView.findViewById(R.id.txt_title);
            shimmerPoster = itemView.findViewById(R.id.shimmer_poster);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        MovieViewHolder holder = new MovieViewHolder(view);
        holder.shimmerPoster.startShimmer();
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.txtTitle.setText(movie.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(Constants.IMAGE_BASE_URL + movie.getPosterPath())
                .transform(new CenterCrop(), new RoundedCorners(20))
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        holder.shimmerPoster.stopShimmer();
                        holder.shimmerPoster.setShimmer(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        holder.shimmerPoster.stopShimmer();
                        holder.shimmerPoster.setShimmer(null);
                        return false;
                    }
                })
                .into(holder.imgPoster);

        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_object", movie);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}