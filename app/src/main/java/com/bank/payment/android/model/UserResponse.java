package com.bank.payment.android.model;

/**
 * Response from registration endpoint
 */
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private boolean kycVerified;

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public boolean isKycVerified() {
        return kycVerified;
    }
}
