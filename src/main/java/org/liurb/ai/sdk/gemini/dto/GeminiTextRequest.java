package org.liurb.ai.sdk.gemini.dto;

import lombok.Builder;
import lombok.Data;
import org.liurb.ai.sdk.gemini.bean.ChatTextMessage;
import org.liurb.ai.sdk.gemini.bean.GenerationConfig;
import org.liurb.ai.sdk.gemini.bean.SafetySetting;

import java.util.List;

@Builder
@Data
public class GeminiTextRequest {

    private List<ChatTextMessage> contents;

    private GenerationConfig generationConfig;

    private List<SafetySetting> safetySettings;
}
