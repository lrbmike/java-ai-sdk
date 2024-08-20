package org.liurb.ai.sdk;

import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.listener.AiStreamResponseListener;
import org.liurb.ai.sdk.gemini.GeminiClient;
import org.liurb.ai.sdk.utils.Base64Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeminiStreamTest {

    static String apiKey = "";
    static String baseUrl = "";

    public static void main(String[] args) throws IOException, InterruptedException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        List<ChatHistory> history = new ArrayList<>();

        GeminiClient client = new GeminiClient(account);

        // Stream and Multi-turn conversations

        client.stream("Do you know something about Yao Ming", generationConfig, history, new AiStreamResponseListener() {

            @Override
            public void accept(AiStreamMessage streamMessage) {
                System.out.println("accept1:" + streamMessage.getContent());
            }

        });

        Thread.sleep(30 * 1000);

        client.stream("who is his wife", generationConfig, history, new AiStreamResponseListener() {

            @Override
            public void accept(AiStreamMessage streamMessage) {
                System.out.println("accept2:" + streamMessage.getContent());
            }

        });

        // Multi Modal

        String imageUrl = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";

        String base64 = Base64Util.imageUrlToBase64(imageUrl);

        MediaData mediaData = MediaData.builder().type("image/jpeg").url(base64).build();

        client.stream("What is this picture", mediaData, generationConfig, history, new AiStreamResponseListener() {

            @Override
            public void accept(AiStreamMessage streamMessage) {
                System.out.println("accept3:" + streamMessage.getContent());
            }

        });

    }

}
