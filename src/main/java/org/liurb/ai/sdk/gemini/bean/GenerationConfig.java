package org.liurb.ai.sdk.gemini.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GenerationConfig {


    private List<String> stopSequences;

    private Double temperature;

    private Integer maxOutputTokens;

    private Double topP;

    private Integer topK;
}
