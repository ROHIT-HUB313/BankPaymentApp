package com.bank.payment.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.payment.android.databinding.ActivityRegisterBinding;
import com.bank.payment.android.model.RegisterRequest;
import com.bank.payment.android.model.UserResponse;
import com.bank.payment.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Registration Screen - Creates new user account
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);

        binding.btnRegister.setOnClickListener(v -> performRegistration());

        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);

        RegisterRequest request = new RegisterRequest(username, password, email, phone, address);

        retrofitClient.getApiService().register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG)
                            .show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Try a different username.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
