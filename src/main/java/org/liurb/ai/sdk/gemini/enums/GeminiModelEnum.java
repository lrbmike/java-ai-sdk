package org.liurb.ai.sdk.gemini.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Gemini model
 *
 * Learn more about the models
 * https://ai.google.dev/gemini-api/docs/models/gemini
 */
@AllArgsConstructor
@Getter
public enum GeminiModelEnum {

    GEMINI_FLASH("models/gemini-1.5-flash"),
    GEMINI_PRO("models/gemini-1.5-pro"),
    GEMINI_PRO_VISION("models/gemini-pro-vision"),
    EMBEDDING_001("models/text-embedding-004"),
    AQA("models/aqa")
    ;

    private final String name;

}
