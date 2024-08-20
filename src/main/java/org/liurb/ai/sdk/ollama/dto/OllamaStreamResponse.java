package org.liurb.ai.sdk.ollama.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.liurb.ai.sdk.ollama.bean.OllamaAiChatMessage;
import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;

@Data
public class OllamaStreamResponse {

    private String model;

    @JSONField(name = "created_at")
    private String createdAt;

    private OllamaAiChatMessage message;

    private Boolean done;
}
