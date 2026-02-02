package com.bank.payment.android.model;

/**
 * Response from login endpoint containing JWT access token and refresh token.
 * - token: Access token (30 min validity)
 * - refreshToken: Refresh token (7 day validity)
 */
public class AuthResponse {
    private String token;
    private String refreshToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
