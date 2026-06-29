package com.example.thanhmovie.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.R;
import com.example.thanhmovie.adapter.MovieAdapter;
import com.example.thanhmovie.adapter.SuggestAdapter;
import com.example.thanhmovie.model.Movie;
import com.example.thanhmovie.utils.OnScrollDirectionListener;
import com.example.thanhmovie.utils.SearchApiHelper;
import com.example.thanhmovie.utils.SearchHistoryHelper;
import com.example.thanhmovie.utils.SearchHistoryManager;
import com.example.thanhmovie.utils.SearchInputHelper;
import com.example.thanhmovie.utils.SearchState;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchState state;
    private RecyclerView suggestRecyclerView;
    private RecyclerView recyclerView;
    private FlexboxLayout layoutHistory;
    private LinearLayout layoutSuggest;
    private EditText edtSearch;
    private TextView txtNoResult;
    private ImageButton btnClear;
    private TextView txtSeeMore;
    private OnScrollDirectionListener scrollListener;

    private SearchHistoryManager historyManager;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    private boolean isHeaderVisible = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_search_result);
        suggestRecyclerView = view.findViewById(R.id.recycler_search_suggest);
        edtSearch = view.findViewById(R.id.edt_search);
        btnClear = view.findViewById(R.id.btn_search_clear);
        layoutSuggest = view.findViewById(R.id.layout_suggest);
        txtSeeMore = view.findViewById(R.id.txt_see_more);
        layoutHistory = view.findViewById(R.id.layout_history);
        txtNoResult = view.findViewById(R.id.txt_search_no_result);

        if (getContext() != null)
            historyManager = new SearchHistoryManager(getContext());

        int spanCount = com.example.thanhmovie.utils.GridSpanUtils.calculateSpanCount(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));

        suggestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initState();
        setupInput();
        setupHistoryChips();
        setupScrollBehavior();
        setupKeyboardListener(view);

        btnClear.setOnClickListener(v -> edtSearch.setText(""));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollDirectionListener) {
            scrollListener = (OnScrollDirectionListener) context;
        }
    }

    private void initState(){
        List<Movie> searchResultList = new ArrayList<>();
        MovieAdapter searchAdapter = new MovieAdapter(searchResultList);
        recyclerView.setAdapter(searchAdapter);

        List<Movie> suggestResultList = new ArrayList<>();
        SuggestAdapter suggestAdapter = new SuggestAdapter(suggestResultList);
        suggestRecyclerView.setAdapter(suggestAdapter);

        state = new SearchState(searchResultList, searchAdapter,
                suggestResultList, suggestAdapter);

        suggestAdapter.setOnMovieClickListener(movie -> {
            historyManager.addKeyword(movie.getTitle());
            setupHistoryChips();
        });

        SearchApiHelper.onRecyclerScrolling(recyclerView, () -> {
            state.nextPage();
            searchMovie(false);
        });
    }

    private void setupInput(){
        SearchInputHelper.setup(state, recyclerView, edtSearch, txtSeeMore,
                new SearchInputHelper.Listener() {
                    @Override
                    public void onQueryChanged(String query, int currentPage) { searchMovie(true); }
                    @Override
                    public void onSearch(String query) { performFullSearch(query);}
                    @Override
                    public void onShowSuggest(boolean show) { showSuggestLayout(show); }
                    @Override
                    public void onShowHistory(boolean show) { showHistoryLayout(show); }
                    @Override
                    public void onHideOverlay() {hideAllOverlays();}
                }
        );
    }

    private void setupHistoryChips(){
        SearchHistoryHelper.renderHistoryChips(getContext(), state, layoutHistory, edtSearch, historyManager,
                new SearchHistoryHelper.Listener() {
                    @Override
                    public void onChipClicked(String keyword) {
                        performFullSearch(keyword);
                    }
                });
    }

    private void setupScrollBehavior(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && isHeaderVisible) {
                    isHeaderVisible = false;
                    if (scrollListener != null) scrollListener.onScrollUp();
                } else if (dy < 0 && !isHeaderVisible) {
                    isHeaderVisible = true;
                    if (scrollListener != null) scrollListener.onScrollDown();
                }
            }
        });
    }

    private void setupKeyboardListener(View rootView) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            boolean isKeyboardOpen = keypadHeight > screenHeight * 0.15;

            if (isKeyboardOpen) {
                if (scrollListener != null) scrollListener.onScrollUp();
            } else {
                if (scrollListener != null) scrollListener.onScrollDown();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performFullSearch(String currentQuery) {
        if (currentQuery == null || currentQuery.isEmpty()) return;

        historyManager.addKeyword(currentQuery);
        setupHistoryChips();
        showHistoryLayout(false);

        searchHandler.removeCallbacks(SearchInputHelper.searchRunnable);

        state.resetPage();
        state.searchResultList.clear();
        state.searchAdapter.notifyDataSetChanged();
        searchMovie(false);

        showSuggestLayout(false);

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
    }

    private void searchMovie(boolean isSuggest){
        SearchApiHelper.searchMovies(getContext(), state, txtSeeMore, isSuggest, new SearchApiHelper.OnSearchedListener() {
            @Override
            public void onResultSearched(boolean isNoResult) {
                showTextNoResult(isNoResult);
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