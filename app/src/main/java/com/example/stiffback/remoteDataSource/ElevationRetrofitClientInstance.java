package com.example.stiffback.remoteDataSource;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ElevationRetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://nationalmap.gov";

    public static Retrofit getRetrofitInstance() {
        // set custom timeouts
        int TIMEOUT = 30;
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        client.readTimeout(TIMEOUT,TimeUnit.SECONDS);
        client.writeTimeout(TIMEOUT,TimeUnit.SECONDS);



        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}