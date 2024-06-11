package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenAiGenerationConfig {

    @JSONField(name = "max_tokens")
    private Integer maxTokens;

    private Integer n;

    private String stop;

    private Double temperature;

    @JSONField(name = "top_p")
    private Double topP;

}
