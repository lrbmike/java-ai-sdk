package org.liurb.ai.sdk.ollama.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OllamaChatMessage {

    @JSONField(name = "role")
    private String role;

    @JSONField(name = "content")
    private String content;

    @JSONField(name = "images")
    private String[] images;

}
