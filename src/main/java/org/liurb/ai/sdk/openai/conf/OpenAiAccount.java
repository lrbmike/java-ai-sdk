package org.liurb.ai.sdk.openai.conf;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenAiAccount {

    private String apiKey;

    private String baseUrl;

}
