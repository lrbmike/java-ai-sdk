package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

@Data
public class SchemaItem {

    private String type;

    private SchemaItemProperty properties;
}
