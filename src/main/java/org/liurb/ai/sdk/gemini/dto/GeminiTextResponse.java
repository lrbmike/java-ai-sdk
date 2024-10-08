package org.liurb.ai.sdk.gemini.dto;


import lombok.Data;
import org.liurb.ai.sdk.gemini.bean.Candidate;
import org.liurb.ai.sdk.gemini.bean.UsageMetaData;

import java.util.List;

@Data
public class GeminiTextResponse {

    private List<Candidate> candidates;

    private UsageMetaData usageMetadata;
}
