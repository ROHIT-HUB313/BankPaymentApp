package com.bank.payment.android.model;

import java.math.BigDecimal;

/**
 * Request body for transfers
 */
public class TransferRequest {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String idempotencyKey;

    public TransferRequest(String senderAccountNumber, String receiverAccountNumber,
            BigDecimal amount, String idempotencyKey) {
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    // Getters
    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
