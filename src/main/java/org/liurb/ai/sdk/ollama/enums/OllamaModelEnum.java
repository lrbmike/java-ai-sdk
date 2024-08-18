package org.liurb.ai.sdk.ollama.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OllamaModelEnum {

    LLAMA3_1("llama3.1"),
    GEMMA2("gemma2"),
    QWEN2("qwen2"),
    ;

    private final String name;

}
