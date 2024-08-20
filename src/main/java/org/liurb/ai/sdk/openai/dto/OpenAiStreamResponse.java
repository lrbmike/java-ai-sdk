package org.liurb.ai.sdk.openai.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.liurb.ai.sdk.openai.bean.StreamChoice;
import org.liurb.ai.sdk.openai.bean.Usage;

import java.util.List;

@Data
public class OpenAiStreamResponse {


    @JSONField(name = "id")
    private String id;

    @JSONField(name = "object")
    private String object;

    @JSONField(name = "created")
    private Long created;

    @JSONField(name = "model")
    private String model;

    @JSONField(name = "usage")
    private Usage usage;

    @JSONField(name = "choices")
    private List<StreamChoice> choices;

}
