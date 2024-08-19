package org.liurb.ai.sdk.openai.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import org.liurb.ai.sdk.openai.bean.OpenAiChatMessage;

import java.util.List;


@Builder
@Data
public class OpenAiTextRequest {


    @JSONField(name = "model")
    private String model;

    @JSONField(name = "messages")
    private List<OpenAiChatMessage> messages;

    @JSONField(name = "max_tokens")
    private Integer maxTokens;

    private Integer n;

    private String stop;

    private boolean stream;

    private Double temperature;

    @JSONField(name = "top_p")
    private Double topP;


}
