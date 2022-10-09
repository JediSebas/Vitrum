package com.jedisebas.vitrum.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jedisebas.vitrum.R;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    private String password2;
    static boolean worker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button loginBtn = findViewById(R.id.loginBtn);

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

                if (password.equals(password2)) {
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
                @Language("RoomSql")final String query1 = "SELECT password FROM `worker` WHERE login = \"" + login + "\"";
                @Language("RoomSql")final String query2 = "SELECT password FROM `inhabitant` WHERE email = \"" + login + "\"";
                final ResultSet rs;
                if (worker) {
                    rs = stmt.executeQuery(query1);
                } else {
                    rs = stmt.executeQuery(query2);
                }
                rs.next();
                setPassword2(rs.getString("password"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}