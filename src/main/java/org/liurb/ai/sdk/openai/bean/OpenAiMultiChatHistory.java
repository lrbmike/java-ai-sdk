package org.liurb.ai.sdk.openai.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatHistory;


@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OpenAiMultiChatHistory extends ChatHistory {

    private MaterialData materialData;

}
