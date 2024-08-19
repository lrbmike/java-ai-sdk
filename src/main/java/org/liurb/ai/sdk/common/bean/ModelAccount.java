package org.liurb.ai.sdk.common.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModelAccount {

    private String apiKey;

    private String baseUrl;

}
