package org.liurb.ai.sdk.ollama;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import org.liurb.ai.sdk.common.AiBaseClient;
import org.liurb.ai.sdk.ollama.bean.*;
import org.liurb.ai.sdk.ollama.conf.OllamaAccount;
import org.liurb.ai.sdk.ollama.dto.OllamaStreamResponse;
import org.liurb.ai.sdk.ollama.dto.OllamaTextRequest;
import org.liurb.ai.sdk.ollama.dto.OllamaTextResponse;
import org.liurb.ai.sdk.ollama.enums.OllamaModelEnum;
import org.liurb.ai.sdk.ollama.listener.OllamaStreamResponseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OllamaClient extends AiBaseClient {

    private OllamaAccount ollamaAccount;
    private String BASE_URL = "http://localhost:11434";
    private String MODEL_NAME = OllamaModelEnum.LLAMA3_1.getName();
    private OkHttpClient okHttpClient;

    private OllamaClient() {}

    public OllamaClient(OllamaAccount ollamaAccount) {
        this.ollamaAccount = ollamaAccount;
        this.okHttpClient = this.getDefaultClient();
    }

    public OllamaClient(String modelName, OllamaAccount ollamaAccount) {
        this.ollamaAccount = ollamaAccount;
        this.okHttpClient = this.getDefaultClient();
        this.MODEL_NAME = modelName;
    }

    public OllamaClient(OllamaAccount ollamaAccount, OkHttpClient okHttpClient) {
        this.ollamaAccount = ollamaAccount;
        this.okHttpClient = okHttpClient;
    }

    public OllamaClient(String modelName, OllamaAccount ollamaAccount, OkHttpClient okHttpClient) {
        this.ollamaAccount = ollamaAccount;
        this.okHttpClient = okHttpClient;
        this.MODEL_NAME = modelName;
    }

    public OllamaTextResponse chat(String message) throws IOException {

        return this.chat(message, null, null, null);
    }

    public OllamaTextResponse chat(String message, OllamaGenerationConfig generationConfig) throws IOException {

        return this.chat(message, null, generationConfig, null);
    }

    public OllamaTextResponse chat(String message, List<OllamaChatHistory> history) throws IOException {

        return this.chat(message, null, null, history);
    }

    public OllamaTextResponse chat(String message, OllamaGenerationConfig generationConfig, List<OllamaChatHistory> history) throws IOException {

        return this.chat(message, null, generationConfig, history);
    }

    public OllamaTextResponse chat(String message, String[] images) throws IOException {

        return this.chat(message, images, null, null);
    }

    public OllamaTextResponse chat(String message, String[] images, OllamaGenerationConfig generationConfig, List<OllamaChatHistory> history) throws IOException {


        if (this.ollamaAccount.getBaseUrl() != null && !this.ollamaAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.ollamaAccount.getBaseUrl();
        }

        OllamaTextRequest questParams = this.buildOllamaTextRequest(message, images, history);
        questParams.setStream(false);

        if (generationConfig != null) {
            OllamaRequestOptions options = OllamaRequestOptions.builder().temperature(generationConfig.getTemperature()).build();
            questParams.setOptions(options);
        }

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        String url = "{base_url}/api/chat";
        url = url.replace("{base_url}", this.BASE_URL);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.ollamaAccount.getApiKey())
                .post(requestBody)
                .build();

        Response response = this.okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            System.out.println(responseBody);

            OllamaTextResponse textResponse = JSON.parseObject(responseBody, OllamaTextResponse.class);
            textResponse.setHistory(this.buildChatHistory(message, images, textResponse.getMessage(), history));
            return textResponse;
        }

        return null;
    }

    public void stream(String message, OllamaStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, null, null, responseListener);
    }

    public void stream(String message, OllamaGenerationConfig generationConfig, OllamaStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, generationConfig, null, responseListener);
    }

    public void stream(String message, List<OllamaChatHistory> history, OllamaStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, null, history, responseListener);
    }

    public void stream(String message, OllamaGenerationConfig generationConfig, List<OllamaChatHistory> history, OllamaStreamResponseListener responseListener) throws IOException {

        this.stream(message, null, generationConfig, history, responseListener);
    }

    public void stream(String message, String[] images, OllamaGenerationConfig generationConfig, List<OllamaChatHistory> history, OllamaStreamResponseListener responseListener) throws IOException {

        if (this.ollamaAccount.getBaseUrl() != null && !this.ollamaAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.ollamaAccount.getBaseUrl();
        }

        OllamaTextRequest questParams = this.buildOllamaTextRequest(message, images, history);
        questParams.setStream(true);

        if (generationConfig != null) {
            OllamaRequestOptions options = OllamaRequestOptions.builder().temperature(generationConfig.getTemperature()).build();
            questParams.setOptions(options);
        }

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        String url = "{base_url}/api/chat";
        url = url.replace("{base_url}", this.BASE_URL);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.ollamaAccount.getApiKey())
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
//                            System.out.println(line);
                            OllamaStreamResponse streamResponse = JSON.parseObject(line, OllamaStreamResponse.class);

                            Boolean done = streamResponse.getDone();
                            if (done) {
                                break;
                            }

                            OllamaChatMessage streamMessage = streamResponse.getMessage();
                            textSb.append(streamMessage.getContent());

                            responseListener.accept(streamMessage);
                        }

                        //handle history
                        buildStreamChatHistory(message, images, textSb, history);

                    }
                }
            }
        });

    }

    private OllamaTextRequest buildOllamaTextRequest(String message, String[] images, List<OllamaChatHistory> history) {

        List<OllamaChatMessage> messages = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (OllamaChatHistory chat : history) {

                if (chat instanceof OllamaMultiChatHistory) {
                    OllamaMultiChatHistory multiChatHistory = (OllamaMultiChatHistory)chat;
                    OllamaChatMessage chatMessage = this.buildChatMessage(multiChatHistory.getContent(), multiChatHistory.getRole(), multiChatHistory.getImages());
                    messages.add(chatMessage);

                } else {
                    OllamaChatMessage chatMessage = this.buildChatMessage(chat.getContent(), chat.getRole());
                    messages.add(chatMessage);
                }

            }

        }

        // user message part
        OllamaChatMessage chatMessage = this.buildChatMessage(message, "user", images);
        messages.add(chatMessage);

        return OllamaTextRequest.builder().model(this.MODEL_NAME).messages(messages).build();
    }

    private OllamaChatMessage buildChatMessage(String message, String role) {

        return this.buildChatMessage(message, role, null);
    }

    private OllamaChatMessage buildChatMessage(String message, String role, String[] images) {

        OllamaChatMessage chatMessage = OllamaChatMessage.builder().role(role).content(message).build();

        if (images != null && images.length > 0) {
            chatMessage.setImages(images);
        }

        return chatMessage;
    }

    private List<OllamaChatHistory> buildChatHistory(String message, String[] images, OllamaChatMessage responseMessage, List<OllamaChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (images != null) {
            OllamaMultiChatHistory chatHistory = OllamaMultiChatHistory.builder().role("user").content(message).images(images).build();
            history.add(chatHistory);
        } else {
            OllamaChatHistory chatHistory = OllamaChatHistory.builder().role("user").content(message).build();
            history.add(chatHistory);
        }

        // add ai response message
        if (responseMessage.getImages() != null && responseMessage.getImages().length > 0) {// response with images
            OllamaMultiChatHistory aiChat = OllamaMultiChatHistory.builder().content(responseMessage.getContent()).role(responseMessage.getRole()).images(responseMessage.getImages()).build();
            history.add(aiChat);
        } else {
            OllamaChatHistory aiChat = OllamaChatHistory.builder().content(responseMessage.getContent()).role(responseMessage.getRole()).build();
            history.add(aiChat);
        }

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

    private void buildStreamChatHistory(String message, String[] images, StringBuffer aiText, List<OllamaChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (images != null) {
            OllamaMultiChatHistory chatHistory = OllamaMultiChatHistory.builder().role("user").content(message).images(images).build();
            history.add(chatHistory);
        }else{
            OllamaChatHistory chatHistory = OllamaChatHistory.builder().role("user").content(message).build();
            history.add(chatHistory);
        }

        if (aiText.length() != 0) {
            OllamaChatHistory aiChat = OllamaChatHistory.builder().content(aiText.toString()).role("assistant").build();
            history.add(aiChat);
        }

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }
    }

}
