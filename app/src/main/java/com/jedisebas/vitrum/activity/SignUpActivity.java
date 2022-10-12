package com.jedisebas.vitrum.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.BirthdatePicker;

import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private TextView birthdateTv;
    private String birthdate;
    private int idGmina = 0;
    private int idPowiat = 0;
    private boolean connectionError;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        birthdateTv = findViewById(R.id.birthdateTv);
        final EditText nameEt = findViewById(R.id.nameEt);
        final EditText surnameEt = findViewById(R.id.surnameEt);
        final EditText peselEt = findViewById(R.id.peselEt);
        final EditText streetEt = findViewById(R.id.streetEt);
        final EditText townEt = findViewById(R.id.townEt);
        final EditText numberEt = findViewById(R.id.nrEt);
        final EditText postCodeEt = findViewById(R.id.postcodeEt);
        final EditText postEt = findViewById(R.id.postEt);
        final EditText emailEt = findViewById(R.id.emailEt);
        final EditText passwordEt = findViewById(R.id.signPasswordEt);
        final EditText phoneEt = findViewById(R.id.phoneEt);

        final Button signUpBtn = findViewById(R.id.signupBtn);
        final Button birthdateBtn = findViewById(R.id.birthdateBtn);

        final Spinner gminy = findViewById(R.id.gminyS);
        final Spinner powiat = findViewById(R.id.powiatS);

        final ArrayAdapter<CharSequence> adapterG = ArrayAdapter.createFromResource(this, R.array.gminy, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> adapterP = ArrayAdapter.createFromResource(this, R.array.powiat, android.R.layout.simple_spinner_item);

        gminy.setOnItemSelectedListener(this);
        gminy.setPrompt(getString(R.string.gmina));
        gminy.setAdapter(adapterG);

        powiat.setOnItemSelectedListener(this);
        powiat.setPrompt(getString(R.string.powiat));
        powiat.setAdapter(adapterP);

        birthdateBtn.setOnClickListener(view -> {
            BirthdatePicker birthdatePicker = new BirthdatePicker();
            birthdatePicker.show(getSupportFragmentManager(), "DATE PICK");
        });

        signUpBtn.setOnClickListener(view -> {
            String name = nameEt.getText().toString().trim();
            String surname = surnameEt.getText().toString().trim();
            String pesel = peselEt.getText().toString().trim();
            String street = streetEt.getText().toString().trim();
            String town = townEt.getText().toString().trim();
            String number = numberEt.getText().toString().trim();
            String postCode = postCodeEt.getText().toString().trim();
            String post = postEt.getText().toString().trim();
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            String phone = phoneEt.getText().toString().trim();

            street = street.isEmpty() ? null : street;

            if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || town.isEmpty() ||
                number.isEmpty() || postCode.isEmpty() || post.isEmpty() || idGmina == 0 || idPowiat == 0) {
                Toast.makeText(this, getString(R.string.not_all_data), Toast.LENGTH_SHORT).show();
            } else {
                JDBCSignUp jdbcSignUp = new JDBCSignUp(pesel, name, surname, birthdate, town, street, number, postCode, post, email, password, phone, idGmina, idPowiat);
                jdbcSignUp.t.start();

                try {
                    jdbcSignUp.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                if (connectionError) {
                    Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, getString(R.string.registration), Toast.LENGTH_SHORT).show();
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.sign_up));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
        String selected = adapterView.getItemAtPosition(i).toString();

        if (selected.charAt(0) == selected.toLowerCase(Locale.ROOT).charAt(0)) {
            idPowiat = i;
        } else {
            idGmina = i;
        }

        Log.println(Log.DEBUG, "selected", selected);
    }

    @Override
    public void onNothingSelected(final AdapterView<?> adapterView) {
        // Auto-generated method
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int month, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        birthdate = getCorrectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        birthdateTv.setText(birthdate);
    }

    private String getCorrectDate(final int year, int month, final int day) {
        return year + "-" + addZero(++month) + "-" + addZero(day);
    }

    private String addZero(final int x) {
        if (x < 10) {
            return "0" + x;
        } else {
            return String.valueOf(x);
        }
    }

    void setConnectionError() {
        connectionError = true;
    }

    private class JDBCSignUp implements Runnable {

        private final Thread t;
        private final String pesel;
        private final String name;
        private final String surname;
        private final String birthdate;
        private final String town;
        private final String street;
        private final String number;
        private final String postCode;
        private final String post;
        private final String email;
        private final String password;
        private final String phone;
        private final int idGmina;
        private final int idPowiat;

        private JDBCSignUp(final String pesel, final String name, final String surname,
                           final String birthdate, final String town, final String street,
                           final String number, final String postCode, final String post,
                           final String email, final String password, final String phone,
                           final int id_gmina, final int id_powiat) {
            this.pesel = pesel;
            this.name = name;
            this.surname = surname;
            this.birthdate = birthdate;
            this.town = town;
            this.street = street;
            this.number = number;
            this.postCode = postCode;
            this.post = post;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.idGmina = id_gmina;
            this.idPowiat = id_powiat;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                final String approved = "0";
                @Language("RoomSql") final String query = "INSERT INTO `inhabitant` (`id`, `name`, `surname`, `birthdate`," +
                        " `town`, `street`, `number`, `post_code`, `post`, `email`, `password`, " +
                        "`phone`, `approved`, `id_gmina`, `id_powiat`) VALUES " +
                        "('" + pesel + "', '" + name + "', '" + surname + "', '" + birthdate + "'," +
                        " '" + town + "', '" + street + "', '" + number + "', '" + postCode + "'," +
                        " '" + post + "', '" + email + "', '" + password + "', '" + phone + "'," +
                        " '" + approved + "', '" + idGmina + "', '" + idPowiat +"');";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                setConnectionError();
                e.printStackTrace();
            }
        }
    }
}