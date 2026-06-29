package com.example.thanhmovie.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.MovieResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchApiHelper {

    private static boolean isLoading;

    public interface OnSearchedListener {
        void onResultSearched(boolean isNoResult);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static void searchMovies(Context context, SearchState state, TextView txtSeeMore, boolean isSuggest, OnSearchedListener listener){
        isLoading = true;
        RetrofitClient.getInstance().getApiService().searchMovies(Constants.API_KEY, state.currentQuery, Constants.getApiLanguage(context), state.currentPage)
            .enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    isLoading = false;
                    if (!response.isSuccessful() || response.body() == null) return;

                    if (isSuggest){
                        state.suggestResultList.clear();
                        state.suggestResultList.addAll(response.body().getResults());
                        state.suggestAdapter.notifyDataSetChanged();
                        String text = state.suggestResultList.isEmpty()  ? context.getString(R.string.no_result) : context.getString(R.string.see_more);
                        txtSeeMore.setText(text);
                    }
                    else {
                        int oldSize = state.searchResultList.size();
                        state.searchResultList.addAll(response.body().getResults());
                        state.searchAdapter.notifyItemRangeInserted(oldSize, response.body().getResults().size());
                        listener.onResultSearched(state.searchResultList.isEmpty());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                    isLoading = false;
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            });
    }

    public static void onRecyclerScrolling(RecyclerView recyclerView, OnLoadMoreListener listener){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && lastVisibleItem >= totalItemCount - 4){
                    listener.onLoadMore();
                }
            }
        });
    }
}
