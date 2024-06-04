package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

import java.util.List;

@Data
public class Content {

    private List<TextPart> parts;

    private String role;
}
