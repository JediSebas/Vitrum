package com.jedisebas.vitrum.activity.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jedisebas.vitrum.R;

import java.util.ArrayList;
import java.util.List;

public class AllSuggestionFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_all_suggestion, container, false);

        final ListView listView = root.findViewById(R.id.allListView);
        final List<SuggestionItem> itemList = new ArrayList<>();

        itemList.add(new SuggestionItem(1, "tytul 1", "10", "20", "30"));
        itemList.add(new SuggestionItem(2, "tytul 2", "11", "22", "34"));
        itemList.add(new SuggestionItem(3, "tytul 3", "12", "22", "34"));
        itemList.add(new SuggestionItem(4, "tytul 4", "13", "22", "34"));

        final SuggestionAdapter adapter = new SuggestionAdapter(requireContext(), 0, itemList);
        listView.setAdapter(adapter);

        return root;
    }
}