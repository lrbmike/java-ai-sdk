//package org.liurb.ai.sdk;
//
//import org.liurb.ai.sdk.openai.OpenAiClient;
//import org.liurb.ai.sdk.openai.bean.MaterialData;
//import org.liurb.ai.sdk.openai.bean.StreamChoice;
//import org.liurb.ai.sdk.openai.conf.OpenAiAccount;
//import org.liurb.ai.sdk.openai.listener.OpenAiStreamResponseListener;
//
//import java.io.IOException;
//
//public class OpenAiStreamTest {
//
//    static String apiKey = "";
//    static String baseUrl = "";
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        OpenAiAccount account = OpenAiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
//
//        OpenAiClient client = new OpenAiClient(account);
//
////        client.stream("Do you know something about Yao Ming", null, new OpenAiStreamResponseListener() {
////            @Override
////            public void accept(StreamChoice choice) {
////                System.out.println("accept1:" + choice);
////            }
////        });
////
////        Thread.sleep(30 * 1000);
//
////        client.stream("who is his wife", null, new OpenAiStreamResponseListener() {
////            @Override
////            public void accept(StreamChoice choice) {
////                System.out.println("accept2:" + choice);
////            }
////        });
//
//        String type = "image_url";
//        String url = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";
//
//        MaterialData materialData = MaterialData.builder().type(type).url(url).build();
//
//        client.stream("What is this picture", materialData, null, null, new OpenAiStreamResponseListener() {
//            @Override
//            public void accept(StreamChoice choice) {
//                System.out.println("accept3:" + choice);
//            }
//        });
//
//    }
//
//}
