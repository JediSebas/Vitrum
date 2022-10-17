package com.jedisebas.vitrum.activity.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jedisebas.vitrum.R;

import java.util.ArrayList;
import java.util.List;

public class MySuggestionFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_my_suggestion, container, false);

        final ListView listView = root.findViewById(R.id.myListView);
        final List<SuggestionItem> itemList = new ArrayList<>();

        itemList.add(new SuggestionItem("tytul 1", "10", "20", "30"));
        itemList.add(new SuggestionItem("tytul 2", "11", "22", "34"));
        itemList.add(new SuggestionItem("tytul 3", "12", "22", "34"));
        itemList.add(new SuggestionItem("tytul 4", "13", "22", "34"));

        final SuggestionAdapter adapter = new SuggestionAdapter(requireContext(), 0, itemList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final SuggestionItem item = (SuggestionItem) adapterView.getItemAtPosition(i);
            final TextView titleTv = view.findViewById(R.id.suggestionTitle);
            final TextView voteUpTv = view.findViewById(R.id.suggestionVoteUp);
            final TextView voteDownTv = view.findViewById(R.id.suggestionVoteDown);
            final TextView commentsTv = view.findViewById(R.id.suggestionComments);
            final ImageView image = view.findViewById(R.id.suggestionImage);

            voteUpTv.setOnClickListener(view1 -> {
                final String newVoteUp = String.valueOf(Integer.parseInt(item.getVoteUp()) + 1);
                item.setVoteUp(newVoteUp);
                voteUpTv.setText(newVoteUp);
            });

            voteDownTv.setOnClickListener(view1 -> {
                final String newVoteDown = String.valueOf(Integer.parseInt(item.getVoteDown()) + 1);
                item.setVoteDown(newVoteDown);
                voteDownTv.setText(newVoteDown);
            });

//            titleTv.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EntireSuggestionActivity.class)));
//            image.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EntireSuggestionActivity.class)));
//            commentsTv.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EntireSuggestionActivity.class)));
        });

        return root;
    }
}