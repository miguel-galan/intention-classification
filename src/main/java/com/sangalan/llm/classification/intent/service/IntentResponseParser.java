package com.sangalan.llm.classification.intent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntentResponseParser {

    private final LlmProperties properties;
    private final ObjectMapper objectMapper;

    public IntentResponseParser(LlmProperties properties) {
        this(properties, new ObjectMapper());
    }

    public IntentResponseParser(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String parse(String rawResponse) {
        return parseResult(rawResponse).intent();
    }

    public IntentClassificationResult parseResult(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return defaultResult();
        }

        String cleaned = clean(rawResponse);
        if (looksLikeJson(cleaned)) {
            return parseJsonResult(cleaned);
        }

        String intent = cleaned.replace("\"", "").trim();
        if (properties.getAllowedIntentNames().contains(intent)) {
            return new IntentClassificationResult(intent, Collections.emptyMap());
        }

        return defaultResult();
    }

    private IntentClassificationResult parseJsonResult(String cleaned) {
        try {
            Map<String, Object> response = objectMapper.readValue(cleaned, new TypeReference<>() {
            });
            Object intentValue = response.get("intent");
            String intent = intentValue == null ? null : intentValue.toString().trim();
            if (!properties.getAllowedIntentNames().contains(intent)) {
                return defaultResult();
            }

            Map<String, Object> parameters = new LinkedHashMap<>();
            Object parametersValue = response.get("parameters");
            if (parametersValue instanceof Map<?, ?> rawParameters) {
                for (Map.Entry<?, ?> entry : rawParameters.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        parameters.put(entry.getKey().toString(), entry.getValue());
                    }
                }
            }
            return new IntentClassificationResult(intent, Collections.unmodifiableMap(parameters));
        } catch (JsonProcessingException e) {
            return defaultResult();
        }
    }

    private IntentClassificationResult defaultResult() {
        return new IntentClassificationResult(properties.getDefaultOption(), Collections.emptyMap());
    }

    private String clean(String rawResponse) {
        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```")) {
            if (cleaned.contains("\n")) {
                cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*\\R", "");
                cleaned = cleaned.replaceFirst("\\s*```$", "");
            } else {
                cleaned = cleaned.replace("```", "");
            }
        }
        return cleaned.trim();
    }

    private boolean looksLikeJson(String cleaned) {
        return cleaned.startsWith("{") && cleaned.endsWith("}");
    }
}
