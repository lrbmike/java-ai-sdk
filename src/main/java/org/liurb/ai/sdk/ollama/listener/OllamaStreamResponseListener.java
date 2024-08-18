package org.liurb.ai.sdk.ollama.listener;

import org.liurb.ai.sdk.ollama.bean.OllamaChatMessage;

public interface OllamaStreamResponseListener {

    void accept(OllamaChatMessage message);
}
