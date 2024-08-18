package org.liurb.ai.sdk;

import org.junit.Test;
import org.liurb.ai.sdk.ollama.OllamaClient;
import org.liurb.ai.sdk.ollama.bean.OllamaChatHistory;
import org.liurb.ai.sdk.ollama.bean.OllamaGenerationConfig;
import org.liurb.ai.sdk.ollama.conf.OllamaAccount;
import org.liurb.ai.sdk.ollama.dto.OllamaTextResponse;

import java.io.IOException;
import java.util.List;

public class OllamaChatTest {


    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        OllamaAccount account = OllamaAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        OllamaGenerationConfig generationConfig = OllamaGenerationConfig.builder().temperature(0.3).build();

        OllamaClient client = new OllamaClient(account);
        OllamaTextResponse textResponse = client.chat("who are you", generationConfig);
        System.out.println(textResponse);
    }

    @Test
    public void multiTurnChatTest() throws IOException {

        OllamaAccount account = OllamaAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        OllamaGenerationConfig generationConfig = OllamaGenerationConfig.builder().temperature(0.3).build();

        OllamaClient client = new OllamaClient(account);
        OllamaTextResponse textResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(textResponse1);

        List<OllamaChatHistory> history = textResponse1.getHistory();

        OllamaTextResponse textResponse2 = client.chat("who is his wife", history);
        System.out.println(textResponse2);
    }

}
