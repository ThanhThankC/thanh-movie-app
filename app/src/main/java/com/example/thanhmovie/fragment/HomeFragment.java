package com.example.thanhmovie.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.SlideAdapter;
import com.example.thanhmovie.api.RetrofitClient;
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
    private SlideAdapter slideAdapter;
//    private List<Movie> slideList = new ArrayList<>();
    private final List<Movie> slideList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragement_home, container, false);

        viewPagerSlide = view.findViewById(R.id.viewpager_slide);
        layoutDots = view.findViewById(R.id.layout_dots);

        slideAdapter = new SlideAdapter(slideList);
        viewPagerSlide.setAdapter(slideAdapter);

//        View view = inflater.inflate(R.layout.fragment_home1, container, false);
//
//        RecyclerView recyclerView;
//        recyclerView = view.findViewById(R.id.recycler_movies);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//
//        movieAdapter = new MovieAdapter(movieList);
//        recyclerView.setAdapter(movieAdapter);
//
        loadMovies();
//
        return view;
    }

    private void loadMovies(){
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(Constants.API_KEY, Constants.LANGUAGE_VI, 1)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null){
                            List<Movie> allMovies = response.body().getResults();

                            slideList.clear();
                            for (int i = 0; i < 7 && i < allMovies.size(); i++){
                                slideList.add(allMovies.get(i));
                            }
                            slideAdapter.notifyDataSetChanged();
                            SetupDots(slideList.size());

                            if (!slideList.isEmpty()) {
                                int startPosition = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % slideList.size();
                                viewPagerSlide.setCurrentItem(startPosition, false);
                            }
                        }
                        else {
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
}