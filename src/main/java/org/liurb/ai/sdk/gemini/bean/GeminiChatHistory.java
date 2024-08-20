package org.liurb.ai.sdk.gemini.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatHistory;

@SuperBuilder
@Getter
@Setter
public class GeminiChatHistory extends ChatHistory {

    private MultiPartInlineData inlineData;

}
