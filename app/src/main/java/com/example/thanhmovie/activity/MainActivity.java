package com.example.thanhmovie.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.thanhmovie.R;
import com.example.thanhmovie.fragment.HomeFragment;
import com.example.thanhmovie.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new HomeFragment())
                .commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment seleted = null;
            int id = item.getItemId();

            if (id == R.id.nav_home){
                seleted = new HomeFragment();
            }
            else if (id == R.id.nav_search){
                seleted = new SearchFragment();
            }
            else if (id == R.id.nav_favorite){
                seleted = new SearchFragment();
            }

            if (seleted != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, seleted)
                        .commit();
            }

            return true;
        });
    }
}