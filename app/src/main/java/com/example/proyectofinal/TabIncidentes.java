package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.proyectofinal.Adaptadores.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabIncidentes extends AppCompatActivity {

    ViewPager2 viewPager2;
    TabLayout tabLayout;

    FragmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_incidentes);

        viewPager2 = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);

        adapter = new FragmentAdapter(this);
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}