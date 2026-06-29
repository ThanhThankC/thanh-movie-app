package com.example.thanhmovie.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.adapter.SlideAdapter;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.HomeGenreSectionHelper;
import com.example.thanhmovie.utils.HomeMovieApiHelper;
import com.example.thanhmovie.utils.OnScrollDirectionListener;
import com.example.thanhmovie.utils.SettingPopupHelper;
import com.example.thanhmovie.utils.HomeSliderHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private OnScrollDirectionListener scrollListener;
    private boolean isHeaderVisible = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSlider(view);
        setupGenres(view);
        setupScrollBehavior(view);
        setupSettings(view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollDirectionListener) {
            scrollListener = (OnScrollDirectionListener) context;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeSliderHelper.toggleAutoSlide(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeSliderHelper.toggleAutoSlide(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        HomeSliderHelper.toggleAutoSlide(!hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeSliderHelper.toggleAutoSlide(false);
    }

    private void setupSlider(View view){
        if (getContext() == null) return;

        List<Movie> slideList = new ArrayList<>();
        LinearLayout layoutDots = view.findViewById(R.id.layout_dots);

        SlideAdapter slideAdapter = new SlideAdapter(slideList);
        ViewPager2 viewPagerSlide = view.findViewById(R.id.viewpager_slide);
        viewPagerSlide.setAdapter(slideAdapter);

        HomeMovieApiHelper.loadSlideMovies(
                getContext(),
                slideList,
                viewPagerSlide,
                isAdded(),
                new HomeMovieApiHelper.OnSlideLoadedListener() {
                    @Override
                    public void onLoaded(int count) {
                        HomeSliderHelper.setupDots(getContext(), viewPagerSlide, layoutDots, count);
                        HomeSliderHelper.startAutoSlide(viewPagerSlide, count, isAdded());
                    }
                }
        );
    }

    private void setupGenres(View view){
        if (getContext() == null) return;

        LinearLayout layoutGenresContainer = view.findViewById(R.id.layout_genres_container);

        HomeGenreSectionHelper.renderGenreSections(
                getContext(),
                layoutGenresContainer,
                HomeGenreSectionHelper.buildGenreList(getContext()),
                new HomeGenreSectionHelper.OnMovieLoadRequest() {
                    @Override
                    public void load(int genreId, List<Movie> list, MovieAdapter adapter) {
                        HomeMovieApiHelper.loadMoviesByGenre(getContext(), genreId, list, adapter, isAdded());
                    }
                }
        );
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

    private void setupSettings(View view){
        ImageView btnSettings = view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> SettingPopupHelper.showSettingsPopup(getContext(), v));
    }


}