package com.jedisebas.vitrum.activity.ui.main;

import android.content.Intent;
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
import com.jedisebas.vitrum.util.User;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySuggestionFragment extends Fragment {

    private List<SuggestionItem> itemList;
    private boolean doUpdate = false;
    private int voted;
    private int voteUp;
    private int voteDown;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_my_suggestion, container, false);

        final ListView listView = root.findViewById(R.id.myListView);
        itemList = new ArrayList<>();

        final JDBCGetMySuggestion jdbcGetMySuggestion = new JDBCGetMySuggestion();
        jdbcGetMySuggestion.t.start();

        try {
            jdbcGetMySuggestion.t.join();
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

            voteUp.setOnClickListener(view1 -> {
                final int id = item.getId();

                final JDBCCheckIsVote jdbcCheckIsVote = new JDBCCheckIsVote(id);
                jdbcCheckIsVote.t.start();

                try {
                    jdbcCheckIsVote.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                if (doUpdate) {
                    final JDBCGetVoted jdbcGetVoted = new JDBCGetVoted(id);
                    jdbcGetVoted.t.start();

                    try {
                        jdbcGetVoted.t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }

                    String newVoteUp = item.getVoteUp();
                    String newVoteDown = item.getVoteDown();

                    if (voted == 0) {
                        final int vu = getVoteUp(id);
                        jdbcUpdateVote(id, 1);
                        jdbcSetVoteUp(id, vu + 1);
                        newVoteUp = String.valueOf(Integer.parseInt(newVoteUp) + 1);
                        arrowUp.setImageResource(R.drawable.arrow_up_green);
                    } else if (voted == 1) {
                        final int vu = getVoteUp(id);
                        jdbcUpdateVote(id, 0);
                        jdbcSetVoteUp(id, vu - 1);
                        newVoteUp = String.valueOf(Integer.parseInt(newVoteUp) - 1);
                        arrowUp.setImageResource(R.drawable.arrow_up_white);
                    } else {
                        final int vu = getVoteUp(id);
                        final int vd = getVoteDown(id);
                        jdbcUpdateVote(id, 1);
                        jdbcSetVoteUp(id, vu + 1);
                        jdbcSetVoteDown(id, vd - 1);
                        newVoteUp = String.valueOf(Integer.parseInt(newVoteUp) + 1);
                        newVoteDown = String.valueOf(Integer.parseInt(newVoteDown) - 1);
                        arrowUp.setImageResource(R.drawable.arrow_up_green);
                        arrowDown.setImageResource(R.drawable.arrow_down_white);
                    }

                    item.setVoteUp(newVoteUp);
                    item.setVoteDown(newVoteDown);
                    voteUpTv.setText(newVoteUp);
                    voteDownTv.setText(newVoteDown);
                }
            });

            voteDown.setOnClickListener(view1 -> {
                final int id = item.getId();

                final JDBCCheckIsVote jdbcCheckIsVote = new JDBCCheckIsVote(id);
                jdbcCheckIsVote.t.start();

                try {
                    jdbcCheckIsVote.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                if (doUpdate) {
                    final JDBCGetVoted jdbcGetVoted = new JDBCGetVoted(id);
                    jdbcGetVoted.t.start();

                    try {
                        jdbcGetVoted.t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }

                    String newVoteUp = item.getVoteUp();
                    String newVoteDown = item.getVoteDown();

                    if (voted == 0) {
                        final int vd = getVoteDown(id);
                        jdbcUpdateVote(id, -1);
                        jdbcSetVoteDown(id, vd + 1);
                        newVoteDown = String.valueOf(Integer.parseInt(newVoteDown) + 1);
                        arrowDown.setImageResource(R.drawable.arrow_down_red);
                    } else if (voted == 1) {
                        final int vu = getVoteUp(id);
                        final int vd = getVoteDown(id);
                        jdbcUpdateVote(id, -1);
                        jdbcSetVoteUp(id, vu - 1);
                        jdbcSetVoteDown(id, vd + 1);
                        newVoteUp = String.valueOf(Integer.parseInt(newVoteUp) - 1);
                        newVoteDown = String.valueOf(Integer.parseInt(newVoteDown) + 1);
                        arrowDown.setImageResource(R.drawable.arrow_down_red);
                        arrowUp.setImageResource(R.drawable.arrow_up_white);
                    } else {
                        final int vd = getVoteDown(id);
                        jdbcUpdateVote(id, 0);
                        jdbcSetVoteDown(id, vd - 1);
                        newVoteDown = String.valueOf(Integer.parseInt(newVoteDown) - 1);
                        arrowDown.setImageResource(R.drawable.arrow_down_white);
                    }

                    item.setVoteUp(newVoteUp);
                    item.setVoteDown(newVoteDown);
                    voteUpTv.setText(newVoteUp);
                    voteDownTv.setText(newVoteDown);
                }
            });

            titleTv.setOnClickListener(view1 -> checkStatus(item.getStatus(), item.getId()));
            image.setOnClickListener(view1 -> checkStatus(item.getStatus(), item.getId()));
            comments.setOnClickListener(view1 -> checkStatus(item.getStatus(), item.getId()));
        });

        return root;
    }

    void setDoUpdate(final boolean doUpdate) {
        this.doUpdate = doUpdate;
    }

    void setVoted(final int voted) {
        this.voted = voted;
    }

    void setVoteUp(final int voteUp) {
        this.voteUp = voteUp;
    }

    int getVoteUp(final int id) {
        JDBCGetVoteUp jdbcGetVoteUp = new JDBCGetVoteUp(id);
        jdbcGetVoteUp.t.start();

        try {
            jdbcGetVoteUp.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return voteUp;
    }

    void setVoteDown(final int voteDown) {
        this.voteDown = voteDown;
    }

    int getVoteDown(final int id) {
        JDBCGetVoteDown jdbcGetVoteDown = new JDBCGetVoteDown(id);
        jdbcGetVoteDown.t.start();

        try {
            jdbcGetVoteDown.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return voteDown;
    }

    private void jdbcUpdateVote(final int id, final int votedCodeParam) {
        final JDBCUpdateVote jdbcUpdateVote = new JDBCUpdateVote(id, votedCodeParam);
        jdbcUpdateVote.t.start();

        try {
            jdbcUpdateVote.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void jdbcSetVoteUp(final int id, final int voteUpParam) {
        final JDBCSetVoteUp jdbcSetVoteUp = new JDBCSetVoteUp(id, voteUpParam);
        jdbcSetVoteUp.t.start();

        try {
            jdbcSetVoteUp.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void jdbcSetVoteDown(final int id, final int voteDownParam) {
        final JDBCSetVoteDown jdbcSetVoteDown = new JDBCSetVoteDown(id, voteDownParam);
        jdbcSetVoteDown.t.start();

        try {
            jdbcSetVoteDown.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void checkStatus(final int status, final int idSuggestion) {
        if (status == 0) {
            CreateSuggestionActivity.idSuggestion = idSuggestion;
            startActivity(new Intent(getContext(), CreateSuggestionActivity.class));
        } else {
            EntireSuggestionActivity.idSuggestion = idSuggestion;
            startActivity(new Intent(getContext(), EntireSuggestionActivity.class));
        }
    }

    private class JDBCGetMySuggestion implements Runnable {

        private final Thread t;
        private int commentsAmount;

        JDBCGetMySuggestion() {
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
                        "WHERE id_inhabitant = '" + User.id + "' AND " + User.unit + " = '" + User.unitId + "';";
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

    private class JDBCCheckIsVote implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCCheckIsVote(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT id FROM `vote` WHERE id_suggestion = '" + id + "' AND id_inhabitant = '" + User.id + "';";
                final ResultSet rs = stmt.executeQuery(query);

                if (!rs.next()) {
                    @Language("RoomSql") String query2 = "INSERT INTO `vote` (`id`, `id_inhabitant`, `id_suggestion`, `voted`) " +
                            "VALUES (NULL, '" + User.id + "', '" + id +"', '0');";
                    stmt.executeUpdate(query2);
                    setDoUpdate(false);
                } else {
                    setDoUpdate(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCUpdateVote implements Runnable {

        private final Thread t;
        private final int id;
        private final int votedCode;

        private JDBCUpdateVote(final int id, final int votedCode) {
            this.id = id;
            this.votedCode = votedCode;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "UPDATE `vote` SET `voted` = '" + votedCode + "' WHERE " +
                        "id_suggestion = '" + id + "' AND id_inhabitant = '" + User.id + "';";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCGetVoted implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCGetVoted(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT voted FROM `vote` WHERE id_suggestion = '" + id + "' AND id_inhabitant = '" + User.id + "';";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                setVoted(rs.getInt("voted"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCSetVoteUp implements Runnable {

        private final Thread t;
        private final int id;
        private final int voteUp;

        private JDBCSetVoteUp(final int id, final int voteUp) {
            this.id = id;
            this.voteUp = voteUp;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "UPDATE `suggestion` SET `vote_up` = '" + voteUp + "' WHERE `suggestion`.`id` = '" + id + "';";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCSetVoteDown implements Runnable {

        private final Thread t;
        private final int id;
        private final int voteDown;

        private JDBCSetVoteDown(final int id, final int voteDown) {
            this.id = id;
            this.voteDown = voteDown;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "UPDATE `suggestion` SET `vote_down` = '" + voteDown + "' WHERE `suggestion`.`id` = '" + id + "';";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCGetVoteUp implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCGetVoteUp(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT vote_up FROM `suggestion` WHERE id = '" + id + "';";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                setVoteUp(rs.getInt("vote_up"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCGetVoteDown implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCGetVoteDown(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT vote_down FROM `suggestion` WHERE id = '" + id + "';";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                setVoteDown(rs.getInt("vote_down"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}