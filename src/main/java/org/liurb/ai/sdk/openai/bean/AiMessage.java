package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AiMessage {

    @JSONField(name = "role")
    private String role;

    @JSONField(name = "content")
    private String content;

}
