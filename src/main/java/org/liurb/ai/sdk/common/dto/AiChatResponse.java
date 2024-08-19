package org.liurb.ai.sdk.common.dto;

import lombok.Data;
import org.liurb.ai.sdk.common.bean.ChatHistory;
import org.liurb.ai.sdk.common.bean.ChatMessage;
import org.liurb.ai.sdk.common.bean.MediaData;

import java.util.List;

@Data
public class AiChatResponse {

    private ChatMessage message;

    private MediaData media;

    private List<ChatHistory> history;

}
