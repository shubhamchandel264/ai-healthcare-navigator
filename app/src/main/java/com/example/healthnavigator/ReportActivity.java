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

public class ReportActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etReportType;
    private EditText etReportText;
    private Button btnExplain;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etReportType = findViewById(R.id.etReportType);
        etReportText = findViewById(R.id.etReportText);
        btnExplain = findViewById(R.id.btnExplain);
        tvResult = findViewById(R.id.tvResult);

        btnExplain.setOnClickListener(v -> explainReport());
    }

    private void explainReport() {

        String reportType = etReportType.getText().toString().trim();
        String reportText = etReportText.getText().toString().trim();

        if (reportType.isEmpty()) {
            etReportType.setError("Enter report type");
            return;
        }

        if (reportText.isEmpty()) {
            etReportText.setError("Enter report text");
            return;
        }

        tvResult.setText("Explaining report...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.ReportResponse> call =
                apiService.explainReport(
                        auth,
                        new ApiModels.ReportRequest(reportType, reportText)
                );

        call.enqueue(new Callback<ApiModels.ReportResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.ReportResponse> call,
                    Response<ApiModels.ReportResponse> response
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
                    Call<ApiModels.ReportResponse> call,
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