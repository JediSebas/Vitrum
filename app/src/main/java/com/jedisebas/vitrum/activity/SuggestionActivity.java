package com.jedisebas.vitrum.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.activity.ui.main.SectionsPagerAdapter;
import com.jedisebas.vitrum.databinding.ActivitySuggestionBinding;

public class SuggestionActivity extends AppCompatActivity {

    private boolean visible = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivitySuggestionBinding binding = ActivitySuggestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        final ViewPager2 viewPager2 = binding.viewPager;
        viewPager2.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = binding.tabs;

        new TabLayoutMediator(tabs, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.my_suggestions));
            } else {
                tab.setText(getString(R.string.all_suggestions));
            }
        }).attach();

        final FloatingActionButton fab = binding.fab;
        final FloatingActionButton saveFab = binding.saveFab;
        final FloatingActionButton sendFab = binding.sendFab;
        final FloatingActionButton cameraFab = binding.cameraFab;
        final FloatingActionButton deleteFab = binding.deleteFab;

        fab.setOnClickListener(view -> {
            if (visible) {
                saveFab.setVisibility(View.GONE);
                sendFab.setVisibility(View.GONE);
                cameraFab.setVisibility(View.GONE);
                deleteFab.setVisibility(View.GONE);
            } else {
                saveFab.setVisibility(View.VISIBLE);
                sendFab.setVisibility(View.VISIBLE);
                cameraFab.setVisibility(View.VISIBLE);
                deleteFab.setVisibility(View.VISIBLE);
            }
            visible = !visible;
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.title_activity_suggestion));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}