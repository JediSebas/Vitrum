package com.jedisebas.vitrum.activity.ui.worker;

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

public class UsersAdapter extends ArrayAdapter<UsersItem> {

    public UsersAdapter(@NonNull final Context context, final int resource, @NonNull final List<UsersItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UsersItem item = getItem(position);
        holder.usersTv.setText(String.format("%s %s", item.getName(), item.getSurname()));

        return convertView;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView usersTv;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            usersTv = itemView.findViewById(R.id.usersTv);
        }
    }
}
