package com.jedisebas.vitrum.activity;

import static com.jedisebas.vitrum.util.User.worker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.User;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    private String password2;
    private boolean workerOfGmina;
    private boolean connectionError;
    private boolean approved;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView logoIv = findViewById(R.id.logoIv);
        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button loginBtn = findViewById(R.id.loginBtn);

        logoIv.setImageResource(R.drawable.logo);

        loginBtn.setOnClickListener(view -> {
            final String login = loginEt.getText().toString().trim();
            final String password = passwordEt.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.not_all_data), Toast.LENGTH_SHORT).show();
            } else {
                final JDBCLogin jdbcLogin = new JDBCLogin(login);
                jdbcLogin.t.start();

                try {
                    jdbcLogin.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                if (connectionError) {
                    Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(password2)) {
                    if (worker && workerOfGmina) {
                        Log.println(Log.DEBUG, "log", "pracownik gminy");
                        // zrob cos z gmina
                    } else if (worker) {
                        Log.println(Log.DEBUG, "log", "pracownik powiatu");
                        // zrob cos z powiatem
                    } else {
                        Log.println(Log.DEBUG, "log", "mieszkaniec");
                        JDBCCheckIfApproved jdbcCheckIfApproved = new JDBCCheckIfApproved(login);
                        jdbcCheckIfApproved.t.start();

                        try {
                            jdbcCheckIfApproved.t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }

                        if (approved) {
                            startActivity(new Intent(this, SuggestionActivity.class));
                        } else {
                            startActivity(new Intent(this, WaitingActivity.class));
                        }
                    }
                    Log.println(Log.DEBUG, "log", "hasla sie zgadzajo");
                } else {
                    Toast.makeText(this, getString(R.string.wrong_data), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.log_in));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setPassword2(final String password2) {
        this.password2 = password2;
    }

    void setWorkerOfGmina(final boolean workerOfGmina) {
        this.workerOfGmina = workerOfGmina;
    }

    void setConnectionError() {
        connectionError = true;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    private class JDBCLogin implements Runnable {

        private final String login;
        private final Thread t;

        private JDBCLogin(final String login) {
            t = new Thread(this);
            this.login = login;
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                Log.println(Log.ASSERT, "login", login);
                @Language("RoomSql")final String query1 = "SELECT password, worker_of_gmina FROM `worker` WHERE login = \"" + login + "\"";
                @Language("RoomSql")final String query2 = "SELECT password, " + User.unit + ", id FROM `inhabitant` WHERE email = \"" + login + "\"";
                final ResultSet rs;
                if (worker) {
                    rs = stmt.executeQuery(query1);
                    rs.next();
                    setWorkerOfGmina(rs.getBoolean("worker_of_gmina"));
                } else {
                    rs = stmt.executeQuery(query2);
                    rs.next();
                    User.unitId = rs.getInt(User.unit);
                    User.id = rs.getInt("id");
                }

                setPassword2(rs.getString("password"));
            } catch (SQLException e) {
                setConnectionError();
                e.printStackTrace();
            }
        }
    }

    private class JDBCCheckIfApproved implements Runnable {

        private final Thread t;
        private final String login;

        JDBCCheckIfApproved(final String login) {
            this.login = login;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                Log.println(Log.ASSERT, "login", login);
                @Language("RoomSql")final String query = "SELECT approved FROM `inhabitant` WHERE email = \"" + login + "\"";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                setApproved(rs.getBoolean("approved"));
            } catch (SQLException e) {
                setConnectionError();
                e.printStackTrace();
            }
        }
    }
}