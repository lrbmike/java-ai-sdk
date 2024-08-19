package org.liurb.ai.sdk.ollama.bean;

import lombok.Data;

import java.util.List;

@Data
public class OllamaAiChatMessage {

    private String role;

    private String content;

    private List<String> images;

}
