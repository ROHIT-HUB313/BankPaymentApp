package com.bank.payment.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.payment.android.databinding.ActivityDepositBinding;
import com.bank.payment.android.model.DepositRequest;
import com.bank.payment.android.model.TransactionResponse;
import com.bank.payment.android.network.RetrofitClient;

import java.math.BigDecimal;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Deposit Screen - Add money to account
 */
public class DepositActivity extends AppCompatActivity {

    private ActivityDepositBinding binding;
    private RetrofitClient retrofitClient;
    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDepositBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);
        accountNumber = getIntent().getStringExtra("accountNumber");

        binding.tvAccount.setText("Account: " + accountNumber);
        binding.btnDeposit.setOnClickListener(v -> performDeposit());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void performDeposit() {
        String amountStr = binding.etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            binding.etAmount.setError("Enter amount");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                binding.etAmount.setError("Amount must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            binding.etAmount.setError("Invalid amount");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnDeposit.setEnabled(false);

        String idempotencyKey = UUID.randomUUID().toString();
        DepositRequest request = new DepositRequest(accountNumber, amount, idempotencyKey);

        retrofitClient.getApiService().deposit(request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnDeposit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(DepositActivity.this,
                            "Deposit successful! New Balance: ₹" + response.body().getClosingBalance(),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DepositActivity.this, "Deposit failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnDeposit.setEnabled(true);
                Toast.makeText(DepositActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
