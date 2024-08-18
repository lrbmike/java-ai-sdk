package org.liurb.ai.sdk.ollama.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
public class OllamaChatHistory {

    private String role;

    private String content;
}
