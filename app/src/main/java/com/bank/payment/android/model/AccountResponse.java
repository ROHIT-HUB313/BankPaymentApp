package com.bank.payment.android.model;

import java.math.BigDecimal;

/**
 * Response from bank account operations
 */
public class AccountResponse {
    private Long bankId;
    private Long userId;
    private String accountNumber;
    private String accountType;
    private String accountStatus;
    private BigDecimal currentBalance;
    private String currency;
    private String ifscCode;
    private String address;
    private String createdBy;
    private String createdOn;

    // Getters
    public Long getBankId() {
        return bankId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getAddress() {
        return address;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    // Helper method for display
    public String getFormattedBalance() {
        return "₹ " + (currentBalance != null ? currentBalance.toString() : "0.00");
    }
}
