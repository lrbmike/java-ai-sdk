package org.liurb.ai.sdk;

import org.liurb.ai.sdk.common.bean.AiStreamMessage;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.GenerationConfig;
import org.liurb.ai.sdk.common.bean.ModelAccount;
import org.liurb.ai.sdk.common.listener.AiStreamResponseListener;
import org.liurb.ai.sdk.ollama.OllamaClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OllamaStreamTest {

    static String apiKey = "";
    static String baseUrl = "";

    public static void main(String[] args) throws IOException, InterruptedException {

        ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        OllamaClient client = new OllamaClient(account);

        List<ChatHistory> history = new ArrayList<>();

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

    }

}
