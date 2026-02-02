package com.bank.payment.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.Toast;

/**
 * Utility class for network-related operations and error handling.
 */
public class NetworkUtils {

    /**
     * Check if device has active internet connection.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    /**
     * Show network error toast based on error type.
     */
    public static void showNetworkError(Context context, Throwable t) {
        String message;
        if (t instanceof java.net.UnknownHostException) {
            message = "Cannot reach server. Check your connection.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            message = "Connection timed out. Try again.";
        } else if (t instanceof java.io.IOException) {
            message = "Network error. Please try again.";
        } else {
            message = "Error: " + t.getMessage();
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Extract error message from Retrofit response.
     */
    public static String getErrorMessage(retrofit2.Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (Exception ignored) {
        }

        return "Error: " + response.code() + " " + response.message();
    }
}
