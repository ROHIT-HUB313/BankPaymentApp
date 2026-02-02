package com.bank.payment.android.model;

/**
 * Request body for creating a bank account
 */
public class CreateAccountRequest {
    private String accountType; // SAVINGS, CURRENT
    private String branchCode; // e.g., "MUM001", "DEL001"

    public CreateAccountRequest(String accountType, String branchCode) {
        this.accountType = accountType;
        this.branchCode = branchCode;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getBranchCode() {
        return branchCode;
    }
}
