package org.liurb.ai.sdk.common.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenerationConfig {

    private Double temperature;

    private Integer topK;

    private Double topP;

    private String stop;

    private Integer maxTokens;

}
