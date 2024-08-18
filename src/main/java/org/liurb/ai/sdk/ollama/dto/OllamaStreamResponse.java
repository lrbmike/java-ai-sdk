package org.liurb.ai.sdk.ollama.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;

@Data
public class OllamaStreamResponse {


    @JSONField(name = "model")
    private String model;
    @JSONField(name = "created_at")
    private String createdAt;
    @JSONField(name = "message")
    private OllamaChatMessage message;
    @JSONField(name = "done")
    private Boolean done;
}
