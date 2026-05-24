package com.sangalan.llm.classification.intent.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class IntentOption {

    @NotBlank
    private String name;

    private String description;

    @Valid
    private List<IntentParameter> parameters = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IntentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<IntentParameter> parameters) {
        this.parameters = parameters == null ? new ArrayList<>() : parameters;
    }
}
