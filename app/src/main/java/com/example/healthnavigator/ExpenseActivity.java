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

public class ExpenseActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private EditText etCategory;
    private EditText etAmount;
    private EditText etDescription;
    private EditText etDate;

    private Button btnAddExpense;

    private TextView tvExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        etCategory = findViewById(R.id.etCategory);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);

        btnAddExpense = findViewById(R.id.btnAddExpense);

        tvExpenses = findViewById(R.id.tvExpenses);

        btnAddExpense.setOnClickListener(v -> addExpense());

        loadExpenses();
    }

    private void addExpense() {

        String category = etCategory.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (category.isEmpty()) {
            etCategory.setError("Enter category");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (Exception e) {
            etAmount.setError("Enter valid amount");
            return;
        }

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        ApiModels.ExpenseRequest request =
                new ApiModels.ExpenseRequest(
                        category,
                        amount,
                        description,
                        date.isEmpty() ? null : date
                );

        Call<ApiModels.ExpenseResponse> call = apiService.addExpense(auth, request);

        call.enqueue(new Callback<ApiModels.ExpenseResponse>() {
            @Override
            public void onResponse(
                    Call<ApiModels.ExpenseResponse> call,
                    Response<ApiModels.ExpenseResponse> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful()) {

                    etCategory.setText("");
                    etAmount.setText("");
                    etDescription.setText("");
                    etDate.setText("");

                    loadExpenses();

                } else {
                    tvExpenses.setText("Could not add expense.");
                }
            }

            @Override
            public void onFailure(
                    Call<ApiModels.ExpenseResponse> call,
                    Throwable t
            ) {
                tvExpenses.setText("Error: " + t.getMessage());
            }
        });
    }

    private void loadExpenses() {

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<ApiModels.ExpenseResponse>> call = apiService.getExpenses(auth);

        call.enqueue(new Callback<List<ApiModels.ExpenseResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ApiModels.ExpenseResponse>> call,
                    Response<List<ApiModels.ExpenseResponse>> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    List<ApiModels.ExpenseResponse> expenses = response.body();

                    if (expenses.isEmpty()) {
                        tvExpenses.setText("No expenses found.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();

                    sb.append("Your Expenses:\n\n");

                    for (ApiModels.ExpenseResponse expense : expenses) {

                        sb.append("Category: ").append(expense.category).append("\n");
                        sb.append("Amount: ₹").append(expense.amount).append("\n");
                        sb.append("Date: ").append(expense.expense_date).append("\n");

                        if (expense.description != null && !expense.description.isEmpty()) {
                            sb.append("Description: ").append(expense.description).append("\n");
                        }

                        sb.append("\n");
                    }

                    tvExpenses.setText(sb.toString());

                } else {
                    tvExpenses.setText("Could not load expenses.");
                }
            }

            @Override
            public void onFailure(
                    Call<List<ApiModels.ExpenseResponse>> call,
                    Throwable t
            ) {
                tvExpenses.setText("Error: " + t.getMessage());
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