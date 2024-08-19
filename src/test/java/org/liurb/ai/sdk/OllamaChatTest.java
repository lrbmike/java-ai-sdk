package org.liurb.ai.sdk;

import org.junit.Test;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.ollama.OllamaClient;

import java.io.IOException;

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

//    @Test
//    public void multiTurnChatTest() throws IOException {
//
//        OllamaAccount account = OllamaAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
//
//        OllamaGenerationConfig generationConfig = OllamaGenerationConfig.builder().temperature(0.3).build();
//
//        OllamaClient client = new OllamaClient(account);
//        OllamaTextResponse textResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
//        System.out.println(textResponse1);
//
//        List<OllamaChatHistory> history = textResponse1.getHistory();
//
//        OllamaTextResponse textResponse2 = client.chat("who is his wife", history);
//        System.out.println(textResponse2);
//    }

}
