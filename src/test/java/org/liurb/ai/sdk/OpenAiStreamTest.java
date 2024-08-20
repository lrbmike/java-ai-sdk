package org.liurb.ai.sdk;

import org.liurb.ai.sdk.common.bean.*;
import org.liurb.ai.sdk.common.listener.AiStreamResponseListener;
import org.liurb.ai.sdk.openai.OpenAiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAiStreamTest {

    static String apiKey = "";
    static String baseUrl = "";

    public static void main(String[] args) throws IOException, InterruptedException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        List<ChatHistory> history = new ArrayList<>();

        OpenAiClient client = new OpenAiClient(account);

        client.stream("Do you know something about Yao Ming", null, generationConfig, history, new AiStreamResponseListener() {
            @Override
            public void accept(AiStreamMessage streamMessage) {
                System.out.println("accept1:" + streamMessage.getContent());
            }
        });

        Thread.sleep(30 * 1000);

        client.stream("who is his wife", null, generationConfig, history, new AiStreamResponseListener() {
            @Override
            public void accept(AiStreamMessage streamMessage) {
                System.out.println("accept2:" + streamMessage.getContent());
            }
        });

//        String type = "image_url";
//        String url = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";
//
//        MediaData mediaData = MediaData.builder().type(type).url(url).build();
//
//        client.stream("What is this picture", mediaData, generationConfig, null, new AiStreamResponseListener() {
//            @Override
//            public void accept(AiStreamMessage streamMessage) {
//                System.out.println("accept3:" + streamMessage.getContent());
//            }
//        });

    }

}
