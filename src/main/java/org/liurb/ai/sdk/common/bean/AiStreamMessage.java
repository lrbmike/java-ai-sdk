package org.liurb.ai.sdk.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class AiStreamMessage {

    private String role;

    private String content;

    private boolean stop;

}
