package com.example.thanhmovie.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.thanhmovie.R;
import com.example.thanhmovie.fragment.FavoriteFragment;
import com.example.thanhmovie.fragment.HomeFragment;
import com.example.thanhmovie.fragment.SearchFragment;
import com.example.thanhmovie.utils.OnScrollDirectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements OnScrollDirectionListener {

    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment favoriteFragment;
    private Fragment oldFragment;
    private Fragment selectedFragment;
    private BottomNavigationView bottomNav;
    private boolean isNavVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        favoriteFragment = new FavoriteFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container, searchFragment).hide(searchFragment)
                .add(R.id.fragment_container, favoriteFragment).hide(favoriteFragment)
                .commit();

        oldFragment = homeFragment;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home)
                selectedFragment = homeFragment;
            else if (id == R.id.nav_search)
                selectedFragment = searchFragment;
            else if (id == R.id.nav_favorite)
                selectedFragment = favoriteFragment;

            if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .hide(oldFragment)
                        .show(selectedFragment)
                        .commit();
                oldFragment = selectedFragment;
            }

            return true;
        });
    }

    @Override
    public void onScrollUp() {
        if (isNavVisible) {
            bottomNav.animate()
                    .translationY(bottomNav.getHeight())
                    .setDuration(200)
                    .start();
            isNavVisible = false;
        }
    }

    @Override
    public void onScrollDown() {
        if (!isNavVisible) {
            bottomNav.animate()
                    .translationY(0)
                    .setDuration(200)
                    .start();
            isNavVisible = true;
        }
    }
}