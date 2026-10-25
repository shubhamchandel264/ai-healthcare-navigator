package com.example.healthnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvStatus;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            openDashboard();
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvStatus = findViewById(R.id.tvStatus);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        tvStatus.setText("Logging in...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.LoginResponse> call =
                apiService.login(new ApiModels.LoginRequest(email, password));

        call.enqueue(new Callback<ApiModels.LoginResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.LoginResponse> call,
                    Response<ApiModels.LoginResponse> response
            ) {

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().access_token;
                    sessionManager.saveToken(token);

                    tvStatus.setText("Login successful");

                    openDashboard();
                    finish();

                } else {
                    tvStatus.setText("Invalid email or password");
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.LoginResponse> call,
                    Throwable t
            ) {
                tvStatus.setText("Error: " + t.getMessage());
            }
        });
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}