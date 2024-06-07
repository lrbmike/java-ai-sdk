package org.liurb.ai.sdk.openai;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.openai.bean.*;
import org.liurb.ai.sdk.openai.conf.OpenAiAccount;
import org.liurb.ai.sdk.openai.dto.OpenAiStreamResponse;
import org.liurb.ai.sdk.openai.dto.OpenAiTextRequest;
import org.liurb.ai.sdk.openai.dto.OpenAiTextResponse;
import org.liurb.ai.sdk.openai.enums.OpenAiModelEnum;
import org.liurb.ai.sdk.openai.listener.OpenAiStreamResponseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpenAiClient {

    private OpenAiAccount openaiAccount;
    private String BASE_URL = "https://api.openai.com";
    private String MODEL_NAME = OpenAiModelEnum.GPT_35_TURBO.getName();
    private OkHttpClient okHttpClient;
    private List<ChatHistory> history;

    private OpenAiClient() {}

    public OpenAiClient(OpenAiAccount openaiAccount) {
        this.openaiAccount = openaiAccount;
        this.okHttpClient = this.defaultClient();
        this.history = new ArrayList<>();
    }

    public OpenAiClient(String modelName, OpenAiAccount openaiAccount) {
        this.openaiAccount = openaiAccount;
        this.okHttpClient = this.defaultClient();
        this.MODEL_NAME = modelName;
        this.history = new ArrayList<>();
    }

    public OpenAiClient(OpenAiAccount openaiAccount, OkHttpClient okHttpClient) {
        this.openaiAccount = openaiAccount;
        this.okHttpClient = okHttpClient;
        this.history = new ArrayList<>();
    }

    public OpenAiClient(String modelName, OpenAiAccount openaiAccount, OkHttpClient okHttpClient) {
        this.openaiAccount = openaiAccount;
        this.okHttpClient = okHttpClient;
        this.MODEL_NAME = modelName;
        this.history = new ArrayList<>();
    }


    public OpenAiTextResponse chat(String message) throws IOException {

        return this.chat(message, null);
    }

    public OpenAiTextResponse chat(String message, MaterialData materialData) throws IOException {

        if (this.openaiAccount == null || this.openaiAccount.getApiKey() == null || this.openaiAccount.getApiKey().isEmpty()) {
            throw new RuntimeException("gemini api key is empty");
        }

        if (this.openaiAccount.getBaseUrl() != null && !this.openaiAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.openaiAccount.getBaseUrl();
        }


        OpenAiTextRequest questParams = this.buildOpenAiTextRequest(message, materialData, history);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        String url = "{base_url}/v1/chat/completions";
        url = url.replace("{base_url}", this.BASE_URL);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.openaiAccount.getApiKey())
                .post(requestBody)
                .build();

        Response response = this.okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            System.out.println(responseBody);

            OpenAiTextResponse textResponse = JSON.parseObject(responseBody, OpenAiTextResponse.class);
            textResponse.setHistory(this.buildChatHistory(message, materialData, textResponse.getChoices(), history));
            return textResponse;
        }


        return null;
    }

    public void stream(String message, MaterialData materialData, OpenAiStreamResponseListener responseListener) throws IOException {

        if (this.openaiAccount == null || this.openaiAccount.getApiKey() == null || this.openaiAccount.getApiKey().isEmpty()) {
            throw new RuntimeException("gemini api key is empty");
        }

        if (this.openaiAccount.getBaseUrl() != null && !this.openaiAccount.getBaseUrl().isEmpty()) {
            this.BASE_URL = this.openaiAccount.getBaseUrl();
        }


        OpenAiTextRequest questParams = this.buildOpenAiTextRequest(message, materialData, history);
        questParams.setStream(true);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));

        String url = "{base_url}/v1/chat/completions";
        url = url.replace("{base_url}", this.BASE_URL);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.openaiAccount.getApiKey())
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
                            System.out.println(line);
                            if (line.startsWith("data: ")) {
                                line = line.substring("data: ".length());
                                if ("[DONE]".equals(line)) {
                                    break;
                                }
                                OpenAiStreamResponse streamResponse = JSON.parseObject(line, OpenAiStreamResponse.class);

                                StreamChoice streamChoice = streamResponse.getChoices().get(0);
                                AiMessage content = streamChoice.getDelta();

                                textSb.append(content.getContent());

                                responseListener.accept(streamChoice);
                            }
                        }

                        //handle history
                        buildStreamChatHistory(message, materialData, textSb, history);

                    }
                }
            }
        });

    }


    private OpenAiTextRequest buildOpenAiTextRequest(String message, MaterialData materialData, List<ChatHistory> history) {

        List<ChatMessage> messages = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (ChatHistory chat : history) {

                if (chat instanceof OpenAiMultiChatHistory) {
                    OpenAiMultiChatHistory openAiMultiChatHistory = (OpenAiMultiChatHistory)chat;
                    ChatMessage chatMessage = this.buildChatMessage(openAiMultiChatHistory.getText(), openAiMultiChatHistory.getRole(), openAiMultiChatHistory.getMaterialData());
                    messages.add(chatMessage);

                } else {
                    ChatMessage chatMessage = this.buildChatMessage(chat.getText(), chat.getRole());
                    messages.add(chatMessage);
                }

            }

        }

        // user message part
        ChatMessage chatMessage = this.buildChatMessage(message, "user", materialData);
        messages.add(chatMessage);

        return OpenAiTextRequest.builder().model(this.MODEL_NAME).messages(messages).build();
    }

    private ChatMessage buildChatMessage(String message, String role) {

        return this.buildChatMessage(message, role, null);
    }

    private ChatMessage buildChatMessage(String message, String role, MaterialData materialData) {

        List<ChatContent> contents = new ArrayList<>();

        ChatContent chatContent = ChatContent.builder().type("text").text(message).build();
        contents.add(chatContent);

        if (materialData != null) {
            if ("image_url".equals(materialData.getType())) {
                ImageUrl imageUrl = ImageUrl.builder().url(materialData.getUrl()).build();
                ChatImageContent imageContent = ChatImageContent.builder().type("image_url").imageUrl(imageUrl).build();
                contents.add(imageContent);
            }
//            else if ("file".equals(materialData.getType())) {
//                FileUrl fileUrl = FileUrl.builder().url(materialData.getUrl()).build();
//                ChatFileContent fileContent = ChatFileContent.builder().type("file").fileUrl(fileUrl).build();
//                contents.add(fileContent);
//            }
        }

        return ChatMessage.builder().role(role).content(contents).build();
    }

    private List<ChatHistory> buildChatHistory(String message, MaterialData materialData, List<Choice> choices, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (materialData != null) {
            OpenAiMultiChatHistory chatHistory = OpenAiMultiChatHistory.builder().role("user").text(message).materialData(materialData).build();
            history.add(chatHistory);
        } else {
            ChatHistory chatHistory = ChatHistory.builder().role("user").text(message).build();
            history.add(chatHistory);
        }

        // add ai response message
        AiMessage aiMessage = choices.get(0).getMessage();
        ChatHistory aiChat = ChatHistory.builder().text(aiMessage.getContent()).role(aiMessage.getRole()).build();
        history.add(aiChat);

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

    private List<ChatHistory> buildStreamChatHistory(String message, MaterialData materialData, StringBuffer aiText, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (materialData != null) {
            OpenAiMultiChatHistory chatHistory = OpenAiMultiChatHistory.builder().role("user").text(message).materialData(materialData).build();
            history.add(chatHistory);
        }else{
            ChatHistory chatHistory = ChatHistory.builder().role("user").text(message).build();
            history.add(chatHistory);
        }

        if (aiText.length() != 0) {
            ChatHistory aiChat = ChatHistory.builder().text(aiText.toString()).role("assistant").build();
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
