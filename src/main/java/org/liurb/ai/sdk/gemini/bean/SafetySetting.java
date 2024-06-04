package org.liurb.ai.sdk.gemini.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SafetySetting {

    private String category;

    private String threshold;
}
