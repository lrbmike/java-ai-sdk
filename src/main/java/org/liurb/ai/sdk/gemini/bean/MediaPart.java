package org.liurb.ai.sdk.gemini.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MediaPart extends TextPart {

    @JSONField(name = "inline_data")
    private MultiPartInlineData inlineData;
}
