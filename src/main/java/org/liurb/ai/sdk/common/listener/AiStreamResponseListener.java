package org.liurb.ai.sdk.common.listener;

import org.liurb.ai.sdk.common.bean.AiStreamMessage;

public interface AiStreamResponseListener {

    void accept(AiStreamMessage message);

}
