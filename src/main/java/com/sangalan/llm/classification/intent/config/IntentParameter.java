package com.sangalan.llm.classification.intent.config;

import jakarta.validation.constraints.NotBlank;

public class IntentParameter {

    @NotBlank
    private String name;

    private String type = "string";

    private String description;

    private boolean required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
