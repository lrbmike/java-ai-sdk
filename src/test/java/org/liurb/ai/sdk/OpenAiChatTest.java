package org.liurb.ai.sdk;

import org.junit.Test;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.MediaData;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.openai.OpenAiClient;

import java.io.IOException;
import java.util.List;

public class OpenAiChatTest {

    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        OpenAiClient client = new OpenAiClient(account);
        AiChatResponse textResponse = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(textResponse);
    }

    @Test
    public void multiTurnChatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        OpenAiClient client = new OpenAiClient(account);
        AiChatResponse textResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(textResponse1);

        List<ChatHistory> history = textResponse1.getHistory();

        AiChatResponse textResponse2 = client.chat("who is his wife", generationConfig, history);
        System.out.println(textResponse2);
    }

    @Test
    public void chatMultiModalTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        String type = "image_url";
        String url = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";

        MediaData mediaData = MediaData.builder().type(type).url(url).build();

        OpenAiClient client = new OpenAiClient(account);
        AiChatResponse textResponse = client.chat("What is this picture", mediaData, generationConfig, null);
        System.out.println(textResponse);
    }

}
