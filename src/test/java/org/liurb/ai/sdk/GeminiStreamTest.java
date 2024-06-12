package org.liurb.ai.sdk;

import org.liurb.ai.sdk.gemini.bean.Content;
import org.liurb.ai.sdk.gemini.bean.GeminiGenerationConfig;
import org.liurb.ai.sdk.gemini.GeminiClient;
import org.liurb.ai.sdk.gemini.bean.MultiPartInlineData;
import org.liurb.ai.sdk.gemini.conf.GeminiAccount;
import org.liurb.ai.sdk.gemini.listener.GeminiStreamResponseListener;
import org.liurb.ai.sdk.utils.Base64Util;

import java.io.IOException;

public class GeminiStreamTest {

    static String apiKey = "";
    static String baseUrl = "";

    public static void main(String[] args) throws IOException, InterruptedException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GeminiGenerationConfig geminiGenerationConfig = GeminiGenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);

        // Stream and Multi-turn conversations

//        client.stream("Do you know something about Yao Ming", null, generationConfig, new GeminiStreamResponseListener() {
//
//            @Override
//            public void accept(Content content) {
//                System.out.println("accept1:" + content);
//            }
//
//        });
//
//        Thread.sleep(30 * 1000);
//
//        client.stream("who is his wife", null, generationConfig, new GeminiStreamResponseListener() {
//
//            @Override
//            public void accept(Content content) {
//                System.out.println("accept2:" + content);
//            }
//
//        });

        // Multi Modal

        String imageUrl = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";

        String base64 = Base64Util.imageUrlToBase64(imageUrl);

        MultiPartInlineData inlineData = MultiPartInlineData.builder().mimeType("image/jpeg").data(base64).build();

        client.stream("What is this picture", inlineData, geminiGenerationConfig, null, new GeminiStreamResponseListener() {

            @Override
            public void accept(Content content) {
                System.out.println("accept3:" + content);
            }

        });

    }

}
