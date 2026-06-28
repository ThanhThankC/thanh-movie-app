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
import com.example.thanhmovie.utils.GenreHelper;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.MessageFormat;
import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
    private final List<Movie> slideList;
    private static final int VIRTUAL_COUNT = Integer.MAX_VALUE;

    public SlideAdapter(List<Movie> slideList){ this.slideList = slideList; }

    public static class SlideViewHolder extends RecyclerView.ViewHolder{
        ShimmerFrameLayout shimmerBackdrop;
        ShimmerFrameLayout shimmerPoster;
        ImageView imgBackdrop, imgPoster;
        TextView txtTitle, txtRating, txtGenre, txtYear;

        public SlideViewHolder(View itemView){
            super((itemView));
            imgBackdrop = itemView.findViewById(R.id.img_backdrop);
            imgPoster = itemView.findViewById(R.id.img_slide_poster);
            txtTitle = itemView.findViewById(R.id.txt_slide_title);
            txtRating = itemView.findViewById(R.id.txt_slide_rating);
            txtGenre = itemView.findViewById(R.id.txt_slide_genre);
            txtYear = itemView.findViewById(R.id.txt_slide_year);
            shimmerBackdrop = itemView.findViewById(R.id.shimmer_slide_backdrop);
            shimmerPoster = itemView.findViewById(R.id.shimmer_slide_poster);
        }
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slide_movie, parent, false);
        SlideAdapter.SlideViewHolder holder = new SlideViewHolder(view);
        holder.shimmerBackdrop.startShimmer();
        holder.shimmerPoster.startShimmer();
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        int realPosition = position % slideList.size();
        Movie movie = slideList.get(realPosition);

        holder.txtTitle.setText(movie.getTitle());
        holder.txtRating.setText(String.format("%.1f/10", movie.getVoteAverage()));
        holder.txtGenre.setText(GenreHelper.getGenreName(holder.itemView.getContext(),movie.getGenreIds()));
        holder.txtYear.setText(movie.getReleaseDate().substring(0,4));

        Glide.with(holder.itemView.getContext())
                .load(Constants.IMAGE_BASE_URL + movie.getBackdropPath())
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        holder.shimmerBackdrop.stopShimmer();
                        holder.shimmerBackdrop.setShimmer(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        holder.shimmerBackdrop.stopShimmer();
                        holder.shimmerBackdrop.setShimmer(null);
                        return false;
                    }
                })
                .into(holder.imgBackdrop);

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
    public int getItemCount() { return slideList.isEmpty() ? 0 : VIRTUAL_COUNT; }
}
