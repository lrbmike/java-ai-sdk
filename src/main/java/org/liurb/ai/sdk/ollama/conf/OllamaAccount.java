package org.liurb.ai.sdk.ollama.conf;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OllamaAccount {

    private String apiKey;

    private String baseUrl;

}
