package com.jedisebas.vitrum.activity.ui.worker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.BirthdatePicker;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class UserActivity extends AppCompatActivity {

    static long id;
    private String name;
    private String surname;
    private String birthdate;
    private String town;
    private String street;
    private String number;
    private String postCode;
    private String post;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        final JDBCGetEntireInhabitant jdbcGetEntireInhabitant = new JDBCGetEntireInhabitant();
        jdbcGetEntireInhabitant.t.start();

        final TextView nameTv = findViewById(R.id.userNameTv);
        final TextView surnameTv = findViewById(R.id.userSurnameTv);
        final TextView birthdateTv = findViewById(R.id.userBirthdateTv);
        final TextView townTv = findViewById(R.id.userTownTv);
        final TextView streetTv = findViewById(R.id.userStreetTv);
        final TextView numberTv = findViewById(R.id.userNumberTv);
        final TextView postCodeTv = findViewById(R.id.userPostcodeTv);
        final TextView postTv = findViewById(R.id.userPostTv);

        final Button approveBtn = findViewById(R.id.userApproveBtn);
        final Button disapproveBtn = findViewById(R.id.userDisapproveBtn);

        try {
            jdbcGetEntireInhabitant.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        nameTv.setText(name);
        surnameTv.setText(surname);
        birthdateTv.setText(birthdate);
        townTv.setText(town);
        streetTv.setText(street);
        numberTv.setText(number);
        postCodeTv.setText(postCode);
        postTv.setText(post);

        approveBtn.setOnClickListener(view -> {
            final JDBCSetApprovedInhabitant jdbcSetApprovedInhabitant = new JDBCSetApprovedInhabitant();
            jdbcSetApprovedInhabitant.t.start();
            super.onBackPressed();
        });

        disapproveBtn.setOnClickListener(view -> {
            final JDBCDeleteInhabitant jdbcDeleteInhabitant = new JDBCDeleteInhabitant();
            jdbcDeleteInhabitant.t.start();
            super.onBackPressed();
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.title_activity_suggestion));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setName(final String name) {
        this.name = name;
    }

    void setSurname(final String surname) {
        this.surname = surname;
    }

    void setBirthdate(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        birthdate = BirthdatePicker.getDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    void setTown(final String town) {
        this.town = town;
    }

    void setStreet(final String street) {
        this.street = street;
    }

    void setNumber(final String number) {
        this.number = number;
    }

    void setPostCode(final String postCode) {
        this.postCode = postCode;
    }

    void setPost(final String post) {
        this.post = post;
    }

    private class JDBCGetEntireInhabitant implements Runnable {

        private final Thread t;

        JDBCGetEntireInhabitant() {
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "SELECT name, surname, birthdate, town, street, number, post_code, post FROM `inhabitant` WHERE id = " + id + ";";
                final ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    setName(rs.getString("name"));
                    setSurname(rs.getString("surname"));
                    setBirthdate(rs.getDate("birthdate"));
                    setTown(rs.getString("town"));
                    setStreet(rs.getString("street"));
                    setNumber(rs.getString("number"));
                    setPostCode(rs.getString("post_code"));
                    setPost(rs.getString("post"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCSetApprovedInhabitant implements Runnable {

        private final Thread t;

        JDBCSetApprovedInhabitant() {
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "UPDATE `inhabitant` SET `approved` = '1' WHERE " +
                        "id = '" + id + "';";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCDeleteInhabitant implements Runnable {

        private final Thread t;

        JDBCDeleteInhabitant() {
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") final String query = "DELETE FROM inhabitant WHERE `id` = " + id + ";";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}