package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

import java.util.List;

@Data
public class Candidate {

    private Content content;

    private String finishReason;

    private Integer index;

    private List<SafetyRating> safetyRatings;

}
