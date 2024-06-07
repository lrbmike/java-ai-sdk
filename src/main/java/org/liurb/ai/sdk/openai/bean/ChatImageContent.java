package org.liurb.ai.sdk.openai.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatImageContent extends ChatContent{

    @JSONField(name = "image_url")
    private ImageUrl imageUrl;

}
