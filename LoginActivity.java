package com.example.travelbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelbooking.R;
import com.example.travelbooking.database.DatabaseHelper;
import com.example.travelbooking.models.User;
import com.example.travelbooking.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnQuickUser = findViewById(R.id.btnQuickUser);
        MaterialButton btnQuickAdmin = findViewById(R.id.btnQuickAdmin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> performLogin());

        btnQuickUser.setOnClickListener(v -> {
            etEmail.setText("john@example.com");
            etPassword.setText("Password123");
            performLogin();
        });

        btnQuickAdmin.setOnClickListener(v -> {
            etEmail.setText("admin@travel.com");
            etPassword.setText("Admin@123");
            performLogin();
        });

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        User user = databaseHelper.login(email, password);
        if (user != null) {
            sessionManager.createLoginSession(user);
            Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();

            Intent intent;
            if (user.isAdmin()) {
                intent = new Intent(LoginActivity.this, AdminActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password. Please verify your credentials.", Toast.LENGTH_LONG).show();
        }
    }
}
