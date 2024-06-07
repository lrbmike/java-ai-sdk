package org.liurb.ai.sdk.openai.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
public class ChatContent {

    private String type;

    private String text;

}
