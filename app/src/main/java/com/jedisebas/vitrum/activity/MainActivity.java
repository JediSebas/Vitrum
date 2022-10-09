package com.jedisebas.vitrum.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.jedisebas.vitrum.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView logoIv = findViewById(R.id.logoIv);
        final Button workerBtn = findViewById(R.id.workerBtn);
        final Button inhabitant = findViewById(R.id.inhabitantBtn);

        logoIv.setImageResource(R.drawable.logo);

        workerBtn.setOnClickListener(view -> {
            LoginActivity.worker = true;
            startActivity(new Intent(this, LoginActivity.class));
        });
        inhabitant.setOnClickListener(view -> startActivity(new Intent(this, ChoiceOneActivity.class)));
    }
}