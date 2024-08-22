package org.liurb.ai.sdk.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.common.listener.AiStreamResponseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AiBaseClient {

    private ModelAccount account;
    private String MODEL_NAME;
    private OkHttpClient okHttpClient;
    private boolean stream;

    
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
        this.stream = false;

        Request request = this.buildHttpRequest(message, mediaData, generationConfig, false, history);

        Response response = this.okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            return this.buildChatResponse(responseBody, message, mediaData, history);
        }

        return null;
    }

    public void stream(String message, GenerationConfig generationConfig, AiStreamResponseListener responseListener) throws IOException {
        this.stream(message, null, generationConfig, null, responseListener);
    }

    public void stream(String message, GenerationConfig generationConfig, List<ChatHistory> history, AiStreamResponseListener responseListener) throws IOException {
        this.stream(message, null, generationConfig, history, responseListener);
    }

    public void stream(String message, MediaData mediaData, GenerationConfig generationConfig, List<ChatHistory> history, AiStreamResponseListener responseListener) throws IOException {
        this.stream = true;

        Request request = this.buildHttpRequest(message, mediaData, generationConfig, true, history);

        this.okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {

                        StringBuffer textSb = new StringBuffer();

                        InputStream inputStream = responseBody.byteStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            log.debug(line);

                            AiStreamMessage streamMessage = buildStreamMessage(line);
                            if (streamMessage != null) {
                                if (streamMessage.isStop()) {
                                    break;
                                }
                                //append response message content
                                textSb.append(streamMessage.getContent());
                                //callback
                                responseListener.accept(streamMessage);
                            }
                        }

                        //handle history
                        buildStreamChatHistory(message, mediaData, textSb.toString(), history);

                    } finally {
                        //finish callback
                        responseListener.accept(AiStreamMessage.builder().stop(true).build());
                    }
                }
            }
        });

    }

    private Request buildHttpRequest(String message, MediaData mediaData, GenerationConfig generationConfig, boolean stream, List<ChatHistory> history) {
        String baseUrl = this.getDefaultBaseUrl();
        if (this.getAccount() != null && this.getAccount().getBaseUrl() != null && !this.getAccount().getBaseUrl().isEmpty()) {
            baseUrl = this.getAccount().getBaseUrl();
        }

        JSONObject chatRequestParams = this.buildChatRequest(message, mediaData, generationConfig, stream, history);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(chatRequestParams));

        String url = baseUrl + this.getApi();

        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.getAccount().getApiKey())
                .post(requestBody)
                .build();
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

    protected boolean getStreaming() {
        return this.stream;
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
     * @param message
     * @param mediaData
     * @param generationConfig
     * @param stream
     * @param history
     * @return
     */
    protected abstract JSONObject buildChatRequest(String message, MediaData mediaData, GenerationConfig generationConfig, boolean stream, List<ChatHistory> history);

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

    /**
     * 子类构建 stream 模式下的消息内容
     *
     * @param responseLine
     * @return
     */
    protected abstract AiStreamMessage buildStreamMessage(String responseLine);

    /**
     * 子类构建 steam 模式下的历史记录
     *
     * @param message
     * @param mediaData
     * @param aiMessage
     * @param history
     */
    protected abstract void buildStreamChatHistory(String message, MediaData mediaData, String aiMessage, List<ChatHistory> history);
}
