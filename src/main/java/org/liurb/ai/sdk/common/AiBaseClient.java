package org.liurb.ai.sdk.common;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public abstract class AiBaseClient {

    private static final ConnectionPool sharedConnectionPool = new ConnectionPool(32, 60, TimeUnit.SECONDS);

    private OkHttpClient defaultClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectionPool(sharedConnectionPool)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .build();
    }

    protected OkHttpClient getDefaultClient() {
        return this.defaultClient();
    }
}
