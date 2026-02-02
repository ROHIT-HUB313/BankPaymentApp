package com.bank.payment.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.payment.android.R;
import com.bank.payment.android.databinding.ActivityHistoryBinding;
import com.bank.payment.android.model.TransactionResponse;
import com.bank.payment.android.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Transaction History Screen - Shows list of past transactions
 */
public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private RetrofitClient retrofitClient;
    private String accountNumber;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitClient = RetrofitClient.getInstance(this);
        accountNumber = getIntent().getStringExtra("accountNumber");

        setupRecyclerView();
        binding.btnBack.setOnClickListener(v -> finish());
        loadHistory();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        binding.progressBar.setVisibility(View.VISIBLE);

        retrofitClient.getApiService().getTransactionHistory(accountNumber)
                .enqueue(new Callback<List<TransactionResponse>>() {
                    @Override
                    public void onResponse(Call<List<TransactionResponse>> call,
                            Response<List<TransactionResponse>> response) {
                        binding.progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<TransactionResponse> transactions = response.body();
                            if (transactions.isEmpty()) {
                                binding.tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                adapter.setTransactions(transactions);
                            }
                        } else {
                            Toast.makeText(HistoryActivity.this, "Failed to load history",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TransactionResponse>> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(HistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Inner Adapter Class
    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

        private List<TransactionResponse> transactions = new ArrayList<>();

        void setTransactions(List<TransactionResponse> list) {
            this.transactions = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TransactionResponse tx = transactions.get(position);
            holder.tvType.setText(tx.getTransactionType());
            holder.tvAmount.setText(tx.getFormattedAmount());
            holder.tvStatus.setText(tx.getStatus());
            holder.tvDate.setText(tx.getTimestamp());
            holder.tvUtr.setText("UTR: " + tx.getUtr());

            // Color based on type
            int color = "CREDIT".equals(tx.getTransactionType()) ? android.graphics.Color.parseColor("#4CAF50")
                    : android.graphics.Color.parseColor("#F44336");
            holder.tvAmount.setTextColor(color);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvType, tvAmount, tvStatus, tvDate, tvUtr;

            ViewHolder(View view) {
                super(view);
                tvType = view.findViewById(R.id.tvType);
                tvAmount = view.findViewById(R.id.tvAmount);
                tvStatus = view.findViewById(R.id.tvStatus);
                tvDate = view.findViewById(R.id.tvDate);
                tvUtr = view.findViewById(R.id.tvUtr);
            }
        }
    }
}
