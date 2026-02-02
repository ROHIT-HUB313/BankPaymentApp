package com.bank.payment.android.network;

/**
 * API Configuration constants.
 * Change BASE_URL based on your environment:
 * - Emulator: Use EMULATOR_URL (10.0.2.2 maps to localhost)
 * - Physical Device: Update DEVICE_URL with your PC's IP address
 */
public class ApiConfig {

    // For Android Emulator (10.0.2.2 is the emulator's localhost mapping)
    public static final String EMULATOR_URL = "http://10.0.2.2:8080";
    public static final String EMULATOR_WS_URL = "ws://10.0.2.2:8082";

    // For Physical Device - Update with your PC's local IP
    public static final String DEVICE_URL = "http://192.168.1.100:8080";
    public static final String DEVICE_WS_URL = "ws://192.168.1.100:8082";

    // Active URL - Switch between EMULATOR_URL and DEVICE_URL
    public static final String BASE_URL = EMULATOR_URL;

    // WebSocket URL - connects directly to bank-engine (not via gateway)
    public static final String WEBSOCKET_URL = EMULATOR_WS_URL;
}
