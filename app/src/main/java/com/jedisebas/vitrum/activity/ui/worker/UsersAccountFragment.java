package com.jedisebas.vitrum.activity.ui.worker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.User;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsersAccountFragment extends Fragment {

    private List<UsersItem> itemList;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_users_account, container, false);

        final ListView listView = root.findViewById(R.id.usersListView);
        itemList = new ArrayList<>();

        final JDBCGetInhabitant jdbcGetInhabitant = new JDBCGetInhabitant();
        jdbcGetInhabitant.t.start();

        try {
            jdbcGetInhabitant.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        final UsersAdapter usersAdapter = new UsersAdapter(requireContext(), 0, itemList);
        listView.setAdapter(usersAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final UsersItem item = (UsersItem) adapterView.getItemAtPosition(i);
            UserActivity.id = item.getId();
            startActivity(new Intent(getContext(), UserActivity.class));
        });

        return root;
    }

    private class JDBCGetInhabitant implements Runnable {

        private final Thread t;

        JDBCGetInhabitant() {
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT id, name, surname FROM `inhabitant` WHERE " + User.unit + " = " + User.unitId + " ORDER BY approved ASC;";
                final ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    final UsersItem item = new UsersItem();
                    item.setId(rs.getLong("id"));
                    item.setName(rs.getString("name"));
                    item.setSurname(rs.getString("surname"));
                    itemList.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}