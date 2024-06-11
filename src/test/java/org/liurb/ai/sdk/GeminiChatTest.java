package org.liurb.ai.sdk;


import org.junit.Test;
import org.liurb.ai.sdk.gemini.GeminiClient;
import org.liurb.ai.sdk.gemini.bean.GeminiGenerationConfig;
import org.liurb.ai.sdk.gemini.bean.MultiPartInlineData;
import org.liurb.ai.sdk.gemini.dto.GeminiTextResponse;
import org.liurb.ai.sdk.gemini.enums.GeminiModelEnum;
import org.liurb.ai.sdk.utils.Base64Util;
import org.liurb.ai.sdk.gemini.conf.GeminiAccount;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class GeminiChatTest
{

    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GeminiGenerationConfig geminiGenerationConfig = GeminiGenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);
        GeminiTextResponse chatResponse1 = client.chat("Do you know something about Yao Ming", geminiGenerationConfig);
        System.out.println(chatResponse1);

        GeminiTextResponse chatResponse2 = client.chat("who is his wife");
        System.out.println(chatResponse2);

        GeminiTextResponse chatResponse3 = client.chat("who is his daughter", geminiGenerationConfig);
        System.out.println(chatResponse3);
    }

    @Test
    public void chatMultiModalTest() throws IOException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GeminiGenerationConfig geminiGenerationConfig = GeminiGenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(GeminiModelEnum.GEMINI_PRO.getName(), account);

        // local image
//        Path img = Paths.get("/path/abc.jpg");
//        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(img));

        // image url
        String imageUrl = "https://storage.googleapis.com/generativeai-downloads/images/scones.jpg";

        String base64 = Base64Util.imageUrlToBase64(imageUrl);

        MultiPartInlineData inlineData = MultiPartInlineData.builder().mimeType("image/jpeg").data(base64).build();

        String message = "What is this picture";

        GeminiTextResponse chatResponse1 = client.chat(message, inlineData, geminiGenerationConfig);
        System.out.println(chatResponse1);

        GeminiTextResponse chatResponse2 = client.chat("How many flowers are there", geminiGenerationConfig);
        System.out.println(chatResponse2);
    }

}
