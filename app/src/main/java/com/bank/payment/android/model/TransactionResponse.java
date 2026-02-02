package com.bank.payment.android.model;

import java.math.BigDecimal;

/**
 * Response from transaction operations
 */
public class TransactionResponse {
    private Long id;
    private String utr;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType; // CREDIT, DEBIT
    private String status; // SUCCESS, FAILED, INITIATED
    private String idempotencyKey;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private String timestamp;

    // Getters
    public Long getId() {
        return id;
    }

    public String getUtr() {
        return utr;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Helper for display
    public String getFormattedAmount() {
        String sign = "CREDIT".equals(transactionType) ? "+ ₹" : "- ₹";
        return sign + (amount != null ? amount.toString() : "0.00");
    }

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
}
