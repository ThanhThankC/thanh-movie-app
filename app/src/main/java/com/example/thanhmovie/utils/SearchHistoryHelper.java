package com.example.thanhmovie.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thanhmovie.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class SearchHistoryHelper {

    public interface Listener{
        void onChipClicked(String keyword);
    }

    public static void renderHistoryChips(Context context, SearchState state ,FlexboxLayout layoutHistory, EditText edtSearch,
                                          SearchHistoryManager historyManager, Listener listener){
        layoutHistory.removeAllViews();
        List<String> historyList = historyManager.getHistoryToShow();

        LayoutInflater inflater = LayoutInflater.from(context);

        for (String keyword : historyList){
            View chipView = inflater.inflate(R.layout.layout_chip_history, layoutHistory, false);

            TextView txtKeyword = chipView.findViewById(R.id.txt_history_keyword);
            ImageButton btnRemove = chipView.findViewById(R.id.btn_history_remove);

            txtKeyword.setText(keyword);

            chipView.setOnClickListener(v -> {
                edtSearch.setText(keyword);
                listener.onChipClicked(state.currentQuery);
            });

            btnRemove.setOnClickListener(v ->{
                historyManager.removeKeyword(keyword);
                renderHistoryChips(context, state, layoutHistory, edtSearch, historyManager, listener);
            });

            layoutHistory.addView(chipView);
        }
    }
}
