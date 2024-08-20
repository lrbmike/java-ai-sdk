package org.liurb.ai.sdk.ollama;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.liurb.ai.sdk.common.AiBaseClient;
import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.ollama.bean.OllamaAiChatMessage;
import org.liurb.ai.sdk.ollama.bean.OllamaChatHistory;
import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;
import org.liurb.ai.sdk.ollama.bean.OllamaRequestOptions;
import org.liurb.ai.sdk.ollama.dto.OllamaStreamResponse;
import org.liurb.ai.sdk.ollama.dto.OllamaTextRequest;
import org.liurb.ai.sdk.ollama.dto.OllamaTextResponse;
import org.liurb.ai.sdk.ollama.enums.OllamaModelEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class OllamaClient extends AiBaseClient {

    public OllamaClient(ModelAccount account) {
        super(account);
    }

    public OllamaClient(String modelName, ModelAccount account) {
        super(modelName, account);
    }

    private OllamaTextRequest buildOllamaTextRequest(String message, MediaData mediaData, List<ChatHistory> history) {

        List<OllamaChatMessage> messages = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (ChatHistory chatHistory : history) {

                if (chatHistory instanceof OllamaChatHistory) {
                    OllamaChatHistory ollamaChatHistory = (OllamaChatHistory)chatHistory;
                    MediaData historyMediaData = null;
                    if (ollamaChatHistory.getImages() != null) {
                        historyMediaData = MediaData.builder().url(ollamaChatHistory.getImages().get(0)).build();
                    }
                    OllamaChatMessage ollamaChatMessage = this.buildChatMessage(ollamaChatHistory.getContent(),
                            ollamaChatHistory.getRole(), historyMediaData);
                    messages.add(ollamaChatMessage);

                } else {
                    OllamaChatMessage ollamaChatMessage = this.buildChatMessage(chatHistory.getContent(), chatHistory.getRole());
                    messages.add(ollamaChatMessage);
                }

            }

        }

        // user message part
        OllamaChatMessage chatMessage = this.buildChatMessage(message, "user", mediaData);
        messages.add(chatMessage);

        return OllamaTextRequest.builder().model(this.getModelName()).messages(messages).build();
    }

    private OllamaChatMessage buildChatMessage(String message, String role) {

        return this.buildChatMessage(message, role, null);
    }

    private OllamaChatMessage buildChatMessage(String message, String role, MediaData mediaData) {

        OllamaChatMessage chatMessage = OllamaChatMessage.builder().content(message).role(role).build();

        if (mediaData != null) {
            chatMessage.setImages(Arrays.asList(mediaData.getUrl()));
        }

        return chatMessage;
    }

    private List<ChatHistory> buildChatHistory(String message, MediaData mediaData, OllamaAiChatMessage aiChatMessage, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (mediaData != null) {
            OllamaChatHistory chatHistory = OllamaChatHistory.builder().role("user").content(message).images(Arrays.asList(mediaData.getUrl())).build();
            history.add(chatHistory);
        } else {
            ChatHistory chatHistory = OllamaChatHistory.builder().role("user").content(message).build();
            history.add(chatHistory);
        }

        // add ai response message
        if (aiChatMessage.getImages() != null && aiChatMessage.getImages().size() > 0) {// response with images
            OllamaChatHistory aiChat = OllamaChatHistory.builder().content(aiChatMessage.getContent()).role(aiChatMessage.getRole()).images(aiChatMessage.getImages()).build();
            history.add(aiChat);
        } else {
            ChatHistory aiChat = OllamaChatHistory.builder().content(aiChatMessage.getContent()).role(aiChatMessage.getRole()).build();
            history.add(aiChat);
        }

        // max 10 chat history
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        return history;
    }

    @Override
    protected String getDefaultModelName() {

        return OllamaModelEnum.QWEN2_MIN.getName();
    }

    @Override
    protected String getDefaultBaseUrl() {
        return "http://localhost:11434";
    }

    @Override
    protected String getApi() {
        return "/api/chat";
    }

    @Override
    protected JSONObject buildChatRequest(String message, MediaData mediaData, GenerationConfig generationConfig, boolean stream, List<ChatHistory> history) {

        OllamaTextRequest questParams = this.buildOllamaTextRequest(message, mediaData, history);
        questParams.setStream(stream);

        if (generationConfig != null) {
            OllamaRequestOptions.OllamaRequestOptionsBuilder builder = OllamaRequestOptions.builder();

            Optional.ofNullable(generationConfig.getTemperature()).ifPresent(builder::temperature);
            Optional.ofNullable(generationConfig.getTopK()).ifPresent(builder::topK);
            Optional.ofNullable(generationConfig.getTopP()).ifPresent(builder::topP);
            Optional.ofNullable(generationConfig.getStop()).ifPresent(stop -> builder.stop(Arrays.asList(stop)));

            questParams.setOptions(builder.build());
        }

        return JSON.parseObject(JSON.toJSONString(questParams));
    }

    @Override
    protected AiChatResponse buildChatResponse(String responseBody, String message, MediaData mediaData, List<ChatHistory> history) {
        AiChatResponse response = new AiChatResponse();

        ////json string to object
        OllamaTextResponse ollamaTextResponse = JSON.parseObject(responseBody, OllamaTextResponse.class);

        //set ai response message
        OllamaAiChatMessage aiMessage = ollamaTextResponse.getMessage();
        ChatMessage resMessage = ChatMessage.builder().content(aiMessage.getContent()).role(aiMessage.getRole()).build();
        response.setMessage(resMessage);
        //set media content
        if (aiMessage.getImages() != null) {
            MediaData resMedia = MediaData.builder().url(aiMessage.getImages().get(0)).build();
            response.setMedia(resMedia);
        }

        //handle chat history
        List<ChatHistory> newHistory = this.buildChatHistory(message, mediaData, aiMessage, history);
        response.setHistory(newHistory);

        return response;
    }

    @Override
    protected AiStreamMessage buildStreamMessage(String responseLine) {
        OllamaStreamResponse streamResponse = JSON.parseObject(responseLine, OllamaStreamResponse.class);

        Boolean done = streamResponse.getDone();
        if (done) {
            return AiStreamMessage.builder().stop(true).build();
        }

        OllamaAiChatMessage streamMessage = streamResponse.getMessage();

        return AiStreamMessage.builder().stop(false).content(streamMessage.getContent()).role(streamMessage.getRole()).build();
    }

    @Override
    protected void buildStreamChatHistory(String message, MediaData mediaData, String aiMessage, List<ChatHistory> history) {

        OllamaAiChatMessage resMessage = OllamaAiChatMessage.builder().role("assistant").content(aiMessage).build();
        this.buildChatHistory(message, mediaData, resMessage, history);
    }

}
