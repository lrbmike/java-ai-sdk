package org.liurb.ai.sdk.gemini.conf;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GeminiAccount {

    private String apiKey;

    private String baseUrl;

}
