package com.example.thanhmovie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.thanhmovie.R;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
    private final List<Movie> slideList;
    private static final int VIRTUAL_COUNT = Integer.MAX_VALUE;

    public SlideAdapter(List<Movie> slideList){ this.slideList = slideList; }

    public static class SlideViewHolder extends RecyclerView.ViewHolder{
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
        }
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slide_movie, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        int realPosition = position % slideList.size();
        Movie movie = slideList.get(realPosition);

        holder.txtTitle.setText(movie.getTitle());
        holder.txtRating.setText(String.format("%.1f/10", movie.getVoteAverage()));
        holder.txtGenre.setText(getGenreName(movie.getGenreIds()));
        holder.txtYear.setText(movie.getReleaseDate().substring(0,4));

        Glide.with(holder.itemView.getContext())
                .load(Constants.IMAGE_BASE_URL + movie.getBackdropPath())
                .into(holder.imgBackdrop);

        Glide.with(holder.itemView.getContext())
                .load(Constants.IMAGE_BASE_URL + movie.getPosterPath())
                .transform(new CenterCrop(), new RoundedCorners(20))
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() { return slideList.isEmpty() ? 0 : VIRTUAL_COUNT; }

    private String getGenreName(List<Integer> genreIds){
        if (genreIds == null || genreIds.isEmpty()) return "";
        int id = genreIds.get(0);

        if (id == 28) return "Hành động";
        if (id == 35) return "Hài";
        if (id == 10749) return "Tình cảm";
        if (id == 27) return "Kinh dị";
        if (id == 18) return "Tâm lý";
        return "Khác";
    }
}
