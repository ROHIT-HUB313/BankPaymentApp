package com.bank.payment.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.payment.android.databinding.ActivityTransferBinding;
import com.bank.payment.android.model.TransactionResponse;
import com.bank.payment.android.model.TransferRequest;
import com.bank.payment.android.network.RetrofitClient;

import java.math.BigDecimal;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Transfer Screen - Send money to another account
 */
public class TransferActivity extends AppCompatActivity {

    private ActivityTransferBinding binding;
    private RetrofitClient retrofitClient;
    private String myAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);
        myAccountNumber = getIntent().getStringExtra("accountNumber");

        binding.tvFromAccount.setText("From: " + myAccountNumber);
        binding.btnTransfer.setOnClickListener(v -> performTransfer());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void performTransfer() {
        String toAccount = binding.etReceiverAccount.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();

        if (toAccount.isEmpty()) {
            binding.etReceiverAccount.setError("Enter receiver account");
            return;
        }
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
        binding.btnTransfer.setEnabled(false);

        // Generate unique idempotency key
        String idempotencyKey = UUID.randomUUID().toString();

        TransferRequest request = new TransferRequest(myAccountNumber, toAccount, amount, idempotencyKey);

        retrofitClient.getApiService().transfer(request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnTransfer.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    TransactionResponse tx = response.body();
                    if (tx.isSuccess()) {
                        Toast.makeText(TransferActivity.this,
                                "Transfer successful! UTR: " + tx.getUtr(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(TransferActivity.this,
                                "Transfer failed: " + tx.getStatus(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransferActivity.this, "Transfer failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnTransfer.setEnabled(true);
                Toast.makeText(TransferActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
