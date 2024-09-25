# java-ai-sdk

[English](README.md) | [中文简体](README_CN.md)

The development of this JAVA-based AI SDK is primarily aimed at addressing the lack of adequate support for the JAVA environment in the SDK provided by mainstream model manufacturers, or the difficulties encountered when integrating them into existing business systems.

The project mainly involves encapsulating the REST API provided by model manufacturers, making it convenient for JAVA developers to use. It also introduces only a minimal number of dependency packages to avoid conflicts.

The SDK currently supports historical context. Simply pass in the corresponding historical data when calling the interface. For specific examples, please refer to the multi-turn dialogue in the example below.

## Importing

### Maven

```xml
<dependency>
    <groupId>org.liurb.ai.sdk</groupId>
    <artifactId>java-ai-sdk</artifactId>
    <version>${version}</version>
</dependency>
```

You can get the version here：[Maven Central](https://central.sonatype.com/artifact/org.liurb.ai.sdk/java-ai-sdk)

## Google Gemini

For more information about the REST API, please refer to the official documentation. [Gemini API Overview](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn)

- [Text and image input](https://ai.google.dev/gemini-api/docs/api-overview#text_image_input) : It only supports the Gemini 1.5 model or the Gemini 1.0 Pro model.
- [Text only input](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#text_only_input)
- [Multi-turn conversations (chat)](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#chat)
- [Streamed responses](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#stream)

## Usage

Configure the  `API KEY`  through the `ModelAccount` settings. If you need to use a reverse proxy, you can configure the  `BASE_URL`.

```java
ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
```

Build a request client

```java
public GeminiClient(ModelAccount account) 
```

Chat

```java
@Test
public void chatTest() throws IOException {

    ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

    GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

    GeminiClient client = new GeminiClient(account);
    // By default, use the models/gemini-1.5-flash model, or customize the configuration.
    // client.chat("models/gemini-1.5-pro", who are you", generationConfig);
    AiChatResponse chatResponse = client.chat("who are you", generationConfig);
    System.out.println(chatResponse);
}
```

Multi-turn Chat

```java
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
```

Multimodal (with context)

```java
@Test
public void chatMultiModalTest() throws IOException {

    ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

    GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

    GeminiClient client = new GeminiClient(GeminiModelEnum.GEMINI_PRO.getName(), account);

    // image url
    String imageUrl = "https://pic.qqtn.com/uploadfiles/2009-6/2009614181816.jpg";
	// Convert the image to base64
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
```

Stream Chat

```java
ModelAccount account = ModelAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

List<ChatHistory> history = new ArrayList<>();

GeminiClient client = new GeminiClient(account);

client.stream("Do you know something about Yao Ming", generationConfig, history, new AiStreamResponseListener() {

    @Override
    public void accept(AiStreamMessage streamMessage) {
        System.out.println("accept1:" + streamMessage.getContent());
    }

});
```

In the chat methods, the function of automatically implementing historical records has been realized. The caller can obtain the `history` through the returned `AiChatResponse` object.

```java
@Data
public class AiChatResponse {

    private ChatMessage message;

    private MediaData media;

    private List<ChatHistory> history;
}
```

For streaming conversations, it is necessary to pass in a list of historical records from the outside.

```java
List<ChatHistory> history = new ArrayList<>();
client.stream("ask something", generationConfig, history, new AiStreamResponseListener() {
    //...
});
```

## OpenAI

For more information about the REST API, please refer to the official documentation.[API Overview](https://platform.openai.com/docs/api-reference/authentication)

- Multi-turn conversations
- [Vision](https://platform.openai.com/docs/guides/vision)
- Streamed responses

### Usage

The usage is similar to `Google Gemini`. For specific examples, please refer to the samples in the `Test` package.

### Additional Notes

Currently, it is compatible with some free `API` interfaces, meaning any interface that is compatible with `/v1/chat/completions` can be used. Simply replace `apiKey` and `baseUrl` as needed.

## Subsequent

In the future, I plan to create a `Spring` version, or look at which model `SDKs` are currently difficult to use and will be added to this project. 
