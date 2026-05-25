package com.sangalan.llm.classification.intent.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "sangalan.classify.llm")
public class LlmProperties {

    @NotBlank
    private String url;

    private String model;

    @NotBlank
    private String systemPrompt;

    private double temperature = 0;

    private int timeoutMs = 30000;

    private String apiKey;

    private String authHeader = "Authorization";

    private String authScheme = "Bearer";

    private String defaultOption = "UNKNOWN";

    private List<String> options = new ArrayList<>();

    @Valid
    private List<IntentOption> intentOptions = new ArrayList<>();

    @Valid
    private List<FewShotExample> fewShots = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAuthHeader() {
        if (authHeader == null || authHeader.isBlank()) {
            return "Authorization";
        }
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public String getAuthScheme() {
        if (authScheme == null) {
            return "";
        }
        return authScheme;
    }

    public void setAuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }

    public String getDefaultOption() {
        if (defaultOption == null || defaultOption.isBlank()) {
            return "UNKNOWN";
        }
        return defaultOption;
    }

    public void setDefaultOption(String defaultOption) {
        this.defaultOption = defaultOption;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options == null ? new ArrayList<>() : options;
    }

    public List<IntentOption> getIntentOptions() {
        return intentOptions;
    }

    public void setIntentOptions(List<IntentOption> intentOptions) {
        this.intentOptions = intentOptions == null ? new ArrayList<>() : intentOptions;
    }

    public List<String> getAllowedIntentNames() {
        if (intentOptions != null && !intentOptions.isEmpty()) {
            return intentOptions.stream()
                    .map(IntentOption::getName)
                    .toList();
        }
        return options;
    }

    public List<FewShotExample> getFewShots() {
        return fewShots;
    }

    public void setFewShots(List<FewShotExample> fewShots) {
        this.fewShots = fewShots == null ? new ArrayList<>() : fewShots;
    }

    @AssertTrue(message = "defaultOption must be included in options")
    public boolean isDefaultOptionIncludedInOptions() {
        List<String> allowedIntentNames = getAllowedIntentNames();
        return allowedIntentNames == null || allowedIntentNames.isEmpty() || allowedIntentNames.contains(getDefaultOption());
    }

    @AssertTrue(message = "options or intentOptions must not be empty")
    public boolean isAnyIntentOptionConfigured() {
        return (options != null && !options.isEmpty()) || (intentOptions != null && !intentOptions.isEmpty());
    }

    @AssertTrue(message = "few-shot intents must be included in options")
    public boolean isFewShotIntentsIncludedInOptions() {
        List<String> allowedIntentNames = getAllowedIntentNames();
        if (allowedIntentNames == null || fewShots == null) {
            return true;
        }
        return fewShots.stream()
                .filter(example -> example != null && example.getIntent() != null)
                .allMatch(example -> allowedIntentNames.contains(example.getIntent()));
    }

    public String buildAuthorizationValue() {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        String normalizedScheme = getAuthScheme().trim();
        if (normalizedScheme.isEmpty()) {
            return apiKey;
        }
        return normalizedScheme + " " + apiKey;
    }
}
