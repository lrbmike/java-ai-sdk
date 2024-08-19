package org.liurb.ai.sdk.ollama.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatHistory;

import java.util.List;

@SuperBuilder
@Getter
@Setter
public class OllamaChatHistory extends ChatHistory {

    private List<String> images;
}
