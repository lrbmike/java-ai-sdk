package org.liurb.ai.sdk.gemini.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class SchemaItemProperty {

    @JSONField(name = "recipe_name")
    private SchemaItemPropertyRecipeItem recipeName;


}
