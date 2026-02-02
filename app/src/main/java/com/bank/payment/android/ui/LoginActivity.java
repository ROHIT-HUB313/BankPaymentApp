package com.bank.payment.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.payment.android.databinding.ActivityLoginBinding;
import com.bank.payment.android.model.AuthRequest;
import com.bank.payment.android.model.AuthResponse;
import com.bank.payment.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login Screen - Authenticates user and stores JWT token
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);

        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            binding.etUsername.setError("Username required");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Password required");
            return;
        }

        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        AuthRequest request = new AuthRequest(username, password);

        retrofitClient.getApiService().login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Save both access token and refresh token
                    retrofitClient.saveToken(response.body().getToken());
                    if (response.body().getRefreshToken() != null) {
                        retrofitClient.saveRefreshToken(response.body().getRefreshToken());
                    }

                    // Navigate to Dashboard
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
