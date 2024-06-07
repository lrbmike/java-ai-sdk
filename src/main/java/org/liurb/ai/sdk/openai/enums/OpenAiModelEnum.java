package org.liurb.ai.sdk.openai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OpenAi model
 *
 * Learn more about the models
 * https://platform.openai.com/docs/models
 */
@AllArgsConstructor
@Getter
public enum OpenAiModelEnum {

    GPT_35_TURBO("gpt-3.5-turbo"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4("gpt-4"),
    GPT_4O_TURBO("gpt-4o"),
    ;

    private final String name;

}
