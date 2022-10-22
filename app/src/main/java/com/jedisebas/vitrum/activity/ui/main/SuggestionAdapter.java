package com.jedisebas.vitrum.activity.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jedisebas.vitrum.R;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<SuggestionItem> {

    public SuggestionAdapter(@NonNull final Context context, final int resource, @NonNull final List<SuggestionItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SuggestionItem item = getItem(position);

        holder.title.setText(item.getTitle());
        holder.imageView.setImageResource(R.drawable.logo);
        holder.voteUp.setText(item.getVoteUp());
        holder.voteDown.setText(item.getVoteDown());
        holder.comments.setText(item.getComments());

        return convertView;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView imageView;
        private final TextView voteUp;
        private final TextView voteDown;
        private final TextView comments;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.suggestionTitle);
            imageView = itemView.findViewById(R.id.suggestionImage);
            voteUp = itemView.findViewById(R.id.suggestionVoteUp);
            voteDown = itemView.findViewById(R.id.suggestionVoteDown);
            comments = itemView.findViewById(R.id.suggestionComments);
        }
    }
}
