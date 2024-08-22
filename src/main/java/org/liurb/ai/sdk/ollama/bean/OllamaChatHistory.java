package org.liurb.ai.sdk.ollama.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.liurb.ai.sdk.common.bean.ChatHistory;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class OllamaChatHistory extends ChatHistory {

    private List<String> images;
}
