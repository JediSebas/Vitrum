package com.jedisebas.vitrum.activity.ui.main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.activity.ui.main.comment.CommentAdapter;
import com.jedisebas.vitrum.activity.ui.main.comment.CommentItem;
import com.jedisebas.vitrum.util.BirthdatePicker;
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

public class EntireSuggestionActivity extends AppCompatActivity {

    private List<CommentItem> itemList;

    private String title;
    private String description;
    private String voteUp;
    private String voteDown;
    private String comments;
    private int status;

    private boolean visible = false;

    public static int idSuggestion;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entire_suggestion);

        final ListView commentsListView = findViewById(R.id.commentsListView);
        itemList = new ArrayList<>();

        JDBCGetSuggestion jdbcGetSuggestion = new JDBCGetSuggestion();
        jdbcGetSuggestion.t.start();

        try {
            jdbcGetSuggestion.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        final CommentAdapter commentAdapter = new CommentAdapter(this, 0, itemList);
        commentsListView.setAdapter(commentAdapter);

        final LinearLayout voteUpL = findViewById(R.id.voteUp);
        final LinearLayout voteDownL = findViewById(R.id.voteDown);

        final TextView titleTv = findViewById(R.id.eSuggestionTitle);
        final TextView descriptionTv = findViewById(R.id.eSuggestionDescription);
        final TextView voteUpTv = findViewById(R.id.suggestionVoteUp);
        final TextView voteDownTv = findViewById(R.id.suggestionVoteDown);
        final TextView commentTv = findViewById(R.id.suggestionComments);

        final ImageView image = findViewById(R.id.eSuggestionImage);
        final ImageView arrowUp = findViewById(R.id.arrowVoteUp);
        final ImageView arrowDown = findViewById(R.id.arrowVoteDown);

        final Button approveBtn = findViewById(R.id.approveBtn);
        final Button disapproveBtn = findViewById(R.id.disapproveBtn);
        final Button sendBtn = findViewById(R.id.sendCommentBtn);

        final EditText reasonEt = findViewById(R.id.reasonEt);
        final EditText yourCommentEt = findViewById(R.id.yourCommentEt);

        final FloatingActionButton fab = findViewById(R.id.fab2);
        final FloatingActionButton infoFab = findViewById(R.id.infoFab);
        final FloatingActionButton delFab = findViewById(R.id.delFab);

        final JDBCUtil jdbcUtil = new JDBCUtil(getBaseContext());
        final SuggestionItem item = new SuggestionItem(idSuggestion, title, voteUp, voteDown, comments, status);

        titleTv.setText(title);
        descriptionTv.setText(description);
        voteUpTv.setText(voteUp);
        voteDownTv.setText(voteDown);
        commentTv.setText(comments);

        voteUpL.setOnClickListener(view -> jdbcUtil.clickedVoteUp(item, voteUpTv, voteDownTv, arrowUp, arrowDown));
        voteDownL.setOnClickListener(view -> jdbcUtil.clickedVoteDown(item, voteUpTv, voteDownTv, arrowUp, arrowDown));

        sendBtn.setOnClickListener(view -> {
            final String comment = yourCommentEt.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(this, getString(R.string.write_the_comment), Toast.LENGTH_SHORT).show();
            } else {
                JDBCSendComment jdbcSendComment = new JDBCSendComment(comment);
                jdbcSendComment.t.start();

                try {
                    jdbcSendComment.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                Toast.makeText(this, getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
            }
        });

        approveBtn.setOnClickListener(view -> sendReason(reasonEt.getText().toString().trim(), 2));
        disapproveBtn.setOnClickListener(view -> sendReason(reasonEt.getText().toString().trim(), -1));

        fab.setOnClickListener(view -> {
            if (visible) {
                infoFab.setVisibility(View.GONE);
                delFab.setVisibility(View.GONE);
            } else {
                infoFab.setVisibility(View.VISIBLE);
                delFab.setVisibility(View.VISIBLE);
            }
            visible = !visible;
        });

        infoFab.setOnClickListener(view -> {

        });

        delFab.setOnClickListener(view -> {
            jdbcUtil.deleteSuggestion(idSuggestion);
            super.onBackPressed();
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setTitle(final String title) {
        this.title = title;
    }

    void setDescription(final String description) {
        this.description = description;
    }

    void setVoteUp(final String voteUp) {
        this.voteUp = voteUp;
    }

    void setVoteDown(final String voteDown) {
        this.voteDown = voteDown;
    }

    void setComments(final String comments) {
        this.comments = comments;
    }

    void setStatus(final int status) {
        this.status = status;
    }

    private void sendReason(final String reason, final int status) {
        if (reason.isEmpty()) {
            Toast.makeText(this, getString(R.string.write_the_reason), Toast.LENGTH_SHORT).show();
        } else {
            JDBCSendReason jdbcSendReason = new JDBCSendReason(status);
            jdbcSendReason.t.start();

            try {
                jdbcSendReason.t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            Toast.makeText(this, getString(R.string.reason_success), Toast.LENGTH_SHORT).show();
        }
    }

    private class JDBCGetSuggestion implements Runnable {

        private final Thread t;
        private int commentsAmount;

        JDBCGetSuggestion() {
            t = new Thread(this);
        }

        void setCommentsAmount(final int commentsAmount) {
            this.commentsAmount = commentsAmount;
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT title, description, vote_up, vote_down, status FROM `suggestion` " +
                        "WHERE id = '" + idSuggestion + "';";
                final ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {

                    final JDBCGetComments jdbcGetComments = new JDBCGetComments(idSuggestion);
                    jdbcGetComments.t.start();
                    jdbcGetComments.t.join();

                    setTitle(rs.getString("title"));
                    setDescription(rs.getString("description"));
                    setVoteUp(String.valueOf(rs.getInt("vote_up")));
                    setVoteDown(String.valueOf(rs.getInt("vote_down")));
                    setComments(String.valueOf(commentsAmount));
                    setStatus(rs.getInt("status"));
                }
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private class JDBCGetComments implements Runnable {

            private final Thread t;
            private final int id;
            private String name;
            private String surname;

            JDBCGetComments(final int id) {
                this.id = id;
                t = new Thread(this);
            }

            @Override
            public void run() {
                try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                     final Statement stmt = conn.createStatement()) {
                    @Language("RoomSql") String query = "SELECT message, id_inhabitant, datetime FROM `comment` " +
                            "WHERE id_suggestion = '" + id + "';";
                    final ResultSet rs = stmt.executeQuery(query);
                    int i = 0;

                    while (rs.next()) {
                        final CommentItem item = new CommentItem();

                        final JDBCGetNameAndSurname jdbcGetNameAndSurname = new JDBCGetNameAndSurname(rs.getInt("id_inhabitant"));
                        jdbcGetNameAndSurname.t.start();
                        jdbcGetNameAndSurname.t.join();

                        item.setName(name);
                        item.setSurname(surname);
                        item.setMessage(rs.getString("message"));
                        item.setDateTime(rs.getTimestamp("datetime"));

                        itemList.add(item);
                        i++;
                    }
                    setCommentsAmount(i);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

            public void setName(final String name) {
                this.name = name;
            }

            public void setSurname(final String surname) {
                this.surname = surname;
            }

            private class JDBCGetNameAndSurname implements Runnable {

                private final Thread t;
                private final int id;

                JDBCGetNameAndSurname(final int id) {
                    this.id = id;
                    t = new Thread(this);
                }

                @Override
                public void run() {
                    try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                         final Statement stmt = conn.createStatement()) {
                        @Language("RoomSql") String query = "SELECT name, surname FROM `inhabitant` " +
                                "WHERE id = '" + id + "';";
                        final ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        setName(rs.getString("name"));
                        setSurname(rs.getString("surname"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class JDBCSendComment implements Runnable {

        private final Thread t;
        private final String message;

        JDBCSendComment(final String message) {
            t = new Thread(this);
            this.message = message;
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "INSERT INTO `comment` (`id`, `message`, " +
                        "`id_inhabitant`, `id_suggestion`, `datetime`) VALUES " +
                        "(NULL, '" + message + "', '" + User.id + "', '" + idSuggestion + "', '" + BirthdatePicker.getCurrentDateTime() + "');";

                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCSendReason implements Runnable {

        private final Thread t;
        private final int status;

        JDBCSendReason(final int status) {
            t = new Thread(this);
            this.status = status;
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "UPDATE `suggestion` SET `status` = '" + status + "' WHERE `suggestion`.`id` = '" + idSuggestion + "'';";

                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}