package org.liurb.ai.sdk.openai;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.liurb.ai.sdk.common.AiBaseClient;
import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.openai.bean.*;
import org.liurb.ai.sdk.openai.dto.OpenAiStreamResponse;
import org.liurb.ai.sdk.openai.dto.OpenAiTextRequest;
import org.liurb.ai.sdk.openai.dto.OpenAiTextResponse;
import org.liurb.ai.sdk.openai.enums.OpenAiModelEnum;

import java.util.ArrayList;
import java.util.List;

public class OpenAiClient extends AiBaseClient {

    public OpenAiClient(ModelAccount account) {
        super(account);
    }

    public OpenAiClient(String modelName, ModelAccount account) {
        super(modelName, account);
    }

    private OpenAiTextRequest buildOpenAiTextRequest(String message, MediaData mediaData, List<ChatHistory> history) {

        List<OpenAiChatMessage> messages = new ArrayList<>();

        if (history != null && !history.isEmpty()) {// history part

            for (ChatHistory chat : history) {

                if (chat instanceof OpenAiChatHistory) {
                    OpenAiChatHistory openAiChatHistory = (OpenAiChatHistory)chat;
                    OpenAiChatMessage chatMessage = this.buildChatMessage(openAiChatHistory.getContent(),
                            openAiChatHistory.getRole(), openAiChatHistory.getMediaData());
                    messages.add(chatMessage);

                } else {
                    OpenAiChatMessage chatMessage = this.buildChatMessage(chat.getContent(), chat.getRole());
                    messages.add(chatMessage);
                }

            }

        }

        // user message part
        OpenAiChatMessage chatMessage = this.buildChatMessage(message, "user", mediaData);
        messages.add(chatMessage);

        return OpenAiTextRequest.builder().model(this.getModelName()).messages(messages).build();
    }

    private OpenAiChatMessage buildChatMessage(String message, String role) {

        return this.buildChatMessage(message, role, null);
    }

    private OpenAiChatMessage buildChatMessage(String message, String role, MediaData mediaData) {

        List<ChatContent> contents = new ArrayList<>();

        ChatContent chatContent = ChatContent.builder().type("text").text(message).build();
        contents.add(chatContent);

        if (mediaData != null) {
            if ("image_url".equals(mediaData.getType())) {
                ImageUrl imageUrl = ImageUrl.builder().url(mediaData.getUrl()).build();
                ChatImageContent imageContent = ChatImageContent.builder().type("image_url").imageUrl(imageUrl).build();
                contents.add(imageContent);
            }
//            else if ("file".equals(materialData.getType())) {
//                FileUrl fileUrl = FileUrl.builder().url(materialData.getUrl()).build();
//                ChatFileContent fileContent = ChatFileContent.builder().type("file").fileUrl(fileUrl).build();
//                contents.add(fileContent);
//            }
        }

        return OpenAiChatMessage.builder().role(role).content(contents).build();
    }

    private List<ChatHistory> buildChatHistory(String message, MediaData mediaData, ChatMessage aiChatMessage, List<ChatHistory> history) {

        if (history == null) {
            history = new ArrayList<>();
        }

        // add user chat message
        if (mediaData != null) {
            OpenAiChatHistory chatHistory = OpenAiChatHistory.builder().role("user").content(message).mediaData(mediaData).build();
            history.add(chatHistory);
        } else {
            ChatHistory chatHistory = ChatHistory.builder().role("user").content(message).build();
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

        return OpenAiModelEnum.GPT_35_TURBO.getName();
    }

    @Override
    protected String getDefaultBaseUrl() {

        return "https://api.openai.com";
    }

    @Override
    protected String getApi() {

        return "/v1/chat/completions";
    }

    @Override
    protected JSONObject buildChatRequest(String message, MediaData mediaData, GenerationConfig generationConfig, boolean stream, List<ChatHistory> history) {

        OpenAiTextRequest questParams = this.buildOpenAiTextRequest(message, mediaData, history);
        questParams.setStream(stream);

        if (generationConfig != null) {
            questParams.setTemperature(generationConfig.getTemperature());
            questParams.setMaxTokens(generationConfig.getMaxTokens());
            questParams.setTopP(generationConfig.getTopP());
            questParams.setStop(generationConfig.getStop());
        }

        return JSON.parseObject(JSON.toJSONString(questParams));
    }

    @Override
    protected AiChatResponse buildChatResponse(String responseBody, String message, MediaData mediaData, List<ChatHistory> history) {
        AiChatResponse response = new AiChatResponse();

        //json string to object
        OpenAiTextResponse openAiTextResponse = JSON.parseObject(responseBody, OpenAiTextResponse.class);
        //set ai response message
        OpenAiMessage aiMessage = openAiTextResponse.getChoices().get(0).getMessage();
        ChatMessage resMessage = ChatMessage.builder().content(aiMessage.getContent()).role(aiMessage.getRole()).build();
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
            if ("[DONE]".equals(responseLine)) {
                return AiStreamMessage.builder().stop(true).build();
            }

            OpenAiStreamResponse streamResponse = JSON.parseObject(responseLine, OpenAiStreamResponse.class);
            StreamChoice streamChoice = streamResponse.getChoices().get(0);
            OpenAiMessage content = streamChoice.getDelta();

            return AiStreamMessage.builder().stop(false).content(content.getContent()).role(content.getRole()).build();
        }

        return null;
    }

    @Override
    protected void buildStreamChatHistory(String message, MediaData mediaData, String aiMessage, List<ChatHistory> history) {

        ChatMessage resMessage = ChatMessage.builder().role("assistant").content(aiMessage).build();
        this.buildChatHistory(message, mediaData, resMessage, history);
    }

}
