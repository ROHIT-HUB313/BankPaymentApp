package com.bank.payment.android.model;

/**
 * Request body for user registration
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;

    public RegisterRequest(String username, String password, String email, String phoneNumber, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
}
