package org.liurb.ai.sdk.gemini.bean;

import lombok.Data;

@Data
public class Schema {

    private String type;

    private SchemaItem items;
}
