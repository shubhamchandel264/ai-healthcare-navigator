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

public class InsuranceActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etPolicy;
    private Button btnExplain;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etPolicy = findViewById(R.id.etPolicy);
        btnExplain = findViewById(R.id.btnExplain);
        tvResult = findViewById(R.id.tvResult);

        btnExplain.setOnClickListener(v -> explainInsurance());
    }

    private void explainInsurance() {

        String policyText = etPolicy.getText().toString().trim();

        if (policyText.isEmpty()) {
            etPolicy.setError("Enter insurance policy text");
            return;
        }

        tvResult.setText("Explaining policy...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.InsuranceResponse> call =
                apiService.explainInsurance(
                        auth,
                        new ApiModels.InsuranceRequest(policyText)
                );

        call.enqueue(new Callback<ApiModels.InsuranceResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.InsuranceResponse> call,
                    Response<ApiModels.InsuranceResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    tvResult.setText(response.body().explanation);
                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.InsuranceResponse> call,
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