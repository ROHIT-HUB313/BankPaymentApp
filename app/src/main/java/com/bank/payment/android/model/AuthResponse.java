package com.bank.payment.android.model;

/**
 * Response from login endpoint containing JWT token
 */
public class AuthResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
