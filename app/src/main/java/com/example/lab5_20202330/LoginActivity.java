package com.example.lab5_20202330;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button ingresar = findViewById(R.id.ingresar);
        EditText data = findViewById(R.id.data);

        ingresar.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("PUCP_CODE", data.getText().toString());
            startActivity(intent);
        });

    }
}