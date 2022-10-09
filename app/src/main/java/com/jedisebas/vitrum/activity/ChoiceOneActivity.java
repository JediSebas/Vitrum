package com.jedisebas.vitrum.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.jedisebas.vitrum.R;

public class ChoiceOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_one);

        final ImageView logoIv = findViewById(R.id.logoIv);
        final Button loginBtn = findViewById(R.id.choiceLoginBtn);
        final Button signUpBtn = findViewById(R.id.signUpBtn);

        logoIv.setImageResource(R.drawable.logo);

        loginBtn.setOnClickListener(view -> {
            LoginActivity.worker = false;
            startActivity(new Intent(this, LoginActivity.class));
        });
        signUpBtn.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));

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
}