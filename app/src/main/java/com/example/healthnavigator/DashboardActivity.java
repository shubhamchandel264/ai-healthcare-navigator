package com.example.healthnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private TextView tvDashboard;

    private Button btnLogout;
    private Button btnPrescription;
    private Button btnReport;
    private Button btnSpecialist;
    private Button btnHospitals;
    private Button btnCost;
    private Button btnInsurance;
    private Button btnInteraction;
    private Button btnExpenses;
    private Button btnDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        tvDashboard = findViewById(R.id.tvDashboard);

        btnLogout = findViewById(R.id.btnLogout);
        btnPrescription = findViewById(R.id.btnPrescription);
        btnReport = findViewById(R.id.btnReport);
        btnSpecialist = findViewById(R.id.btnSpecialist);
        btnHospitals = findViewById(R.id.btnHospitals);
        btnCost = findViewById(R.id.btnCost);
        btnInsurance = findViewById(R.id.btnInsurance);
        btnInteraction = findViewById(R.id.btnInteraction);
        btnExpenses = findViewById(R.id.btnExpenses);
        btnDocuments = findViewById(R.id.btnDocuments);

        loadDashboard();

        btnLogout.setOnClickListener(v -> logout());

        btnPrescription.setOnClickListener(v -> openActivity(PrescriptionActivity.class));
        btnReport.setOnClickListener(v -> openActivity(ReportActivity.class));
        btnSpecialist.setOnClickListener(v -> openActivity(SpecialistActivity.class));
        btnHospitals.setOnClickListener(v -> openActivity(HospitalsActivity.class));
        btnCost.setOnClickListener(v -> openActivity(CostEstimatorActivity.class));
        btnInsurance.setOnClickListener(v -> openActivity(InsuranceActivity.class));
        btnInteraction.setOnClickListener(v -> openActivity(MedicineInteractionActivity.class));
        btnExpenses.setOnClickListener(v -> openActivity(ExpenseActivity.class));
        btnDocuments.setOnClickListener(v -> openActivity(DocumentsActivity.class));
    }

    private void loadDashboard() {

        String auth = sessionManager.getAuthHeader();

        if (auth == null) {
            logout();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiModels.DashboardResponse> call = apiService.getDashboard(auth);

        call.enqueue(new Callback<ApiModels.DashboardResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.DashboardResponse> call,
                    Response<ApiModels.DashboardResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    ApiModels.DashboardResponse data = response.body();

                    String dashboardText =
                            "Upcoming Appointments: " + data.upcoming_appointments +
                                    "\nActive Medicines: " + data.active_medicines +
                                    "\nTotal Prescriptions: " + data.total_prescriptions +
                                    "\nMonthly Expense: ₹" + data.monthly_expense +
                                    "\n\nThis app provides educational guidance only. " +
                                    "It does not replace doctors.";

                    tvDashboard.setText(dashboardText);

                } else {
                    tvDashboard.setText("Could not load dashboard.");
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.DashboardResponse> call,
                    Throwable t
            ) {
                tvDashboard.setText("Error: " + t.getMessage());
            }
        });
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void logout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}