package org.liurb.ai.sdk.ollama.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OllamaMultiChatHistory extends OllamaChatHistory{

    private String[] images;

}
