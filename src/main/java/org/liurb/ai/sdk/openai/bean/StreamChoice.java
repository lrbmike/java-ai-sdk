package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class StreamChoice {

    @JSONField(name = "delta")
    private OpenAiMessage delta;

    @JSONField(name = "logprobs")
    private Integer logprobs;

    @JSONField(name = "finish_reason")
    private String finishReason;

    @JSONField(name = "index")
    private Integer index;

}
