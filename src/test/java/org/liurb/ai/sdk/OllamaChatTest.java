package org.liurb.ai.sdk;

import org.junit.Test;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.ollama.OllamaClient;

import java.io.IOException;
import java.util.List;

public class OllamaChatTest {


    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        OllamaClient client = new OllamaClient(account);
        AiChatResponse textResponse = client.chat("who are you", generationConfig);
        System.out.println(textResponse);
    }

    @Test
    public void multiTurnChatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        OllamaClient client = new OllamaClient(account);
        AiChatResponse textResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(textResponse1);

        List<ChatHistory> history = textResponse1.getHistory();

        AiChatResponse textResponse2 = client.chat("who is his wife", generationConfig, history);
        System.out.println(textResponse2);
    }

}
