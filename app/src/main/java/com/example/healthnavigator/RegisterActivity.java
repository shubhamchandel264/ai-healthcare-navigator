package com.example.healthnavigator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private TextView tvStatus;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        tvStatus = findViewById(R.id.tvStatus);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Enter full name");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        tvStatus.setText("Registering...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        ApiModels.RegisterRequest request =
                new ApiModels.RegisterRequest(fullName, email, password, phone);

        Call<ApiModels.MessageResponse> call = apiService.register(request);

        call.enqueue(new Callback<ApiModels.MessageResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.MessageResponse> call,
                    Response<ApiModels.MessageResponse> response
            ) {

                if (response.isSuccessful()) {
                    tvStatus.setText("Registration successful. Please login.");
                    finish();
                } else {
                    tvStatus.setText("Registration failed. Email may already exist.");
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.MessageResponse> call,
                    Throwable t
            ) {
                tvStatus.setText("Error: " + t.getMessage());
            }
        });
    }
}