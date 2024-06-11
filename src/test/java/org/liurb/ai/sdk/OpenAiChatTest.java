package org.liurb.ai.sdk;

import org.junit.Test;
import org.liurb.ai.sdk.openai.OpenAiClient;
import org.liurb.ai.sdk.openai.bean.MaterialData;
import org.liurb.ai.sdk.openai.bean.OpenAiGenerationConfig;
import org.liurb.ai.sdk.openai.conf.OpenAiAccount;
import org.liurb.ai.sdk.openai.dto.OpenAiTextResponse;

import java.io.IOException;

public class OpenAiChatTest {

    String apiKey = "";
    String baseUrl = "";

    @Test
    public void chatTest() throws IOException {

        OpenAiAccount account = OpenAiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        OpenAiGenerationConfig generationConfig = OpenAiGenerationConfig.builder().temperature(0.3).build();

        OpenAiClient client = new OpenAiClient(account);
        OpenAiTextResponse textResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(textResponse1);

        OpenAiTextResponse textResponse2 = client.chat("who is his wife");
        System.out.println(textResponse2);
    }

    @Test
    public void chatMultiModalTest() throws IOException {

        OpenAiAccount account = OpenAiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        String type = "image_url";
        String url = "https://storage.googleapis.com/generativeai-downloads/images/scones.jpg";

        MaterialData materialData = MaterialData.builder().type(type).url(url).build();

        OpenAiClient client = new OpenAiClient(account);
        OpenAiTextResponse textResponse = client.chat("What is this picture", materialData);
        System.out.println(textResponse);
    }

}
