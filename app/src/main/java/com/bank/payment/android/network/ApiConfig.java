package com.bank.payment.android.network;

/**
 * API Configuration Constants
 * Update BASE_URL based on your testing environment:
 * - Emulator: "http://10.0.2.2:8080/"
 * - Physical Device: "http://<YOUR_PC_IP>:8080/"
 */
public class ApiConfig {
    // For Android Emulator - 10.0.2.2 maps to host machine's localhost
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:8080/";

    // For Physical Device - Replace with your PC's local IP (e.g., 192.168.1.100)
    public static final String BASE_URL_DEVICE = "http://192.168.1.1:8080/";

    // Active Base URL - Change this based on your testing environment
    public static final String BASE_URL = BASE_URL_EMULATOR;
}
