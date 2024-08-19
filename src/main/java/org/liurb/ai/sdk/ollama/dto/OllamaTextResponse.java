package org.liurb.ai.sdk.ollama.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.liurb.ai.sdk.ollama.bean.OllamaAiChatMessage;


@Data
public class OllamaTextResponse {

    @JSONField(name = "model")
    private String model;

    @JSONField(name = "created_at")
    private String createdAt;

    @JSONField(name = "message")
    private OllamaAiChatMessage message;

    @JSONField(name = "done")
    private Boolean done;

    @JSONField(name = "total_duration")
    private Long totalDuration;

    @JSONField(name = "load_duration")
    private Integer loadDuration;

    @JSONField(name = "prompt_eval_count")
    private Integer promptEvalCount;

    @JSONField(name = "prompt_eval_duration")
    private Integer promptEvalDuration;

    @JSONField(name = "eval_count")
    private Integer evalCount;

    @JSONField(name = "eval_duration")
    private Long evalDuration;
}
