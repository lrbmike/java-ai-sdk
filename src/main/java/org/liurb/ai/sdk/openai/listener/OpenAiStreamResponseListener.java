package org.liurb.ai.sdk.openai.listener;

import org.liurb.ai.sdk.openai.bean.StreamChoice;

public interface OpenAiStreamResponseListener {

    void accept(StreamChoice choice);

}
