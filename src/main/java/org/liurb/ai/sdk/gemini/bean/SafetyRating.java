package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

@Data
public class SafetyRating {

    private String category;

    private String probability;

}
