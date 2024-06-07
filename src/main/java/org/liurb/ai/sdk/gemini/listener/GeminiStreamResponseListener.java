package org.liurb.ai.sdk.gemini.listener;

import org.liurb.ai.sdk.gemini.bean.Content;

public interface GeminiStreamResponseListener {

    void accept(Content content);

}
