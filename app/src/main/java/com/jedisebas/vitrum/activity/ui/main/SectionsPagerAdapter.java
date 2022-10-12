package com.jedisebas.vitrum.activity.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull final FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(final int position) {
        if (position == 1) {
            return new MySuggestionFragment();
        }
        return new AllSuggestionFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}