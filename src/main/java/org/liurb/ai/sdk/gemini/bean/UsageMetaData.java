package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

@Data
public class UsageMetaData {

    private Integer promptTokenCount;

    private Integer candidatesTokenCount;

    private Integer totalTokenCount;
}
