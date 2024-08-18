package org.liurb.ai.sdk.ollama.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
public class OllamaRequestOptions {

    @JSONField(name = "num_keep")
    private Integer numKeep;
    @JSONField(name = "seed")
    private Integer seed;
    @JSONField(name = "num_predict")
    private Integer numPredict;
    @JSONField(name = "top_k")
    private Integer topK;
    @JSONField(name = "top_p")
    private Double topP;
    @JSONField(name = "min_p")
    private Double minP;
    @JSONField(name = "tfs_z")
    private Double tfsZ;
    @JSONField(name = "typical_p")
    private Double typicalP;
    @JSONField(name = "repeat_last_n")
    private Integer repeatLastN;
    @JSONField(name = "temperature")
    private Double temperature;
    @JSONField(name = "repeat_penalty")
    private Double repeatPenalty;
    @JSONField(name = "presence_penalty")
    private Double presencePenalty;
    @JSONField(name = "frequency_penalty")
    private Double frequencyPenalty;
    @JSONField(name = "mirostat")
    private Integer mirostat;
    @JSONField(name = "mirostat_tau")
    private Double mirostatTau;
    @JSONField(name = "mirostat_eta")
    private Double mirostatEta;
    @JSONField(name = "penalize_newline")
    private Boolean penalizeNewline;
    @JSONField(name = "stop")
    private List<String> stop;
    @JSONField(name = "numa")
    private Boolean numa;
    @JSONField(name = "num_ctx")
    private Integer numCtx;
    @JSONField(name = "num_batch")
    private Integer numBatch;
    @JSONField(name = "num_gpu")
    private Integer numGpu;
    @JSONField(name = "main_gpu")
    private Integer mainGpu;
    @JSONField(name = "low_vram")
    private Boolean lowVram;
    @JSONField(name = "f16_kv")
    private Boolean f16Kv;
    @JSONField(name = "vocab_only")
    private Boolean vocabOnly;
    @JSONField(name = "use_mmap")
    private Boolean useMmap;
    @JSONField(name = "use_mlock")
    private Boolean useMlock;
    @JSONField(name = "num_thread")
    private Integer numThread;
}
