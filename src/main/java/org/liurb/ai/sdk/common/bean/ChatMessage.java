package org.liurb.ai.sdk.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ChatMessage {

    String role;

    String content;

}
