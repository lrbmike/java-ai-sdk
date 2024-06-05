# java-ai-sdk

[English](README.md) | [中文简体](README_CN.md)

The development of this JAVA-based AI SDK is primarily aimed at addressing the lack of adequate support for the JAVA environment in the SDK provided by mainstream model manufacturers, or the difficulties encountered when integrating them into existing business systems.

The project mainly involves encapsulating the REST API provided by model manufacturers, making it convenient for JAVA developers to use. It also introduces only a minimal number of dependency packages to avoid conflicts.

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

Configure the  `API KEY`  through the `GeminiAccount` settings. If you need to use a reverse proxy, you can configure the  `BASE_URL`.

```java
GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
```

By default, the `models/gemini-1.5-flash` model is used, but you can also customize the configuration through the constructor.

```java
public GeminiClient(String modelName, GeminiAccount geminiAccount) 
```

Chat

```java
    @Test
    public void chatTest() throws IOException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);
        GeminiTextResponse chatResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(chatResponse1);
    }

```

Multi-turn Chat

```java
    @Test
    public void chatTest() throws IOException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(account);
        GeminiTextResponse chatResponse1 = client.chat("Do you know something about Yao Ming", generationConfig);
        System.out.println(chatResponse1);

        GeminiTextResponse chatResponse2 = client.chat("who is his wife");
        System.out.println(chatResponse2);

        GeminiTextResponse chatResponse3 = client.chat("who is his daughter", generationConfig);
        System.out.println(chatResponse3);
    }
```

Multimodal (with context)

```java
@Test
    public void chatMultiModalTest() throws IOException {

        GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();

        GenerationConfig generationConfig = GenerationConfig.builder().temperature(0.3).build();

        GeminiClient client = new GeminiClient(GeminiModelEnum.GEMINI_PRO.getName(), account);

        // local image
//        Path img = Paths.get("/path/abc.jpg");
//        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(img));

        // image url
        String imageUrl = "https://storage.googleapis.com/generativeai-downloads/images/scones.jpg";

        String base64 = Base64Util.imageUrlToBase64(imageUrl);

        MultiPartInlineData inlineData = MultiPartInlineData.builder().mimeType("image/jpeg").data(base64).build();

        String message = "What is this picture";

        GeminiTextResponse chatResponse1 = client.chat(message, inlineData, generationConfig);
        System.out.println(chatResponse1);

        GeminiTextResponse chatResponse2 = client.chat("How many flowers are there", generationConfig);
        System.out.println(chatResponse2);
    }
```

In the current full conversation methods, the function of automatically implementing historical records has been realized. The caller can obtain the `history` through the returned `GeminiTextResponse` object.

```java
@Data
public class GeminiTextResponse {

    private List<Candidate> candidates;

    private UsageMetaData usageMetadata;

    private List<ChatHistory> history;
}
```

## Subsequent

In the future, I plan to create a `Spring` version, or look at which model `SDKs` are currently difficult to use and will be added to this project. 
