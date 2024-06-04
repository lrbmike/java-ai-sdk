package org.liurb.ai.sdk.gemini.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ChatTextMessage {

    private String role;

    private List<TextPart> parts;

}
