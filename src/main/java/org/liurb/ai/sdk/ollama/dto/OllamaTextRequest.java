package org.liurb.ai.sdk.ollama.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;
import org.liurb.ai.sdk.ollama.bean.OllamaRequestOptions;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class OllamaTextRequest {

    @JSONField(name = "model")
    private String model;
    @JSONField(name = "messages")
    private List<OllamaChatMessage> messages;
    @JSONField(name = "stream")
    private Boolean stream;
    @JSONField(name = "format")
    private String format;
    @JSONField(name = "images")
    private ArrayList<String> images;
    @JSONField(name = "options")
    private OllamaRequestOptions options;
}
