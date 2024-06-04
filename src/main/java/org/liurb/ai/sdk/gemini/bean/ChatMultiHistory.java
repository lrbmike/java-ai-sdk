package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatMultiHistory extends ChatHistory {

    private MultiPartInlineData inlineData;

}
