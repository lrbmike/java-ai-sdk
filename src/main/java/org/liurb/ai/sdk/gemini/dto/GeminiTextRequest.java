package org.liurb.ai.sdk.gemini.dto;

import lombok.Builder;
import lombok.Data;
import org.liurb.ai.sdk.gemini.bean.GeminiChatMessage;
import org.liurb.ai.sdk.gemini.bean.GeminiGenerationConfig;
import org.liurb.ai.sdk.gemini.bean.SafetySetting;

import java.util.List;

@Builder
@Data
public class GeminiTextRequest {

    private List<GeminiChatMessage> contents;

    private GeminiGenerationConfig generationConfig;

    private List<SafetySetting> safetySettings;
}
