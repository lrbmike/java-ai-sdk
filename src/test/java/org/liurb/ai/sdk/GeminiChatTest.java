package org.liurb.ai.sdk;


import org.junit.Test;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.MediaData;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.dto.AiChatResponse;
import org.liurb.ai.sdk.gemini.GeminiClient;
import org.liurb.ai.sdk.gemini.enums.GeminiModelEnum;
import org.liurb.ai.sdk.utils.Base64Util;

import java.io.IOException;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class GeminiChatTest
{

    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GeminiClient client = new GeminiClient(account);
        AiChatResponse chatResponse = client.chat("who are you");
        System.out.println(chatResponse);
    }

    @Test
    public void multiTurnChatTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);
        AiChatResponse chatResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(chatResponse1);

        // round one history data
        List<ChatHistory> history1 = chatResponse1.getHistory();

        AiChatResponse chatResponse2 = client.chat("who is his wife", generationConfig, history1);
        System.out.println(chatResponse2);

        // round two history data
        List<ChatHistory> history2 = chatResponse2.getHistory();

        AiChatResponse chatResponse3 = client.chat("who is his daughter", generationConfig, history2);
        System.out.println(chatResponse3);
    }

    @Test
    public void chatMultiModalTest() throws IOException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);

        // local image
//        Path img = Paths.get("/path/abc.jpg");
//        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(img));

        // image url
        String imageUrl = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";

        String base64 = Base64Util.imageUrlToBase64(imageUrl);

        MediaData mediaData = MediaData.builder().type("image/jpeg").url(base64).build();

        String message = "What is this picture";

        AiChatResponse chatResponse1 = client.chat(message, mediaData, generationConfig, null);
        System.out.println(chatResponse1);

        // history data
        List<ChatHistory> history = chatResponse1.getHistory();

        AiChatResponse chatResponse2 = client.chat("How many dog are there", generationConfig, history);
        System.out.println(chatResponse2);
    }

}
