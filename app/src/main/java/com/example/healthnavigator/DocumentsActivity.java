package com.example.healthnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentsActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            logout();
            return;
        }

        tvDocuments = findViewById(R.id.tvDocuments);

        loadDocuments();
    }

    private void loadDocuments() {

        String auth = sessionManager.getAuthHeader();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<ApiModels.DocumentResponse>> call = apiService.getDocuments(auth);

        call.enqueue(new Callback<List<ApiModels.DocumentResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ApiModels.DocumentResponse>> call,
                    Response<List<ApiModels.DocumentResponse>> response
            ) {

                if (response.code() == 401) {
                    logout();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {

                    List<ApiModels.DocumentResponse> documents = response.body();

                    if (documents.isEmpty()) {
                        tvDocuments.setText("No documents found.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();

                    sb.append("Medical Document Wallet\n\n");

                    for (ApiModels.DocumentResponse document : documents) {

                        sb.append("Title: ").append(document.title).append("\n");
                        sb.append("Type: ").append(document.document_type).append("\n");
                        sb.append("Uploaded At: ").append(document.uploaded_at).append("\n\n");
                    }

                    tvDocuments.setText(sb.toString());

                } else {
                    tvDocuments.setText("Could not load documents.");
                }
            }

            @Override
            public void onFailure(
                    Call<List<ApiModels.DocumentResponse>> call,
                    Throwable t
            ) {
                tvDocuments.setText("Error: " + t.getMessage());
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