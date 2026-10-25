package com.example.healthnavigator;

import java.util.List;

public class ApiModels {

    public static class MessageResponse {
        public String message;
        public String detail;
    }

    public static class RegisterRequest {
        public String full_name;
        public String email;
        public String password;
        public String phone;

        public RegisterRequest(String full_name, String email, String password, String phone) {
            this.full_name = full_name;
            this.email = email;
            this.password = password;
            this.phone = phone;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String access_token;
        public String token_type;
    }

    public static class DashboardResponse {
        public int upcoming_appointments;
        public int active_medicines;
        public int total_prescriptions;
        public float monthly_expense;
    }

    public static class PrescriptionRequest {
        public String prescription_text;

        public PrescriptionRequest(String prescription_text) {
            this.prescription_text = prescription_text;
        }
    }

    public static class PrescriptionResponse {
        public int id;
        public String original_text;
        public String extracted_text;
        public String simplified_explanation;
    }

    public static class ReportRequest {
        public String report_type;
        public String report_text;

        public ReportRequest(String report_type, String report_text) {
            this.report_type = report_type;
            this.report_text = report_text;
        }
    }

    public static class ReportResponse {
        public int id;
        public String report_type;
        public String report_text;
        public String explanation;
    }

    public static class SymptomRequest {
        public String symptom;

        public SymptomRequest(String symptom) {
            this.symptom = symptom;
        }
    }

    public static class SymptomResponse {
        public String symptom;
        public String suggested_specialist;
        public String urgency;
        public String disclaimer;
    }

    public static class Hospital {
        public int id;
        public String name;
        public String address;
        public String city;
        public double latitude;
        public double longitude;
        public String specialties;
        public double consultation_fee;
        public String insurance_accepted;
        public double rating;
        public int emergency_services;
        public String phone;
    }

    public static class CostEstimate {
        public int id;
        public String procedure_name;
        public double hospital_charges;
        public double medicine;
        public double tests;
        public double doctor_fees;
        public double total;
        public String currency;
        public String notes;
    }

    public static class InsuranceRequest {
        public String policy_text;

        public InsuranceRequest(String policy_text) {
            this.policy_text = policy_text;
        }
    }

    public static class InsuranceResponse {
        public String explanation;
    }

    public static class InteractionWarning {
        public String medicine_a;
        public String medicine_b;
        public String severity;
        public String description;
    }

    public static class InteractionResponse {
        public List<InteractionWarning> warnings;
        public String note;
        public String message;
    }

    public static class ExpenseRequest {
        public String category;
        public double amount;
        public String description;
        public String expense_date;

        public ExpenseRequest(String category, double amount, String description, String expense_date) {
            this.category = category;
            this.amount = amount;
            this.description = description;
            this.expense_date = expense_date;
        }
    }

    public static class ExpenseResponse {
        public int id;
        public String category;
        public double amount;
        public String description;
        public String expense_date;
    }

    public static class DocumentResponse {
        public int id;
        public String document_type;
        public String title;
        public String file_path;
        public String uploaded_at;
    }
}