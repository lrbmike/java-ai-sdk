package org.liurb.ai.sdk.ollama.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatMessage;

import java.util.List;


@SuperBuilder
@Getter
@Setter
public class OllamaChatMessage extends ChatMessage {

    @Singular
    List<String> images;

}
