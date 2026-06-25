package com.example.thanhmovie.fragment;

import android.content.Context;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.adapter.SuggestAdapter;
import com.example.thanhmovie.api.RetrofitClient;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.model.MovieResponse;
import com.example.thanhmovie.utils.Constants;
import com.example.thanhmovie.utils.SearchHistoryManager;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private List<Movie> searchResultList;
    private List<Movie> suggestResultList;
    private MovieAdapter searchAdapter;
    private SuggestAdapter suggestAdapter;
    private RecyclerView recyclerView;
    private FlexboxLayout layoutHistory;
    private LinearLayout layoutSuggest;
    private EditText edtSearch;
    private TextView txtNoResult;
    private ImageButton btnClear;
    private TextView txtSeeMore;

    private SearchHistoryManager historyManager;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private String currentQuery;
    private int currentPage;
    private boolean isLoading;
    private static final long SEARCH_DELAY = 500;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_search_result);
        RecyclerView suggestRecyclerView = view.findViewById(R.id.recycler_search_suggest);
        edtSearch = view.findViewById(R.id.edt_search);
        btnClear = view.findViewById(R.id.btn_search_clear);
        layoutSuggest = view.findViewById(R.id.layout_suggest);
        txtSeeMore = view.findViewById(R.id.txt_see_more);
        layoutHistory = view.findViewById(R.id.layout_history);
        txtNoResult = view.findViewById(R.id.txt_search_no_result);

        if (getContext() != null)
            historyManager = new SearchHistoryManager(getContext());

        int spanCount = com.example.thanhmovie.util.GridSpanUtils.calculateSpanCount(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));

        suggestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchResultList = new ArrayList<>();
        searchAdapter = new MovieAdapter(searchResultList);
        recyclerView.setAdapter(searchAdapter);

        suggestResultList = new ArrayList<>();
        suggestAdapter = new SuggestAdapter(suggestResultList);
        suggestRecyclerView.setAdapter(suggestAdapter);

        suggestAdapter.setOnMovieClickListener(movie -> {
            historyManager.addKeyword(movie.getTitle());
            renderHistoryChips();
        });

        renderHistoryChips();
        onEditSearchChanged(edtSearch);
        onEditSearchEnter();
        onRecyclerScrolling();
        onOutsideTouched();

        btnClear.setOnClickListener(v -> edtSearch.setText(""));
    }

    private void renderHistoryChips(){
        layoutHistory.removeAllViews();
        List<String> historyList = historyManager.getHistoryToShow();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String keyword : historyList){
            View chipView = inflater.inflate(R.layout.layout_chip_history, layoutHistory, false);

            TextView txtKeyword = chipView.findViewById(R.id.txt_history_keyword);
            ImageButton btnRemove = chipView.findViewById(R.id.btn_history_remove);

            txtKeyword.setText(keyword);

            chipView.setOnClickListener(v -> {
                edtSearch.setText(keyword);
                performFullSearch();
            });

            btnRemove.setOnClickListener(v ->{
                historyManager.removeKeyword(keyword);
                renderHistoryChips();
            });

            layoutHistory.addView(chipView);
        }
    }

    private void onEditSearchChanged(EditText edtSearch){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();

                searchHandler.removeCallbacks(searchRunnable);

                showSuggestLayout(!currentQuery.isEmpty());
                showHistoryLayout(currentQuery.isEmpty());

                if (!currentQuery.isEmpty()) {
                    searchRunnable = () -> {
                        currentPage = 1;
                        suggestResultList.clear();
                        searchMovies(currentQuery, currentPage, true);
                    };
                    searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void onEditSearchEnter(){
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performFullSearch();
                return true;
            }
            return false;
        });

        txtSeeMore.setOnClickListener(v -> performFullSearch());

        edtSearch.setOnClickListener(v ->{
            showSuggestLayout(!currentQuery.isEmpty());
            showHistoryLayout(currentQuery.isEmpty());
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
                    searchMovies(currentQuery, currentPage,false);
                }
            }
        });
    }

    private void onOutsideTouched(){
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    hideAllOverlays();
                }
                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performFullSearch() {
        if (currentQuery == null || currentQuery.isEmpty()) return;

        historyManager.addKeyword(currentQuery);
        renderHistoryChips();
        showHistoryLayout(false);

        searchHandler.removeCallbacks(searchRunnable);

        currentPage = 1;
        searchResultList.clear();
        searchAdapter.notifyDataSetChanged();
        searchMovies(currentQuery, currentPage, false);

        showSuggestLayout(false);

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
    }

    private void searchMovies(String query, int page, boolean isSuggest){
        isLoading = true;
        RetrofitClient.getInstance().getApiService().searchMovies(Constants.API_KEY, query, Constants.LANGUAGE_VI, page)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        isLoading = false;
                        if (!response.isSuccessful() || response.body() == null) return;

                        if (isSuggest){
                            suggestResultList.clear();
                            suggestResultList.addAll(response.body().getResults());
                            suggestAdapter.notifyDataSetChanged();
                            String text = suggestResultList.isEmpty()  ? getString(R.string.no_result) : getString(R.string.see_more);
                            txtSeeMore.setText(text);
                        }
                        else {
                            int oldSize = searchResultList.size();
                            searchResultList.addAll(response.body().getResults());
                            searchAdapter.notifyItemRangeInserted(oldSize, response.body().getResults().size());
                            showTextNoResult(searchResultList.isEmpty());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuggestLayout(boolean isShow){
        int targetVisibility = isShow ? View.VISIBLE : View.GONE;

        if (btnClear.getVisibility() != targetVisibility) {
            btnClear.setVisibility(targetVisibility);
        }
        if (layoutSuggest.getVisibility() != targetVisibility) {
            layoutSuggest.setVisibility(targetVisibility);
        }
    }

    private void showHistoryLayout(boolean isShow){
        int targetVisibility = isShow ? View.VISIBLE : View.GONE;

        if (layoutHistory.getVisibility() != targetVisibility) {
            layoutHistory.setVisibility(targetVisibility);
        }
    }

    private void showTextNoResult(boolean isShow){
        int targetVisibility = isShow ? View.VISIBLE : View.GONE;

        if (txtNoResult.getVisibility() != targetVisibility) {
            txtNoResult.setVisibility(targetVisibility);
        }
    }

    private void hideAllOverlays() {
        showSuggestLayout(false);
        showHistoryLayout(false);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
    }
}