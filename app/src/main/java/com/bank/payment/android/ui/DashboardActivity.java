package com.bank.payment.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.payment.android.databinding.ActivityDashboardBinding;
import com.bank.payment.android.model.AccountResponse;
import com.bank.payment.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dashboard - Main screen showing account balance and quick actions
 */
public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private RetrofitClient retrofitClient;
    private AccountResponse currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccountData();
    }

    private void setupClickListeners() {
        binding.btnTransfer.setOnClickListener(v -> {
            if (currentAccount != null) {
                Intent intent = new Intent(this, TransferActivity.class);
                intent.putExtra("accountNumber", currentAccount.getAccountNumber());
                startActivity(intent);
            }
        });

        binding.btnDeposit.setOnClickListener(v -> {
            if (currentAccount != null) {
                Intent intent = new Intent(this, DepositActivity.class);
                intent.putExtra("accountNumber", currentAccount.getAccountNumber());
                startActivity(intent);
            }
        });

        binding.btnHistory.setOnClickListener(v -> {
            if (currentAccount != null) {
                Intent intent = new Intent(this, HistoryActivity.class);
                intent.putExtra("accountNumber", currentAccount.getAccountNumber());
                startActivity(intent);
            }
        });

        binding.btnLogout.setOnClickListener(v -> logout());

        binding.swipeRefresh.setOnRefreshListener(this::loadAccountData);
    }

    private void loadAccountData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        retrofitClient.getApiService().getAccount().enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentAccount = response.body();
                    updateUI();
                } else if (response.code() == 404) {
                    // No account yet - show create account prompt
                    binding.cardBalance.setVisibility(View.GONE);
                    binding.layoutNoAccount.setVisibility(View.VISIBLE);
                    binding.btnCreateAccount.setOnClickListener(v -> createAccount());
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(DashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        binding.cardBalance.setVisibility(View.VISIBLE);
        binding.layoutNoAccount.setVisibility(View.GONE);

        binding.tvBalance.setText(currentAccount.getFormattedBalance());
        binding.tvAccountNumber.setText("A/C: " + currentAccount.getAccountNumber());
        binding.tvAccountType.setText(currentAccount.getAccountType() + " Account");
        binding.tvStatus.setText(currentAccount.getAccountStatus());
    }

    private void createAccount() {
        // For simplicity, creating SAVINGS account at Mumbai branch
        com.bank.payment.android.model.CreateAccountRequest request = new com.bank.payment.android.model.CreateAccountRequest(
                "SAVINGS", "MUM001");

        binding.progressBar.setVisibility(View.VISIBLE);

        retrofitClient.getApiService().createAccount(request).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DashboardActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                    loadAccountData();
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        retrofitClient.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
