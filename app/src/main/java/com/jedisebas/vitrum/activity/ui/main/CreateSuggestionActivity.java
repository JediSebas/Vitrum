package com.jedisebas.vitrum.activity.ui.main;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.BirthdatePicker;
import com.jedisebas.vitrum.util.JDBCUtil;
import com.jedisebas.vitrum.util.User;

import org.intellij.lang.annotations.Language;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateSuggestionActivity extends AppCompatActivity {

    private boolean visible = false;
    public static int idSuggestion = 0;

    private EditText titleEt;
    private EditText descriptionEt;
    private ImageView suggestionImage;

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    final Intent data = result.getData();

                    if (data != null) {
                        final Uri uri = data.getData();

                        try {
                            final InputStream inputStream = getContentResolver().openInputStream(uri);
                            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            runOnUiThread(() -> suggestionImage.setImageBitmap(bitmap));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_suggestion);

        final FloatingActionButton fab1 = findViewById(R.id.fab1);
        final FloatingActionButton saveFab = findViewById(R.id.saveFab);
        final FloatingActionButton sendFab = findViewById(R.id.sendFab);
        final FloatingActionButton cameraFab = findViewById(R.id.cameraFab);
        final FloatingActionButton deleteFab = findViewById(R.id.deleteFab);

        titleEt = findViewById(R.id.suggestionTitleEt);
        descriptionEt = findViewById(R.id.suggestionDescriptionEt);
        suggestionImage = findViewById(R.id.suggestionImageIv);

        final JDBCUtil jdbcUtil = new JDBCUtil(getBaseContext());

        final JDBCGetSuggestion jdbcGetSuggestion = new JDBCGetSuggestion(idSuggestion);
        jdbcGetSuggestion.t.start();

        try {
            jdbcGetSuggestion.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        fab1.setOnClickListener(view -> {
            if (visible) {
                saveFab.setVisibility(View.GONE);
                sendFab.setVisibility(View.GONE);
                cameraFab.setVisibility(View.GONE);
                deleteFab.setVisibility(View.GONE);
            } else {
                saveFab.setVisibility(View.VISIBLE);
                sendFab.setVisibility(View.VISIBLE);
                cameraFab.setVisibility(View.VISIBLE);
                deleteFab.setVisibility(View.VISIBLE);
            }
            visible = !visible;
        });

        saveFab.setOnClickListener(view -> {
            if (idSuggestion == 0) {
                saveSuggestion(0);
            } else {
                updateSuggestion(0);
            }
            super.onBackPressed();
        });

        sendFab.setOnClickListener(view -> {
            if (idSuggestion == 0) {
                saveSuggestion(1);
            } else {
                updateSuggestion(1);
            }
            super.onBackPressed();
        });

        cameraFab.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            } else {
                final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                getIntent.setType("image/*");

                final Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickIntent.setType("image/*");

                final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startForResult.launch(chooserIntent);
            }
        });

        deleteFab.setOnClickListener(view -> {
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

    private void checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                final Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void saveSuggestion(final int status) {
        final String title = titleEt.getText().toString().trim();
        final String description = descriptionEt.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.not_all_data), Toast.LENGTH_SHORT).show();
        } else {
            JDBCSaveSuggestion jdbcSaveSuggestion = new JDBCSaveSuggestion(idSuggestion, title, description, status);
            jdbcSaveSuggestion.t.start();

            try {
                jdbcSaveSuggestion.t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateSuggestion(final int status) {
        JDBCGetSuggestion jdbcGetSuggestion = new JDBCGetSuggestion(idSuggestion);
        jdbcGetSuggestion.t.start();

        try {
            jdbcGetSuggestion.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        final String title = titleEt.getText().toString().trim();
        final String description = descriptionEt.getText().toString().trim();

        JDBCUpdateSuggestion jdbcUpdateSuggestion = new JDBCUpdateSuggestion(idSuggestion, title, description, status);
        jdbcUpdateSuggestion.t.start();
    }

    private class JDBCSaveSuggestion implements Runnable {

        private final Thread t;
        private final int id;
        private final String title;
        private final String description;
        private final int status;

        JDBCSaveSuggestion(final int id, final String title, final String description, final int status) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.status = status;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "INSERT INTO `suggestion` (`id`, `title`, " +
                        "`description`, `vote_up`, `vote_down`, `id_inhabitant`, `status`, `reason`," +
                        " `datetime`, `id_gmina`, `id_powiat`) VALUES (" + id + ", '" + title + "', " +
                        "'" + description + "', '0', '0', '" + User.id + "', '" + status + "', '', '" + BirthdatePicker.getCurrentDateTime() + "',";
                if (User.unit.equals("id_gmina")) {
                    query += " '" + User.unitId + "', NULL);";
                } else {
                    query += " NULL, '" + User.unitId + "');";
                }
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCGetSuggestion implements Runnable {

        private final Thread t;
        private final int id;

        private JDBCGetSuggestion(final int id) {
            this.id = id;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "SELECT title, description FROM `suggestion` WHERE id = " + id + ";";
                final ResultSet rs = stmt.executeQuery(query);
                rs.next();
                final String title = rs.getString("title");
                final String description = rs.getString("description");

                runOnUiThread(() -> {
                    titleEt.setText(title);
                    descriptionEt.setText(description);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class JDBCUpdateSuggestion implements Runnable {

        private final Thread t;
        private final int id;
        private final String title;
        private final String description;
        private final int status;

        JDBCUpdateSuggestion(final int id, final String title, final String description, final int status) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.status = status;
            t = new Thread(this);
        }

        @Override
        public void run() {
            try (final Connection conn = DriverManager.getConnection(getString(R.string.db_url), getString(R.string.db_username), getString(R.string.db_password));
                 final Statement stmt = conn.createStatement()) {
                @Language("RoomSql") String query = "UPDATE `suggestion` SET `title` = '" + title +
                        "', `description` = '" + description + "', `status` = '" + status +"' WHERE `suggestion`.`id` = " + id + ";";
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}