package com.example.thanhmovie.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class GridSpanUtils {

    private static final int DEFAULT_COLUMN_WIDTH_DP = 140;

    public static int calculateSpanCount(Context context) {
        return calculateSpanCount(context, DEFAULT_COLUMN_WIDTH_DP);
    }

    public static int calculateSpanCount(Context context, int columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (screenWidthDp / columnWidthDp);
        return Math.max(2, spanCount);
    }
}