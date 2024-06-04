package org.liurb.ai.sdk.gemini.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPartInlineData {

    @JSONField(name = "mime_type")
    private String mimeType;

    private String data;

}
