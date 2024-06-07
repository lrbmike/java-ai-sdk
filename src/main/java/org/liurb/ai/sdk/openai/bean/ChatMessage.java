package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ChatMessage {

    @JSONField(name = "role")
    private String role;

    @JSONField(name = "content")
    private List<ChatContent> content;

}
