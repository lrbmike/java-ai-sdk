package org.liurb.ai.sdk.gemini;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.gemini.bean.*;
import org.liurb.ai.sdk.gemini.conf.GeminiAccount;
import org.liurb.ai.sdk.gemini.dto.GeminiTextRequest;
import org.liurb.ai.sdk.gemini.dto.GeminiTextResponse;
import org.liurb.ai.sdk.gemini.enums.GeminiModelEnum;
import org.liurb.ai.sdk.gemini.listener.GeminiStreamResponseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GeminiClient {

    private GeminiAccount geminiAccount;
    private String BASE_URL = "https://generativelanguage.googleapis.com";
    private String MODEL_NAME = GeminiModelEnum.GEMINI_PRO_FLASH.getName();
    private OkHttpClient okHttpClient;

    private GeminiClient(){}

    public GeminiClient(GeminiAccount geminiAccount) {
        this.geminiAccount = geminiAccount;
        this.okHttpClient = this.defaultClient();
    }

    public GeminiClient(String modelName, GeminiAccount geminiAccount) {
        this.geminiAccount = geminiAccount;
        this.okHttpClient = this.defaultClient();
        this.MODEL_NAME = modelName;
    }

    public GeminiClient(GeminiAccount geminiAccount, OkHttpClient okHttpClient) {
        this.geminiAccount = geminiAccount;
        this.okHttpClient = okHttpClient;
    }

    public GeminiClient(String modelName, GeminiAccount geminiAccount, OkHttpClient okHttpClient) {
        this.geminiAccount = geminiAccount;
        this.okHttpClient = okHttpClient;
        this.MODEL_NAME = modelName;
    }

    public GeminiTextResponse chat(String message) throws IOException {

        return this.chat(message, null, null, null);
    }

    public GeminiTextResponse chat(String message, GeminiGenerationConfig generationConfig) throws IOException {

        return this.chat(message, null, generationConfig, null);
    }

    public GeminiTextResponse chat(String message, List<ChatHistory> history) throws IOException {

        return this.chat(message, null, null, history);
    }

    public GeminiTextResponse chat(String message, GeminiGenerationConfig generationConfig, List<ChatHistory> history) throws IOException {

        return this.chat(message, null, generationConfig, history);
    }

    public GeminiTextResponse chat(String message, MultiPartInlineData inlineData) throws IOException {

        return this.chat(message, inlineData, null, null);
    }

    public GeminiTextResponse chat(String message, MultiPartInlineData inlineData, GeminiGenerationConfig generationConfig, List<ChatHistory> history) throws IOException {

        if (this.geminiAccount == null || this.geminiAccount.getApiKey() == null || this.geminiAccount.getApiKey().isEmpty()) {
            throw new RuntimeException("gemini api key is empty");
        }

        if (this.geminiAccount.getBaseUrl() != null && !this.geminiAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.geminiAccount.getBaseUrl();
        }

        //build gemini request body
        GeminiTextRequest questParams = this.buildGeminiTextRequest(message, inlineData, history);

        if (generationConfig != null) {
            questParams.setGenerationConfig(generationConfig);
        }

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        String url = "{base_url}/v1/{model}:generateContent?key={api_key}";
        url = url.replace("{base_url}", this.BASE_URL)
                .replace("{api_key}", this.geminiAccount.getApiKey())
                .replace("{model}", this.MODEL_NAME);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = this.okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            GeminiTextResponse textResponse = JSON.parseObject(responseBody, GeminiTextResponse.class);
            // handle and set history
            textResponse.setHistory(this.buildChatHistory(message, inlineData, textResponse.getCandidates(), history));

            return textResponse;
        }

        return null;
    }

    public void stream(String message, GeminiStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, null, null, responseListener);
    }

    public void stream(String message, GeminiGenerationConfig generationConfig, GeminiStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, generationConfig, null, responseListener);
    }

    public void stream(String message, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, null, history, responseListener);
    }

    public void stream(String message, GeminiGenerationConfig generationConfig, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, generationConfig, history, responseListener);
    }

    public void stream(String message, MultiPartInlineData inlineData, GeminiStreamResponseListener responseListener) throws IOException {

        this.stream(message, inlineData, null, null, responseListener);
    }

    public void stream(String message, MultiPartInlineData inlineData, GeminiGenerationConfig generationConfig, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {

        if (this.geminiAccount == null || this.geminiAccount.getApiKey() == null || this.geminiAccount.getApiKey().isEmpty()) {
            throw new RuntimeException("gemini api key is empty");
        }

        if (this.geminiAccount.getBaseUrl() != null && !this.geminiAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.geminiAccount.getBaseUrl();
        }

        //build gemini request body
        GeminiTextRequest questParams = this.buildGeminiTextRequest(message, inlineData, history);

        if (generationConfig != null) {
            questParams.setGenerationConfig(generationConfig);
        }

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        // Be sure to set alt=sse in the URL parameters
        String url = "{base_url}/v1/{model}:streamGenerateContent?key={api_key}&alt=sse";
        url = url.replace("{base_url}", this.BASE_URL)
                .replace("{api_key}", this.geminiAccount.getApiKey())
                .replace("{model}", this.MODEL_NAME);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

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
                            if (line.startsWith("data: ")) {
                                line = line.substring("data: ".length());
                                GeminiTextResponse streamResponse = JSON.parseObject(line, GeminiTextResponse.class);

                                Content content = streamResponse.getCandidates().get(0).getContent();

                                textSb.append(content.getParts().get(0).getText());

                                responseListener.accept(content);
                            }
                        }

                        //handle history
                        buildStreamChatHistory(message, inlineData, textSb, history);

                    }
                }
            }
        });

    }

    private GeminiTextRequest buildGeminiTextRequest(String message, MultiPartInlineData inlineData, List<ChatHistory> history) {

        List<ChatTextMessage> contents = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (ChatHistory chat : history) {

                if (chat instanceof GeminiChatMultiHistory) {
                    GeminiChatMultiHistory geminiChatMultiHistory = (GeminiChatMultiHistory)chat;
                    ChatTextMessage chatTextMessage = this.buildChatTextMessage(geminiChatMultiHistory.getContent(), geminiChatMultiHistory.getRole(), geminiChatMultiHistory.getInlineData());
                    contents.add(chatTextMessage);
                }else {
                    ChatTextMessage textMessage = this.buildChatTextMessage(chat.getContent(), chat.getRole());
                    contents.add(textMessage);
                }

            }

        }

        // user message part
        ChatTextMessage textMessage = this.buildChatTextMessage(message, "user", inlineData);

        contents.add(textMessage);

        return GeminiTextRequest.builder().contents(contents).build();
    }

    private ChatTextMessage buildChatTextMessage(String text, String role) {

        return this.buildChatTextMessage(text, role, null);
    }

    private ChatTextMessage buildChatTextMessage(String text, String role, MultiPartInlineData inlineData) {

        List<TextPart> parts = new ArrayList<>();

        TextPart textPart = TextPart.builder().text(text).build();
        parts.add(textPart);

        if (inlineData != null) {
            MediaPart mediaPart = MediaPart.builder().inlineData(inlineData).build();
            parts.add(mediaPart);
        }

        return ChatTextMessage.builder().role(role).parts(parts).build();
    }

    private List<ChatHistory> buildChatHistory(String message, MultiPartInlineData inlineData, List<Candidate> candidates, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (inlineData != null) {
            GeminiChatMultiHistory geminiChatMultiHistory = GeminiChatMultiHistory.builder().content(message).role("user").inlineData(inlineData).build();
            history.add(geminiChatMultiHistory);
        }else{
            ChatHistory chatHistory = ChatHistory.builder().content(message).role("user").build();
            history.add(chatHistory);
        }

        // add ai response message
        Content content = candidates.get(0).getContent();
        ChatHistory aiChat = ChatHistory.builder().content(content.getParts().get(0).getText()).role(content.getRole()).build();
        history.add(aiChat);

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

    private List<ChatHistory> buildStreamChatHistory(String message, MultiPartInlineData inlineData, StringBuffer aiText, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (inlineData != null) {
            GeminiChatMultiHistory geminiChatMultiHistory = GeminiChatMultiHistory.builder().content(message).role("user").inlineData(inlineData).build();
            history.add(geminiChatMultiHistory);
        }else{
            ChatHistory chatHistory = ChatHistory.builder().content(message).role("user").build();
            history.add(chatHistory);
        }

        if (aiText.length() != 0) {
            ChatHistory aiChat = ChatHistory.builder().content(aiText.toString()).role("model").build();
            history.add(aiChat);
        }

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

    private static final ConnectionPool sharedConnectionPool = new ConnectionPool(32, 60, TimeUnit.SECONDS);

    private OkHttpClient defaultClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectionPool(sharedConnectionPool)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
    }

}
