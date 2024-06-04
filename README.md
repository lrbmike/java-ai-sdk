# java-ai-sdk

<font size=2>开发这套 JAVA 版的 AI SDK，主要是目前主流模型厂商的 SDK 并没有很好的支持 JAVA 环境，或者集成到现有业务系统比较困难。</font>

<font size=2>项目主要是对模型厂商的 REST API 进行封装，方便 JAVA 开发者调用，并仅引入了少量的依赖包，避免依赖包冲突。</font>

## Google Gemini

更多的 REST API 信息，可在官方文档中获取 [Gemini API 概览](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn)

- [文本和图片输入](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#text_image_input) : 仅支持 Gemini 1.5 模型或 Gemini 1.0 Pro 模型
- [纯文字输入](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#text_only_input)
- [多轮对话（聊天）](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#chat)
- [流式响应](https://ai.google.dev/gemini-api/docs/api-overview?hl=zh-cn#stream)

### 使用

通过 `GeminiAccount` 配置 API KEY，如何需要使用代理地址，可以配置 `BASE_URL`

```java
GeminiAccount account = GeminiAccount.builder().apiKey(apiKey).baseUrl(baseUrl).build();
```

默认使用 `models/gemini-1.5-flash` 模型，当然也可以通过构造函数，自定义配置

```java
public GeminiClient(String modelName, GeminiAccount geminiAccount) 
```

普通对话

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

多轮对话

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

多模态（可带上下文）

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

目前的全部对话中，已自动实现了历史记录的功能，调用者可通过返回的 `GeminiTextResponse` 对象中获取 `history`

```java
@Data
public class GeminiTextResponse {

    private List<Candidate> candidates;

    private UsageMetaData usageMetadata;

    private List<ChatHistory> history;
}
```

## 后续

后面打算做一个 `Spring` 版本，或者看看目前还有哪些比较难用的模型 `SDK`，就会加入到这个项目来，很难说
