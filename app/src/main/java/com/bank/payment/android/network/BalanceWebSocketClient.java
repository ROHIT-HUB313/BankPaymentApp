package com.bank.payment.android.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * WebSocket client for real-time balance updates.
 * Connects to: ws://host:8082/ws/balance/{accountNumber}
 * 
 * Note: WebSocket connects directly to bank-engine (8082), not via API Gateway.
 */
public class BalanceWebSocketClient {

    private static final String TAG = "BalanceWebSocket";
    private static final int NORMAL_CLOSE = 1000;

    private WebSocket webSocket;
    private OkHttpClient client;
    private String accountNumber;
    private BalanceUpdateListener listener;
    private boolean isConnected = false;
    private Handler mainHandler;

    public interface BalanceUpdateListener {
        void onBalanceUpdate(double newBalance, String transactionType);

        void onConnectionStateChanged(boolean connected);

        void onError(String message);
    }

    public BalanceWebSocketClient(String accountNumber, BalanceUpdateListener listener) {
        this.accountNumber = accountNumber;
        this.listener = listener;
        this.mainHandler = new Handler(Looper.getMainLooper());

        this.client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS) // No timeout for WebSocket
                .build();
    }

    /**
     * Connect to the WebSocket server.
     * Uses same URL configuration as REST API but connects to bank-engine directly.
     */
    public void connect() {
        String wsUrl = ApiConfig.WEBSOCKET_URL + "/ws/balance/" + accountNumber;
        Log.i(TAG, "Connecting to WebSocket: " + wsUrl);

        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i(TAG, "WebSocket connected for account: " + accountNumber);
                isConnected = true;
                mainHandler.post(() -> listener.onConnectionStateChanged(true));
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                parseAndNotify(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.i(TAG, "WebSocket closing: " + reason);
                webSocket.close(NORMAL_CLOSE, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.i(TAG, "WebSocket closed: " + reason);
                isConnected = false;
                mainHandler.post(() -> listener.onConnectionStateChanged(false));
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage(), t);
                isConnected = false;
                mainHandler.post(() -> {
                    listener.onConnectionStateChanged(false);
                    listener.onError("WebSocket connection failed: " + t.getMessage());
                });
            }
        });
    }

    private void parseAndNotify(String json) {
        try {
            // Simple JSON parsing (avoiding external dependency)
            if (json.contains("BALANCE_UPDATE")) {
                // Extract balance value
                int balanceStart = json.indexOf("\"balance\":") + 10;
                int balanceEnd = json.indexOf(",", balanceStart);
                if (balanceEnd == -1)
                    balanceEnd = json.indexOf("}", balanceStart);
                double balance = Double.parseDouble(json.substring(balanceStart, balanceEnd));

                // Extract transaction type
                int typeStart = json.indexOf("\"transactionType\":\"") + 19;
                int typeEnd = json.indexOf("\"", typeStart);
                String transactionType = json.substring(typeStart, typeEnd);

                mainHandler.post(() -> listener.onBalanceUpdate(balance, transactionType));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse message: " + json, e);
        }
    }

    /**
     * Send a ping to keep connection alive.
     */
    public void sendPing() {
        if (webSocket != null && isConnected) {
            webSocket.send("PING");
        }
    }

    /**
     * Disconnect from WebSocket server.
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(NORMAL_CLOSE, "User disconnected");
            isConnected = false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
