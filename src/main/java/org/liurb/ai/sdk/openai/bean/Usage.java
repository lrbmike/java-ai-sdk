package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class Usage {

    @JSONField(name = "prompt_tokens")
    private Integer promptTokens;

    @JSONField(name = "completion_tokens")
    private Integer completionTokens;

    @JSONField(name = "total_tokens")
    private Integer totalTokens;

}
