package com.sangalan.llm.classification.intent.config;

import jakarta.validation.constraints.NotBlank;

import java.util.LinkedHashMap;
import java.util.Map;

public class FewShotExample {

    @NotBlank
    private String user;

    @NotBlank
    private String intent;

    private Map<String, Object> parameters = new LinkedHashMap<>();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters == null ? new LinkedHashMap<>() : parameters;
    }
}
