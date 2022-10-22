package com.jedisebas.vitrum.activity.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.JDBCUtil;
import com.jedisebas.vitrum.util.User;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AllSuggestionFragment extends Fragment {

    private List<SuggestionItem> itemList;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_all_suggestion, container, false);

        final ListView listView = root.findViewById(R.id.allListView);
        itemList = new ArrayList<>();

        final JDBCGetAllSuggestion jdbcGetAllSuggestion = new JDBCGetAllSuggestion();
        jdbcGetAllSuggestion.t.start();

        try {
            jdbcGetAllSuggestion.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        final SuggestionAdapter adapter = new SuggestionAdapter(requireContext(), 0, itemList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final SuggestionItem item = (SuggestionItem) adapterView.getItemAtPosition(i);
            final LinearLayout voteUp = view.findViewById(R.id.voteUp);
            final LinearLayout voteDown = view.findViewById(R.id.voteDown);
            final LinearLayout comments = view.findViewById(R.id.comments);
            final TextView titleTv = view.findViewById(R.id.suggestionTitle);
            final TextView voteUpTv = view.findViewById(R.id.suggestionVoteUp);
            final TextView voteDownTv = view.findViewById(R.id.suggestionVoteDown);
            final ImageView image = view.findViewById(R.id.suggestionImage);
            final ImageView arrowUp = view.findViewById(R.id.arrowVoteUp);
            final ImageView arrowDown = view.findViewById(R.id.arrowVoteDown);

            final JDBCUtil jdbcUtil = new JDBCUtil(getContext());

            voteUp.setOnClickListener(view1 -> jdbcUtil.clickedVoteUp(item, voteUpTv, voteDownTv, arrowUp, arrowDown));
            voteDown.setOnClickListener(view1 -> jdbcUtil.clickedVoteDown(item, voteUpTv, voteDownTv, arrowUp, arrowDown));

            titleTv.setOnClickListener(view1 -> jdbcUtil.checkStatus(item.getStatus(), item.getId()));
            image.setOnClickListener(view1 -> jdbcUtil.checkStatus(item.getStatus(), item.getId()));
            comments.setOnClickListener(view1 -> jdbcUtil.checkStatus(item.getStatus(), item.getId()));
        });

        return root;
    }

    private class JDBCGetAllSuggestion implements Runnable {

        private final Thread t;
        private int commentsAmount;

        JDBCGetAllSuggestion() {
            t = new Thread(this);
        }

        void setCommentsAmount(final int commentsAmount) {
            this.commentsAmount = commentsAmount;
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT id, title, vote_up, vote_down, status FROM `suggestion` " +
                        "WHERE " + User.unit + " = '" + User.unitId + "' ORDER BY id DESC;";
                final ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    final SuggestionItem item = new SuggestionItem();
                    final int id = rs.getInt("id");

                    JDBCCountComments jdbcCountComments = new JDBCCountComments(id);
                    jdbcCountComments.t.start();
                    jdbcCountComments.t.join();

                    item.setId(id);
                    item.setTitle(rs.getString("title"));
                    item.setVoteUp(String.valueOf(rs.getInt("vote_up")));
                    item.setVoteDown(String.valueOf(rs.getInt("vote_down")));
                    item.setComments(String.valueOf(commentsAmount));
                    item.setStatus(rs.getInt("status"));
                    itemList.add(item);
                }
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private class JDBCCountComments implements Runnable {

            private final Thread t;
            private final int id;

            JDBCCountComments(final int id) {
                this.id = id;
                t = new Thread(this);
            }

            @Override
            public void run() {
                try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                     final Statement stmt = conn.createStatement()) {
                    @Language("RoomSql") String query = "SELECT id FROM `comment` " +
                            "WHERE id_suggestion = '" + id + "';";
                    final ResultSet rs = stmt.executeQuery(query);
                    int i = 0;

                    while (rs.next()) {
                        i++;
                    }
                    setCommentsAmount(i);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}