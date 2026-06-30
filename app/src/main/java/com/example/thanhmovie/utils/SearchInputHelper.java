package com.example.thanhmovie.utils;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thanhmovie.model.Movie;

import java.util.List;

public class SearchInputHelper {
    private static final Handler searchHandler = new Handler(Looper.getMainLooper());
    public static Runnable searchRunnable;
    private static final long SEARCH_DELAY = 400;

    public interface Listener {
        /** trigger suggest search */
        void onQueryChanged(String query, int currentPage);
        /** trigger full search */
        void onSearch(String query);
        /** show/hide suggest */
        void onShowSuggest(boolean show);
        /** show/hide history layout */
        void onShowHistory(boolean show);
        void onHideOverlay();
    }

    public static void setup(SearchState state, RecyclerView recyclerView,EditText edtSearch, TextView txtSeeMore, Listener listener){
        if (!state.currentQuery.isEmpty()) {
            edtSearch.setText(state.currentQuery);
            edtSearch.setSelection(state.currentQuery.length());
        }
        else
            onEditSearchChanged(state, edtSearch, listener);
        onEditSearchEnter(state,edtSearch, txtSeeMore, listener);
        onOutsideTouched(recyclerView, listener);
    }

    private static void onEditSearchChanged(SearchState state, EditText edtSearch, Listener listener){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                state.currentQuery = s.toString().trim();

                searchHandler.removeCallbacks(searchRunnable);

                listener.onShowSuggest(!state.currentQuery.isEmpty());
                listener.onShowHistory(state.currentQuery.isEmpty());

                if (!state.currentQuery.isEmpty()) {
                    searchRunnable = () -> {
                        state.currentPage = 1;
                        state.suggestResultList.clear();
                        listener.onQueryChanged(state.currentQuery, state.currentPage);
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

    private static void onEditSearchEnter(SearchState state,EditText edtSearch, TextView txtSeeMore, Listener listener){
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                listener.onSearch(state.currentQuery);
                return true;
            }
            return false;
        });

        txtSeeMore.setOnClickListener(v -> listener.onSearch(state.currentQuery));

        edtSearch.setOnClickListener(v ->{
            listener.onShowSuggest(!state.currentQuery.isEmpty());
            listener.onShowHistory(state.currentQuery.isEmpty());
            edtSearch.setSelection(state.currentQuery.length());
        });
    }

    private static void onOutsideTouched(RecyclerView recyclerView, Listener listener){
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    listener.onHideOverlay();
                }
                return false;
            }
        });
    }
}
