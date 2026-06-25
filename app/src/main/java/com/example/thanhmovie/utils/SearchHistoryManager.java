package com.example.thanhmovie.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {
    private static final String PREF_NAME = "search_history_pref";
    private static final String KEY_HISTORY = "history_list";
    private static final int MAX_SAVE = 15;
    private static final int MAX_SHOW = 10;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SearchHistoryManager(Context context){
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private List<String> getAllHistory(){
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public List<String> getHistoryToShow(){
        List<String> all = getAllHistory();
        int size = Math.min(all.size(), MAX_SHOW);
        return  new ArrayList<>(all.subList(0, size));
    }

    public void addKeyword(String keyword){
        if(keyword == null || keyword.trim().isEmpty()) return;
        keyword = keyword.trim();

        List<String> all = getAllHistory();

        all.remove(keyword);
        all.add(0, keyword);

        while (all.size() > MAX_SAVE){
            all.remove(all.size() - 1);
        }

        saveHistory(all);
    }

    public void removeKeyword(String keyword){
        List<String> all = getAllHistory();
        all.remove(keyword);
        saveHistory(all);
    }

    private void saveHistory(List<String> list){
        String json = gson.toJson(list);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }
}
