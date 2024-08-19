package org.liurb.ai.sdk.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.MediaData;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.dto.AiChatResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AiBaseClient {

    private ModelAccount account;
    private String MODEL_NAME;
    private OkHttpClient okHttpClient;
    
    private AiBaseClient() {}

    public AiBaseClient(ModelAccount account) {
        this.MODEL_NAME = this.getDefaultModelName();
        this.account = account;
        this.okHttpClient = this.getDefaultClient();
    }

    public AiBaseClient(String modelName, ModelAccount account) {
        this.MODEL_NAME = modelName;
        this.account = account;
        this.okHttpClient = this.getDefaultClient();
    }

    public AiBaseClient(String modelName, ModelAccount account, OkHttpClient okHttpClient) {
        this.MODEL_NAME = modelName;
        this.account = account;
        this.okHttpClient = okHttpClient;
    }

    public AiChatResponse chat(String message, GenerationConfig generationConfig) throws IOException {

        return this.chat(message, null, generationConfig, null);
    }

    public AiChatResponse chat(String message, GenerationConfig generationConfig, List<ChatHistory> history) throws IOException {

        return this.chat(message, null, generationConfig, history);
    }

    public AiChatResponse chat(String message, MediaData mediaData, GenerationConfig generationConfig, List<ChatHistory> history) throws IOException {
        String baseUrl = this.getDefaultBaseUrl();
        if (this.getAccount() != null && this.getAccount().getBaseUrl() != null && !this.getAccount().getBaseUrl().isEmpty()) {
            baseUrl = this.getAccount().getBaseUrl();
        }

        JSONObject chatRequestParams = this.buildChatRequest(message, mediaData, generationConfig, history);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(chatRequestParams));

        String url = baseUrl + this.getApi();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.getAccount().getApiKey())
                .post(requestBody)
                .build();

        Response response = this.okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            return this.buildChatResponse(responseBody, message, mediaData, history);
        }

        return null;
    }
    
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

    protected ModelAccount getAccount() {
        return this.account;
    }

    protected String getModelName() {
        return this.MODEL_NAME;
    }

    /**
     * 子类提供模型名称
     *
     * @return
     */
    protected abstract String getDefaultModelName();

    /**
     * 子类提供base url
     *
     * @return
     */
    protected abstract String getDefaultBaseUrl();

    /**
     * 子类提供api地址
     *
     * 如:/api/chat
     *
     * @return
     */
    protected abstract String getApi();

    /**
     * 子类构建请求参数
     *
     * @return
     */
    protected abstract JSONObject buildChatRequest(String message, MediaData mediaData, GenerationConfig generationConfig, List<ChatHistory> history);

    /**
     * 子类构建返回内容
     *
     * @param responseBody
     * @param message
     * @param mediaData
     * @param history
     * @return
     */
    protected abstract AiChatResponse buildChatResponse(String responseBody, String message, MediaData mediaData, List<ChatHistory> history);
}
