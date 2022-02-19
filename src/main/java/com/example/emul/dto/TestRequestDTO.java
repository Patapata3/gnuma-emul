package com.example.emul.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestRequestDTO {
    @SerializedName(value = "doc_ids")
    List<String> docIds;

    public TestRequestDTO(List<String> docIds) {
        this.docIds = docIds;
    }

    public List<String> getDocIds() {
        return docIds;
    }

    public void setDocIds(List<String> docIds) {
        this.docIds = docIds;
    }
}
