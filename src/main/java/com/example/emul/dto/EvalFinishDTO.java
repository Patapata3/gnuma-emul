package com.example.emul.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class EvalFinishDTO {
    private String classifierId;
    private String address;
    @SerializedName(value = "model_id", alternate = "modelId")
    private UUID modelId;
    private List<MetricDTO> metrics;

    public EvalFinishDTO(String classifierId, String address, UUID modelId, List<MetricDTO> metrics) {
        this.classifierId = classifierId;
        this.address = address;
        this.modelId = modelId;
        this.metrics = metrics;
    }

    public String getClassifierId() {
        return classifierId;
    }

    public String getAddress() {
        return address;
    }

    public UUID getModelId() {
        return modelId;
    }

    public List<MetricDTO> getMetrics() {
        return metrics;
    }
}
