package org.liurb.ai.sdk.openai.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class OpenAiChatMessage {

    private String role;

    private List<ChatContent> content;

}
