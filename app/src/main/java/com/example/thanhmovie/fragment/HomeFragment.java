package com.example.thanhmovie.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.adapter.SlideAdapter;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.GenreModel;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
//    private MovieAdapter movieAdapter;

    private ViewPager2 viewPagerSlide;
    private LinearLayout layoutDots;
    private LinearLayout layoutGenresContainer;
    private final List<GenreModel> genreSections = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragement_home, container, false);

        viewPagerSlide = view.findViewById(R.id.viewpager_slide);
        layoutDots = view.findViewById(R.id.layout_dots);
        layoutGenresContainer = view.findViewById(R.id.layout_genres_container);

        List<Movie> slideList = new ArrayList<>();
        SlideAdapter slideAdapter = new SlideAdapter(slideList);
        viewPagerSlide.setAdapter(slideAdapter);

        loadMovies(slideList, slideAdapter);

        setupGenreList();
        renderGenreSections();
//        View view = inflater.inflate(R.layout.fragment_home1, container, false);
//
//        RecyclerView recyclerView;
//        recyclerView = view.findViewById(R.id.recycler_movies);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//
//        movieAdapter = new MovieAdapter(movieList);
//        recyclerView.setAdapter(movieAdapter);
//
//
        return view;
    }

    private void loadMovies(List<Movie> targetList, SlideAdapter adapter){
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(Constants.API_KEY, Constants.LANGUAGE_VI, 1)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (!isAdded() || getContext() == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            List<Movie> allMovies = response.body().getResults();

                            targetList.clear();
                            for (int i = 0; i < 7 && i < allMovies.size(); i++){
                                targetList.add(allMovies.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            SetupDots(targetList.size());

                            if (!targetList.isEmpty()) {
                                int startPosition = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % targetList.size();
                                viewPagerSlide.setCurrentItem(startPosition, false);
                            }
                        }
                        else {
                            if (!isAdded() || getContext() == null) return;
                            Log.e("API_ERROR", "Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SetupDots(int count){
        layoutDots.removeAllViews();
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i ++){
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10,0,10,0);
            dots[i].setLayoutParams(params);

            layoutDots.addView(dots[i]);
        }

        if (count > 0) dots[0].setImageResource(R.drawable.dot_active);

        viewPagerSlide.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int realPosition = position % count;
                for (int i = 0; i < count; i ++){
                    dots[i].setImageResource(i == realPosition ? R.drawable.dot_active : R.drawable.dot_inactive);
                }
            }
        }
        );
    }

    private void setupGenreList() {
        genreSections.add(new GenreModel(28, getString(R.string.genre_action)));
        genreSections.add(new GenreModel(10749, getString(R.string.genre_romance)));
        genreSections.add(new GenreModel(35, getString(R.string.genre_comedy)));
        genreSections.add(new GenreModel(878, getString(R.string.genre_fiction)));
    }

    private void renderGenreSections(){
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (GenreModel genre : genreSections) {
            View view = inflater.inflate(R.layout.item_genre_section, layoutGenresContainer, false);

            TextView txtTitle = view.findViewById(R.id.txt_genre_title);
            RecyclerView recyclerGenre = view.findViewById(R.id.recycler_gener);

            txtTitle.setText(genre.getGenreName());

            recyclerGenre.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
            );

            List<Movie> genreMoiveList = new ArrayList<>();
            MovieAdapter movieAdapter = new MovieAdapter(genreMoiveList);
            recyclerGenre.setAdapter(movieAdapter);

            loadMoivesByGenre(genre.getGenreId(), genreMoiveList, movieAdapter);

            layoutGenresContainer.addView(view);
        }
    }

    private void loadMoivesByGenre(int genreId, List<Movie> targeList, MovieAdapter adapter){
        RetrofitClient.getInstance().getApiService()
                .getMoviesByGenre(Constants.API_KEY, Constants.LANGUAGE_VI, genreId, 1)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (!isAdded() || getContext() == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            targeList.addAll(response.body().getResults());
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            if (!isAdded() || getContext() == null) return;
                            Log.e("API_ERROR", "Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}