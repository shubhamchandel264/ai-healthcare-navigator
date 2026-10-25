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

public class SpecialistActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etSymptom;
    private Button btnFind;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etSymptom = findViewById(R.id.etSymptom);
        btnFind = findViewById(R.id.btnFind);
        tvResult = findViewById(R.id.tvResult);

        btnFind.setOnClickListener(v -> findSpecialist());
    }

    private void findSpecialist() {

        String symptom = etSymptom.getText().toString().trim();

        if (symptom.isEmpty()) {
            etSymptom.setError("Enter symptom");
            return;
        }

        tvResult.setText("Finding specialist...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.SymptomResponse> call =
                apiService.getSpecialist(
                        auth,
                        new ApiModels.SymptomRequest(symptom)
                );

        call.enqueue(new Callback<ApiModels.SymptomResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.SymptomResponse> call,
                    Response<ApiModels.SymptomResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    ApiModels.SymptomResponse data = response.body();

                    String result =
                            "Suggested Specialist: " + data.suggested_specialist +
                                    "\nUrgency: " + data.urgency +
                                    "\n\n" + data.disclaimer;

                    tvResult.setText(result);

                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.SymptomResponse> call,
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