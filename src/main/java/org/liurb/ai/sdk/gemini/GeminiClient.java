package org.liurb.ai.sdk.gemini;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.liurb.ai.sdk.common.AiBaseClient;
import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.gemini.bean.*;
import org.liurb.ai.sdk.gemini.dto.GeminiTextRequest;
import org.liurb.ai.sdk.gemini.dto.GeminiTextResponse;
import org.liurb.ai.sdk.gemini.enums.GeminiModelEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GeminiClient extends AiBaseClient {


    public GeminiClient(ModelAccount account) {
        super(account);
    }

//    public void stream(String message, GeminiStreamResponseListener responseListener) throws IOException {
//
//        this.stream(message, null, null, null, responseListener);
//    }
//
//    public void stream(String message, GeminiGenerationConfig generationConfig, GeminiStreamResponseListener responseListener) throws IOException {
//
//        this.stream(message, null, generationConfig, null, responseListener);
//    }
//
//    public void stream(String message, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {
//
//        this.stream(message, null, null, history, responseListener);
//    }
//
//    public void stream(String message, GeminiGenerationConfig generationConfig, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {
//
//        this.stream(message, null, generationConfig, history, responseListener);
//    }
//
//    public void stream(String message, MultiPartInlineData inlineData, GeminiStreamResponseListener responseListener) throws IOException {
//
//        this.stream(message, inlineData, null, null, responseListener);
//    }
//
//    public void stream(String message, MultiPartInlineData inlineData, GeminiGenerationConfig generationConfig, List<ChatHistory> history, GeminiStreamResponseListener responseListener) throws IOException {
//
//        if (this.geminiAccount == null || this.geminiAccount.getApiKey() == null || this.geminiAccount.getApiKey().isEmpty()) {
//            throw new RuntimeException("gemini api key is empty");
//        }
//
//        if (this.geminiAccount.getBaseUrl() != null && !this.geminiAccount.getBaseUrl().isEmpty()) {
//            this.BASE_URL = this.geminiAccount.getBaseUrl();
//        }
//
//        //build gemini request body
//        GeminiTextRequest questParams = this.buildGeminiTextRequest(message, inlineData, history);
//
//        if (generationConfig != null) {
//            questParams.setGenerationConfig(generationConfig);
//        }
//
//        MediaType json = MediaType.parse("application/json; charset=utf-8");
//        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(questParams));
//
//        // Be sure to set alt=sse in the URL parameters
//        String url = "{base_url}/v1/{model}:streamGenerateContent?key={api_key}&alt=sse";
//        url = url.replace("{base_url}", this.BASE_URL)
//                .replace("{api_key}", this.geminiAccount.getApiKey())
//                .replace("{model}", this.MODEL_NAME);
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//        this.okHttpClient.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    try (ResponseBody responseBody = response.body()) {
//
//                        StringBuffer textSb = new StringBuffer();
//
//                        InputStream inputStream = responseBody.byteStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            line = line.trim();
//                            if (line.startsWith("data: ")) {
//                                line = line.substring("data: ".length());
//                                GeminiTextResponse streamResponse = JSON.parseObject(line, GeminiTextResponse.class);
//
//                                Content content = streamResponse.getCandidates().get(0).getContent();
//
//                                textSb.append(content.getParts().get(0).getText());
//
//                                responseListener.accept(content);
//                            }
//                        }
//
//                        //handle history
//                        buildStreamChatHistory(message, inlineData, textSb, history);
//
//                    }
//                }
//            }
//        });
//
//    }

    private GeminiTextRequest buildGeminiTextRequest(String message, MediaData mediaData, List<ChatHistory> history) {

        List<GeminiChatMessage> contents = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (ChatHistory chat : history) {

                if (chat instanceof GeminiChatHistory) {
                    GeminiChatHistory geminiChatHistory = (GeminiChatHistory)chat;
                    MediaData historyMediaData = MediaData.builder().type(geminiChatHistory.getInlineData().getMimeType()).url(geminiChatHistory.getInlineData().getData()).build();
                    GeminiChatMessage chatMessage = this.buildChatMessage(geminiChatHistory.getContent(), geminiChatHistory.getRole(), historyMediaData);
                    contents.add(chatMessage);
                }else {
                    GeminiChatMessage textMessage = this.buildChatMessage(chat.getContent(), chat.getRole());
                    contents.add(textMessage);
                }

            }

        }

        // user message part
        GeminiChatMessage chatMessage = this.buildChatMessage(message, "user", mediaData);

        contents.add(chatMessage);

        return GeminiTextRequest.builder().contents(contents).build();
    }

    private GeminiChatMessage buildChatMessage(String text, String role) {

        return this.buildChatMessage(text, role, null);
    }

    private GeminiChatMessage buildChatMessage(String text, String role, MediaData mediaData) {

        List<TextPart> parts = new ArrayList<>();

        TextPart textPart = TextPart.builder().text(text).build();
        parts.add(textPart);

        if (mediaData != null) {
            MultiPartInlineData inlineData = MultiPartInlineData.builder().mimeType(mediaData.getType()).data(mediaData.getUrl()).build();
            MediaPart mediaPart = MediaPart.builder().inlineData(inlineData).build();
            parts.add(mediaPart);
        }

        return GeminiChatMessage.builder().role(role).parts(parts).build();
    }

    private List<ChatHistory> buildChatHistory(String message, MediaData mediaData, ChatMessage aiChatMessage, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (mediaData != null) {
            MultiPartInlineData inlineData = MultiPartInlineData.builder().mimeType(mediaData.getType()).data(mediaData.getUrl()).build();
            GeminiChatHistory chatHistory = GeminiChatHistory.builder().content(message).role("user").inlineData(inlineData).build();
            history.add(chatHistory);
        }else{
            ChatHistory chatHistory = ChatHistory.builder().content(message).role("user").build();
            history.add(chatHistory);
        }

        // add ai response message
        ChatHistory aiChat = ChatHistory.builder().content(aiChatMessage.getContent()).role(aiChatMessage.getRole()).build();
        history.add(aiChat);

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

//    private List<ChatHistory> buildStreamChatHistory(String message, MultiPartInlineData inlineData, StringBuffer aiText, List<ChatHistory> history) {
//
//        if (history == null) {
//            history = new ArrayList<>();
//        }
//
//        // add user chat message
//        if (inlineData != null) {
//            GeminiChatHistory geminiChatHistory = GeminiChatHistory.builder().content(message).role("user").inlineData(inlineData).build();
//            history.add(geminiChatHistory);
//        }else{
//            ChatHistory chatHistory = ChatHistory.builder().content(message).role("user").build();
//            history.add(chatHistory);
//        }
//
//        if (aiText.length() != 0) {
//            ChatHistory aiChat = ChatHistory.builder().content(aiText.toString()).role("model").build();
//            history.add(aiChat);
//        }
//
//        // max 10 chat history
//        if (history.size() > 10) {
//            history = history.subList(0, 10);
//        }
//
//        return history;
//    }

    @Override
    protected String getDefaultModelName() {

        return GeminiModelEnum.GEMINI_PRO_FLASH.getName();
    }

    @Override
    protected String getDefaultBaseUrl() {

        return "https://generativelanguage.googleapis.com";
    }

    @Override
    protected String getApi() {
        String api = "/v1/{model}:generateContent?key={api_key}";
        api = api.replace("{api_key}", this.getAccount().getApiKey())
                .replace("{model}", this.getModelName());
        return api;
    }

    @Override
    protected JSONObject buildChatRequest(String message, MediaData mediaData, GenerationConfig generationConfig, List<ChatHistory> history) {

        GeminiTextRequest questParams = this.buildGeminiTextRequest(message, mediaData, history);
        if (generationConfig != null) {
            GeminiGenerationConfig geminiGenerationConfig = GeminiGenerationConfig.builder()
                    .temperature(generationConfig.getTemperature()).topP(generationConfig.getTopP())
                    .topK(generationConfig.getTopK()).maxOutputTokens(generationConfig.getMaxTokens())
                    .stopSequences(Arrays.asList(generationConfig.getStop()))
                    .build();

            questParams.setGenerationConfig(geminiGenerationConfig);
        }

        return JSON.parseObject(JSON.toJSONString(questParams));
    }

    @Override
    protected AiChatResponse buildChatResponse(String responseBody, String message, MediaData mediaData, List<ChatHistory> history) {
        AiChatResponse response = new AiChatResponse();

        //json string to object
        GeminiTextResponse geminiTextResponse = JSON.parseObject(responseBody, GeminiTextResponse.class);
        //set ai response message
        Content content = geminiTextResponse.getCandidates().get(0).getContent();
        ChatMessage resMessage = ChatMessage.builder().content(content.getParts().get(0).getText()).role(content.getRole()).build();
        response.setMessage(resMessage);

        //handle chat history
        List<ChatHistory> newHistory = this.buildChatHistory(message, mediaData, resMessage, history);
        response.setHistory(newHistory);

        return response;
    }

}
