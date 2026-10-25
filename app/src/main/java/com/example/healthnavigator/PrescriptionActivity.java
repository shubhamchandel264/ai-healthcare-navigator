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

public class PrescriptionActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etPrescription;
    private Button btnSimplify;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etPrescription = findViewById(R.id.etPrescription);
        btnSimplify = findViewById(R.id.btnSimplify);
        tvResult = findViewById(R.id.tvResult);

        btnSimplify.setOnClickListener(v -> simplifyPrescription());
    }

    private void simplifyPrescription() {

        String prescriptionText = etPrescription.getText().toString().trim();

        if (prescriptionText.isEmpty()) {
            etPrescription.setError("Enter prescription text");
            return;
        }

        tvResult.setText("Processing...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.PrescriptionResponse> call =
                apiService.simplifyPrescription(
                        auth,
                        new ApiModels.PrescriptionRequest(prescriptionText)
                );

        call.enqueue(new Callback<ApiModels.PrescriptionResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.PrescriptionResponse> call,
                    Response<ApiModels.PrescriptionResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    tvResult.setText(response.body().simplified_explanation);
                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.PrescriptionResponse> call,
                    Throwable t
            ) {
                tvResult.setText("Error: " + t.getMessage());
            }
        });
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}