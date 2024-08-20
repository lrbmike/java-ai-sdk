package org.liurb.ai.sdk.ollama.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class OllamaAiChatMessage {

    private String role;

    private String content;

    private List<String> images;

}
