package com.example.healthnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalsActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etCity;
    private EditText etSpecialty;
    private EditText etMaxFee;
    private Button btnSearch;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etCity = findViewById(R.id.etCity);
        etSpecialty = findViewById(R.id.etSpecialty);
        etMaxFee = findViewById(R.id.etMaxFee);
        btnSearch = findViewById(R.id.btnSearch);
        tvResult = findViewById(R.id.tvResult);

        btnSearch.setOnClickListener(v -> searchHospitals());
    }

    private void searchHospitals() {

        String city = etCity.getText().toString().trim();
        String specialty = etSpecialty.getText().toString().trim();
        String maxFeeText = etMaxFee.getText().toString().trim();

        Double maxFee = null;

        if (!maxFeeText.isEmpty()) {
            try {
                maxFee = Double.parseDouble(maxFeeText);
            } catch (Exception e) {
                tvResult.setText("Invalid maximum fee");
                return;
            }
        }

        tvResult.setText("Searching hospitals...");

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<ApiModels.Hospital>> call =
                apiService.getHospitals(
                        auth,
                        city.isEmpty() ? null : city,
                        specialty.isEmpty() ? null : specialty,
                        maxFee
                );

        call.enqueue(new Callback<List<ApiModels.Hospital>>() {
            @Override
            public void onResponse(
                    Call<List<ApiModels.Hospital>> call,
                    Response<List<ApiModels.Hospital>> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    List<ApiModels.Hospital> hospitals = response.body();

                    if (hospitals.isEmpty()) {
                        tvResult.setText("No hospitals found.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();

                    int index = 1;

                    for (ApiModels.Hospital hospital : hospitals) {

                        sb.append(index).append(". ").append(safe(hospital.name)).append("\n");
                        sb.append("City: ").append(safe(hospital.city)).append("\n");
                        sb.append("Specialties: ").append(safe(hospital.specialties)).append("\n");
                        sb.append("Consultation Fee: ₹").append(hospital.consultation_fee).append("\n");
                        sb.append("Rating: ").append(hospital.rating).append("\n");
                        sb.append("Insurance Accepted: ").append(safe(hospital.insurance_accepted)).append("\n");
                        sb.append("Emergency Services: ")
                                .append(hospital.emergency_services == 1 ? "Yes" : "No")
                                .append("\n");
                        sb.append("Phone: ").append(safe(hospital.phone)).append("\n\n");

                        index++;
                    }

                    tvResult.setText(sb.toString());

                } else {
                    tvResult.setText("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<List<ApiModels.Hospital>> call,
                    Throwable t
            ) {
                tvResult.setText("Error: " + t.getMessage());
            }
        });
    }

    private String safe(String value) {
        if (value == null || value.isEmpty()) {
            return "-";
        }
        return value;
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}