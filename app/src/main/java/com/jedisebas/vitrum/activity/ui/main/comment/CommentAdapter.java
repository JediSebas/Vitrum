package com.jedisebas.vitrum.activity.ui.main.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jedisebas.vitrum.R;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentItem> {

    public CommentAdapter(@NonNull final Context context, final int resource, @NonNull final List<CommentItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommentItem item = getItem(position);

        holder.nameAndSurname.setText(String.format("%s %s", item.getName(), item.getSurname()));
        holder.commentTime.setText(item.getDatetime());
        holder.message.setText(item.getMessage());

        return convertView;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameAndSurname;
        private final TextView commentTime;
        private final TextView message;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            nameAndSurname = itemView.findViewById(R.id.nameAndSurnameTv);
            commentTime = itemView.findViewById(R.id.commentTimeTv);
            message = itemView.findViewById(R.id.commentMessageTv);
        }
    }
}
