package com.example.thanhmovie.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
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
import com.example.thanhmovie.utils.OnScrollDirectionListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPagerSlide;
    private LinearLayout layoutDots;
    private LinearLayout layoutGenresContainer;
    private final List<GenreModel> genreSections = new ArrayList<>();
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private OnScrollDirectionListener scrollListener;
    private boolean isHeaderVisible = true;
    private static final long SLIDE_DELAY = 3000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPagerSlide = view.findViewById(R.id.viewpager_slide);
        layoutDots = view.findViewById(R.id.layout_dots);
        layoutGenresContainer = view.findViewById(R.id.layout_genres_container);

        List<Movie> slideList = new ArrayList<>();
        SlideAdapter slideAdapter = new SlideAdapter(slideList);
        viewPagerSlide.setAdapter(slideAdapter);

        loadSlideMovies(slideList, slideAdapter);

        setupGenreSections();
        renderGenreSections();
        setupScrollBehavior(view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollDirectionListener) {
            scrollListener = (OnScrollDirectionListener) context;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        toggleAutoSlide(!hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    private void setupGenreSections() {
        genreSections.add(new GenreModel(28, getString(R.string.genre_action)));
        genreSections.add(new GenreModel(10749, getString(R.string.genre_romance)));
        genreSections.add(new GenreModel(35, getString(R.string.genre_comedy)));
        genreSections.add(new GenreModel(878, getString(R.string.genre_fiction)));
    }

    private void setupDots(int count){
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
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
                }
            }
        }
        );
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

            List<Movie> genreMovieList = new ArrayList<>();
            MovieAdapter movieAdapter = new MovieAdapter(genreMovieList);
            recyclerGenre.setAdapter(movieAdapter);

            loadMoviesByGenre(genre.getGenreId(), genreMovieList, movieAdapter);

            layoutGenresContainer.addView(view);
        }
    }

    private void setupScrollBehavior(View view){
        NestedScrollView scrollHome = view.findViewById(R.id.scroll_home);
        View headerView = view.findViewById(R.id.layout_header);

        scrollHome.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY && scrollY > 50 && isHeaderVisible) {
                int headerHeight = headerView.getHeight() + 80;
                headerView.animate().translationY(-headerHeight).setDuration(200).start();
                isHeaderVisible = false;
                if (scrollListener != null) scrollListener.onScrollUp();
            } else if (scrollY < oldScrollY && !isHeaderVisible) {
                headerView.animate().translationY(0).setDuration(200).start();
                isHeaderVisible = true;
                if (scrollListener != null) scrollListener.onScrollDown();
            }
        });
    }

    private void loadSlideMovies(List<Movie> targetList, SlideAdapter adapter){
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(Constants.API_KEY, Constants.LANGUAGE_VI, 1)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (!isAdded() || getContext() == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            List<Movie> allMovies = response.body().getResults();

                            targetList.clear();
                            for (int i = 0; i < 7 && i < allMovies.size(); i++){
                                targetList.add(allMovies.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            setupDots(targetList.size());
                            startAutoSlide(targetList.size());

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
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMoviesByGenre(int genreId, List<Movie> targetList, MovieAdapter adapter){
        RetrofitClient.getInstance().getApiService()
                .getMoviesByGenre(Constants.API_KEY, Constants.LANGUAGE_VI, genreId, 1)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (!isAdded() || getContext() == null) return;

                        if (response.isSuccessful() && response.body() != null){
                            targetList.addAll(response.body().getResults());
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            if (!isAdded() || getContext() == null) return;
                            Log.e("API_ERROR", "Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startAutoSlide(int count){
        if (count <= 1) return;

        sliderRunnable = () -> {
            if (!isAdded() || viewPagerSlide == null) return;
            viewPagerSlide.setCurrentItem(viewPagerSlide.getCurrentItem() + 1, true);
            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
        };
        sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
    }

    private void toggleAutoSlide(boolean shouldRun) {
        if (sliderRunnable == null) return;
        sliderHandler.removeCallbacks(sliderRunnable);
        if (shouldRun) {
            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
        }
    }
}