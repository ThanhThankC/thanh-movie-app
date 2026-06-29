package com.example.thanhmovie.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

import androidx.viewpager2.widget.ViewPager2;

import com.example.thanhmovie.R;

public class HomeSliderHelper {

    private static final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private static Runnable sliderRunnable;
    private static final long SLIDE_DELAY = 3000;

    public static void setupDots(Context context, ViewPager2 viewPagerSlide, LinearLayout layoutDots, int count){
        layoutDots.removeAllViews();
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i ++){
            dots[i] = new ImageView(context);
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
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
                }
            }
        }
        );
    }

    public static void startAutoSlide(ViewPager2 viewPagerSlide, int count, boolean isAdded){
        if (count <= 1) return;

        sliderRunnable = () -> {
            if (!isAdded || viewPagerSlide == null) return;
            viewPagerSlide.setCurrentItem(viewPagerSlide.getCurrentItem() + 1, true);
            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
        };
        sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
    }

    public static void toggleAutoSlide(boolean shouldRun) {
        if (sliderRunnable == null) return;
        sliderHandler.removeCallbacks(sliderRunnable);
        if (shouldRun) {
            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
        }
    }
}
