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

public class CostEstimatorActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etProcedure;
    private Button btnEstimate;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_estimator);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etProcedure = findViewById(R.id.etProcedure);
        btnEstimate = findViewById(R.id.btnEstimate);
        tvResult = findViewById(R.id.tvResult);

        btnEstimate.setOnClickListener(v -> estimateCost());
    }

    private void estimateCost() {

        String procedure = etProcedure.getText().toString().trim();

        if (procedure.isEmpty()) {
            etProcedure.setError("Enter procedure name");
            return;
        }

        tvResult.setText("Estimating cost...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.CostEstimate> call =
                apiService.getCostEstimate(auth, procedure);

        call.enqueue(new Callback<ApiModels.CostEstimate>() {
            @Override
            public void onResponse(
                    Call<ApiModels.CostEstimate> call,
                    Response<ApiModels.CostEstimate> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    ApiModels.CostEstimate data = response.body();

                    String result =
                            "Procedure: " + data.procedure_name +
                                    "\n\nHospital Charges: ₹" + data.hospital_charges +
                                    "\nMedicine: ₹" + data.medicine +
                                    "\nTests: ₹" + data.tests +
                                    "\nDoctor Fees: ₹" + data.doctor_fees +
                                    "\n\nApproximate Total: ₹" + data.total +
                                    "\n\n" + data.notes;

                    tvResult.setText(result);

                } else if (response.code() == 404) {
                    tvResult.setText("Procedure estimate not found.");
                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.CostEstimate> call,
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