package com.example.thanhmovie.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.activity.MovieDetailActivity;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.Constants;

import java.util.List;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.SuggestViewHolder> {

    private final List<Movie> suggestList;

    public SuggestAdapter(List<Movie> movieList){
        this.suggestList = movieList;
    }

    public static class SuggestViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle, txtGenre, txtYear;

        public SuggestViewHolder(View itemView){
            super((itemView));
            txtTitle = itemView.findViewById(R.id.txt_suggest_title);
            txtGenre = itemView.findViewById(R.id.txt_suggest_genre);
            txtYear = itemView.findViewById(R.id.txt_suggest_year);
        }
    }

    @NonNull
    @Override
    public SuggestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggest_movie, parent, false);
        return new SuggestAdapter.SuggestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestViewHolder holder, int position) {
        Movie movie = suggestList.get(position);
        holder.txtTitle.setText(movie.getTitle());
        String releaseDate = movie.getReleaseDate();
        String year = (releaseDate != null && releaseDate.length() >= 4)
                ? releaseDate.substring(0, 4) : "N/A";
        holder.txtYear.setText(year);
        holder.txtGenre.setText(Constants.getGenreName(movie.getGenreIds()));

        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_object", movie);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return Math.min(suggestList.size(),7); }
}
