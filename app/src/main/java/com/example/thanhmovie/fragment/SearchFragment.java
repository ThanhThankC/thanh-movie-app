package com.example.thanhmovie.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText edtSearch;
    private List<Movie> searchResultList;
    private MovieAdapter searchAdapter;

    private String currentQuery;
    private int currentPage;
    private boolean isLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_search_result);
        edtSearch = view.findViewById(R.id.edt_search);

        int spanCount = com.example.thanhmovie.util.GridSpanUtils.calculateSpanCount(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));

        searchResultList = new ArrayList<>();
        searchAdapter = new MovieAdapter(searchResultList);
        recyclerView.setAdapter(searchAdapter);

        onEditSearchChanged();
        onRecyclerScrolling();

        return view;
    }

    private void onEditSearchChanged(){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();
                currentPage = 1;
                searchResultList.clear();
                searchAdapter.notifyDataSetChanged();

                if (!currentQuery.isEmpty()){
                    searchMovies(currentQuery, currentPage);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void onRecyclerScrolling(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && lastVisibleItem >= totalItemCount - 4){
                    currentPage++;
                    searchMovies(currentQuery, currentPage);
                }
            }
        });
    }

    private void searchMovies(String query, int page){
        isLoading = true;
        RetrofitClient.getInstance().getApiService().searchMovies(Constants.API_KEY, query, Constants.LANGUAGE_VI, page)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null){
                            int oldSize = searchResultList.size();
                            searchResultList.addAll(response.body().getResults());
                            searchAdapter.notifyItemRangeInserted(oldSize, response.body().getResults().size());
                        }
                        else {
                            Log.e("API_ERROR", "Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}