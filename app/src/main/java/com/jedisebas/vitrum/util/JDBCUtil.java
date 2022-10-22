package com.jedisebas.vitrum.util;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.activity.ui.main.CreateSuggestionActivity;
import com.jedisebas.vitrum.activity.ui.main.EntireSuggestionActivity;
import com.jedisebas.vitrum.activity.ui.main.SuggestionItem;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {

    private boolean doUpdate = false;
    private int voted;
    private int voteUp;
    private int voteDown;
    private final Context context;

    public JDBCUtil(final Context context) {
        this.context = context;
    }

    public void clickedVoteUp(final SuggestionItem item,
                              final TextView voteUpTv, final TextView voteDownTv,
                              final ImageView arrowUp, final ImageView arrowDown) {
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
    }

    public void clickedVoteDown(final SuggestionItem item,
                                final TextView voteUpTv, final TextView voteDownTv,
                                final ImageView arrowUp, final ImageView arrowDown) {
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
    }

    public void deleteSuggestion(final int idSuggestion) {
        if (idSuggestion != 0) {
            JDBCDeleteSuggestion jdbcDeleteSuggestion = new JDBCDeleteSuggestion(idSuggestion);
            jdbcDeleteSuggestion.t.start();
        }
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

    public void checkStatus(final int status, final int idSuggestion) {
        if (status == 0) {
            CreateSuggestionActivity.idSuggestion = idSuggestion;
            context.startActivity(new Intent(context, CreateSuggestionActivity.class));
        } else {
            EntireSuggestionActivity.idSuggestion = idSuggestion;
            context.startActivity(new Intent(context, EntireSuggestionActivity.class));
        }
    }

    public class JDBCCheckIsVote implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCCheckIsVote(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT id FROM `vote` WHERE id_suggestion = '" + id + "' AND id_inhabitant = '" + User.id + "';";
                final ResultSet rs = stmt.executeQuery(query);

                if (!rs.next()) {
                    @Language("RoomSql") final String query2 = "INSERT INTO `vote` (`id`, `id_inhabitant`, `id_suggestion`, `voted`) " +
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "UPDATE `vote` SET `voted` = '" + votedCode + "' WHERE " +
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT voted FROM `vote` WHERE id_suggestion = '" + id + "' AND id_inhabitant = '" + User.id + "';";
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "UPDATE `suggestion` SET `vote_up` = '" + voteUp + "' WHERE `suggestion`.`id` = '" + id + "';";
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "UPDATE `suggestion` SET `vote_down` = '" + voteDown + "' WHERE `suggestion`.`id` = '" + id + "';";
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT vote_up FROM `suggestion` WHERE id = '" + id + "';";
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
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT vote_down FROM `suggestion` WHERE id = '" + id + "';";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                setVoteDown(rs.getInt("vote_down"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCDeleteSuggestion implements Runnable {

        private final Thread t;
        private final int id;

        JDBCDeleteSuggestion(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "DELETE FROM suggestion WHERE `suggestion`.`id` = " + id + ";";

                JDBCDeleteComment jdbcDeleteComment = new JDBCDeleteComment(id);
                jdbcDeleteComment.t.start();
                jdbcDeleteComment.t.join();

                stmt.executeUpdate(query);
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private class JDBCDeleteComment implements Runnable {

            private final Thread t;
            private final int id;

            JDBCDeleteComment(final int id) {
                t = new Thread(this);
                this.id = id;
            }

            @Override
            public void run() {
                try (final Connection conn = DriverManager.getConnection(context.getString(R.string.db_url), context.getString(R.string.db_username), context.getString(R.string.db_password));
                     final Statement stmt = conn.createStatement()) {
                    @Language("RoomSql") final String query = "DELETE FROM comment WHERE `id_suggestion` = " + id + ";";
                    stmt.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
