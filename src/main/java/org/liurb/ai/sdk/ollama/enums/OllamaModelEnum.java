package org.liurb.ai.sdk.ollama.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OllamaModelEnum {

    LLAMA3_1("llama3.1"),
    GEMMA2("gemma2"),
    QWEN2("qwen2"),
    QWEN2_5("qwen2.5"),
    ;

    private final String name;

}
