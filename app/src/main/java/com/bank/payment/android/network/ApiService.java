package com.bank.payment.android.network;

import com.bank.payment.android.model.AccountResponse;
import com.bank.payment.android.model.AuthRequest;
import com.bank.payment.android.model.AuthResponse;
import com.bank.payment.android.model.CreateAccountRequest;
import com.bank.payment.android.model.DepositRequest;
import com.bank.payment.android.model.RegisterRequest;
import com.bank.payment.android.model.TransactionResponse;
import com.bank.payment.android.model.TransferRequest;
import com.bank.payment.android.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit API Interface - Defines all backend endpoints
 */
public interface ApiService {

    // ========== USER ENGINE ==========

    @POST("users/public/register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @POST("users/public/token")
    Call<AuthResponse> login(@Body AuthRequest request);

    // ========== BANK ENGINE ==========

    @POST("accounts/public/create")
    Call<AccountResponse> createAccount(@Body CreateAccountRequest request);

    @GET("accounts/public/user")
    Call<AccountResponse> getAccount();

    // ========== TRANSACTION ENGINE ==========

    @POST("transactions/public/transfer")
    Call<TransactionResponse> transfer(@Body TransferRequest request);

    @POST("transactions/public/deposit")
    Call<TransactionResponse> deposit(@Body DepositRequest request);

    @POST("transactions/public/withdraw")
    Call<TransactionResponse> withdraw(@Body DepositRequest request);

    @GET("transactions/public/history")
    Call<List<TransactionResponse>> getTransactionHistory(@Query("accountNo") String accountNumber);
}
