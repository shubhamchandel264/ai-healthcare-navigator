package com.example.healthnavigator;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/register")
    Call<ApiModels.MessageResponse> register(
            @Body ApiModels.RegisterRequest request
    );

    @POST("api/auth/login")
    Call<ApiModels.LoginResponse> login(
            @Body ApiModels.LoginRequest request
    );

    @GET("api/dashboard")
    Call<ApiModels.DashboardResponse> getDashboard(
            @Header("Authorization") String token
    );

    @POST("api/prescriptions/simplify")
    Call<ApiModels.PrescriptionResponse> simplifyPrescription(
            @Header("Authorization") String token,
            @Body ApiModels.PrescriptionRequest request
    );

    @POST("api/reports/explain")
    Call<ApiModels.ReportResponse> explainReport(
            @Header("Authorization") String token,
            @Body ApiModels.ReportRequest request
    );

    @POST("api/symptoms/specialist")
    Call<ApiModels.SymptomResponse> getSpecialist(
            @Header("Authorization") String token,
            @Body ApiModels.SymptomRequest request
    );

    @GET("api/hospitals")
    Call<List<ApiModels.Hospital>> getHospitals(
            @Header("Authorization") String token,
            @Query("city") String city,
            @Query("specialty") String specialty,
            @Query("max_fee") Double maxFee
    );

    @GET("api/cost-estimator/{procedure}")
    Call<ApiModels.CostEstimate> getCostEstimate(
            @Header("Authorization") String token,
            @Path("procedure") String procedure
    );

    @POST("api/insurance/explain")
    Call<ApiModels.InsuranceResponse> explainInsurance(
            @Header("Authorization") String token,
            @Body ApiModels.InsuranceRequest request
    );

    @GET("api/medicines/interactions")
    Call<ApiModels.InteractionResponse> checkMedicineInteractions(
            @Header("Authorization") String token,
            @Query("medicines") String medicines
    );

    @POST("api/expenses")
    Call<ApiModels.ExpenseResponse> addExpense(
            @Header("Authorization") String token,
            @Body ApiModels.ExpenseRequest request
    );

    @GET("api/expenses")
    Call<List<ApiModels.ExpenseResponse>> getExpenses(
            @Header("Authorization") String token
    );

    @GET("api/documents")
    Call<List<ApiModels.DocumentResponse>> getDocuments(
            @Header("Authorization") String token
    );
}