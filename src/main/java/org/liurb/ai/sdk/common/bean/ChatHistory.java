package org.liurb.ai.sdk.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class ChatHistory {

    private String role;

    private String content;

    private String text;

}
