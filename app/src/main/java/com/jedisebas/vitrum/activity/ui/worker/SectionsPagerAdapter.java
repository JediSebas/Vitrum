package com.jedisebas.vitrum.activity.ui.worker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jedisebas.vitrum.activity.ui.main.AllSuggestionFragment;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull final FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(final int position) {
        if (position == 0) {
            return new UsersAccountFragment();
        }
        return new AllSuggestionFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}