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
import java.util.Optional;


public class GeminiClient extends AiBaseClient {


    public GeminiClient(ModelAccount account) {
        super(account);
    }

    private GeminiTextRequest buildGeminiTextRequest(String modelName, String message, MediaData mediaData, List<ChatHistory> history) {

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

    @Override
    protected String getDefaultModelName() {

        return GeminiModelEnum.GEMINI_FLASH.getName();
    }

    @Override
    protected String getDefaultBaseUrl() {

        return "https://generativelanguage.googleapis.com";
    }

    @Override
    protected String getApi(String modelName) {
        String api = "/v1/{model}:generateContent?key={api_key}";
        if (this.getStreaming()) {//stream api
            api = "/v1/{model}:streamGenerateContent?key={api_key}&alt=sse";
        }
        api = api.replace("{api_key}", this.getAccount().getApiKey())
                .replace("{model}", modelName);
        return api;
    }

    @Override
    protected JSONObject buildChatRequest(String modelName, String message, MediaData mediaData, GenerationConfig generationConfig, boolean stream, List<ChatHistory> history) {

        GeminiTextRequest questParams = this.buildGeminiTextRequest(modelName, message, mediaData, history);

        if (generationConfig != null) {
            GeminiGenerationConfig.GeminiGenerationConfigBuilder geminiBuilder = GeminiGenerationConfig.builder();

            Optional.ofNullable(generationConfig.getTemperature()).ifPresent(geminiBuilder::temperature);
            Optional.ofNullable(generationConfig.getTopP()).ifPresent(geminiBuilder::topP);
            Optional.ofNullable(generationConfig.getTopK()).ifPresent(geminiBuilder::topK);
            Optional.ofNullable(generationConfig.getMaxTokens()).ifPresent(geminiBuilder::maxOutputTokens);
            Optional.ofNullable(generationConfig.getStop()).ifPresent(stop -> geminiBuilder.stopSequences(Arrays.asList(stop)));

            questParams.setGenerationConfig(geminiBuilder.build());
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

    @Override
    protected AiStreamMessage buildStreamMessage(String responseLine) {
        if (responseLine.startsWith("data: ")) {
            responseLine = responseLine.substring("data: ".length());

            GeminiTextResponse streamResponse = JSON.parseObject(responseLine, GeminiTextResponse.class);

            Content content = streamResponse.getCandidates().get(0).getContent();

            return AiStreamMessage.builder().stop(false).content(content.getParts().get(0).getText()).role("model").build();
        }
        return null;
    }

    @Override
    protected void buildStreamChatHistory(String message, MediaData mediaData, String aiMessage, List<ChatHistory> history) {
        ChatMessage resMessage = ChatMessage.builder().role("model").content(aiMessage).build();
        this.buildChatHistory(message, mediaData, resMessage, history);
    }

}
