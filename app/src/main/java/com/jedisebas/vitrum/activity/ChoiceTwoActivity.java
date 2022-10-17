package com.jedisebas.vitrum.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.jedisebas.vitrum.R;
import com.jedisebas.vitrum.util.User;

public class ChoiceTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_two);

        final ImageView logoIv = findViewById(R.id.logoIv);
        final Button gmina = findViewById(R.id.gminaBtn);
        final Button powiat = findViewById(R.id.powiatBtn);

        logoIv.setImageResource(R.drawable.logo);

        gmina.setOnClickListener(view -> goToLogin("id_gmina"));
        powiat.setOnClickListener(view -> goToLogin("id_powiat"));

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

    private void goToLogin(final String unit) {
        User.worker = false;
        User.unit = unit;
        startActivity(new Intent(this, LoginActivity.class));
    }
}