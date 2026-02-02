package com.bank.payment.android.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit Client with JWT Authentication Interceptor
 */
public class RetrofitClient {

    private static final String PREFS_NAME = "bank_payment_secure_prefs";
    private static final String TOKEN_KEY = "jwt_token";

    private static RetrofitClient instance;
    private final ApiService apiService;
    private final SharedPreferences securePrefs;

    private RetrofitClient(Context context) {
        // Initialize Encrypted SharedPreferences for secure token storage
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to create encrypted prefs", e);
        }

        // Logging Interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Auth Interceptor - Adds JWT token to all requests
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            String token = getToken();

            if (token != null && !token.isEmpty()) {
                Request authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(authenticatedRequest);
            }

            return chain.proceed(originalRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext());
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    // Token Management
    public void saveToken(String token) {
        securePrefs.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getToken() {
        return securePrefs.getString(TOKEN_KEY, null);
    }

    public void clearToken() {
        securePrefs.edit().remove(TOKEN_KEY).apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    // Refresh Token Management
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    public void saveRefreshToken(String refreshToken) {
        securePrefs.edit().putString(REFRESH_TOKEN_KEY, refreshToken).apply();
    }

    public String getRefreshToken() {
        return securePrefs.getString(REFRESH_TOKEN_KEY, null);
    }

    public void clearAllTokens() {
        securePrefs.edit()
                .remove(TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .apply();
    }
}
