package org.liurb.ai.sdk.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
public class ChatHistory {

    private String text;

    private String role;

}
