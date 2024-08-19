package org.liurb.ai.sdk.openai.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.MediaData;


@SuperBuilder
@Getter
@Setter
public class OpenAiChatHistory extends ChatHistory {

    private MediaData mediaData;

}