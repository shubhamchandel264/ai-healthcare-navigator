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

public class MedicineInteractionActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etMedicines;
    private Button btnCheck;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_interaction);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etMedicines = findViewById(R.id.etMedicines);
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);

        btnCheck.setOnClickListener(v -> checkInteraction());
    }

    private void checkInteraction() {

        String medicines = etMedicines.getText().toString().trim();

        if (medicines.isEmpty()) {
            etMedicines.setError("Enter medicines separated by comma");
            return;
        }

        tvResult.setText("Checking interactions...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.InteractionResponse> call =
                apiService.checkMedicineInteractions(auth, medicines);

        call.enqueue(new Callback<ApiModels.InteractionResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.InteractionResponse> call,
                    Response<ApiModels.InteractionResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    ApiModels.InteractionResponse data = response.body();

                    if (data.warnings == null || data.warnings.isEmpty()) {

                        String message = "No interaction warning found in sample database.\n";

                        if (data.note != null) {
                            message = message + "\n" + data.note;
                        }

                        tvResult.setText(message);

                    } else {

                        StringBuilder sb = new StringBuilder();

                        sb.append("Interaction warnings:\n\n");

                        for (ApiModels.InteractionWarning warning : data.warnings) {

                            sb.append("Medicines: ")
                                    .append(warning.medicine_a)
                                    .append(" + ")
                                    .append(warning.medicine_b)
                                    .append("\n");

                            sb.append("Severity: ").append(warning.severity).append("\n");
                            sb.append("Description: ").append(warning.description).append("\n\n");
                        }

                        if (data.note != null) {
                            sb.append(data.note);
                        }

                        tvResult.setText(sb.toString());
                    }

                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.InteractionResponse> call,
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