//package org.liurb.ai.sdk;
//
//import org.liurb.ai.sdk.ollama.OllamaClient;
//import org.liurb.ai.sdk.ollama.bean.OllamaChatHistory;
//import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;
//import org.liurb.ai.sdk.ollama.bean.OllamaGenerationConfig;
//import org.liurb.ai.sdk.ollama.conf.OllamaAccount;
//import org.liurb.ai.sdk.ollama.listener.OllamaStreamResponseListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OllamaStreamTest {
//
//    static String apiKey = "";
//    static String baseUrl = "";
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        OllamaAccount account = OllamaAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
//
//        OllamaGenerationConfig generationConfig = OllamaGenerationConfig.builder().temperature(0.3).build();
//
//        OllamaClient client = new OllamaClient(account);
//
//        List<OllamaChatHistory> history = new ArrayList<>();
//
//        client.stream("Do you know something about Yao Ming", generationConfig, history, new OllamaStreamResponseListener() {
//            @Override
//            public void accept(OllamaChatMessage streamMessage) {
//                System.out.println("accept1:" + streamMessage);
//            }
//        });
//
//        Thread.sleep(30 * 1000);
//
//        System.out.println(history);
//
//        client.stream("who is his wife", generationConfig, history, new OllamaStreamResponseListener() {
//            @Override
//            public void accept(OllamaChatMessage streamMessage) {
//                System.out.println("accept2:" + streamMessage);
//            }
//        });
//
//    }
//
//}
