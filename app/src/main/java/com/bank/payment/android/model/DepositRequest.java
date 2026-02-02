package com.bank.payment.android.model;

import java.math.BigDecimal;

/**
 * Request body for deposit/withdraw operations
 */
public class DepositRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String idempotencyKey;

    public DepositRequest(String accountNumber, BigDecimal amount, String idempotencyKey) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
