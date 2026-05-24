package com.sangalan.llm.classification.intent.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangalan.llm.classification.intent.LlmClassificationException;
import com.sangalan.llm.classification.intent.config.LlmProperties;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class LlmHttpClient {

    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public LlmHttpClient(LlmProperties properties) {
        this(properties, new ObjectMapper());
    }

    public LlmHttpClient(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build();
    }

    public String sendPrompt(String prompt) {
        OpenAiCompatibleRequest body = new OpenAiCompatibleRequest(
                properties.getModel(),
                properties.getTemperature(),
                List.of(new OpenAiCompatibleRequest.Message("user", prompt))
        );

        HttpRequest request = HttpRequest.newBuilder(URI.create(properties.getUrl()))
                .timeout(Duration.ofMillis(properties.getTimeoutMs()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(writeRequest(body)))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new LlmClassificationException("LLM endpoint request failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmClassificationException("LLM endpoint request was interrupted", e);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new LlmClassificationException("LLM endpoint returned HTTP status " + response.statusCode());
        }

        return extractContent(response.body());
    }

    private String writeRequest(OpenAiCompatibleRequest body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new LlmClassificationException("Could not serialize LLM request", e);
        }
    }

    private String extractContent(String responseBody) {
        try {
            OpenAiCompatibleResponse response = objectMapper.readValue(responseBody, OpenAiCompatibleResponse.class);
            if (response.getChoices() == null || response.getChoices().isEmpty()
                    || response.getChoices().get(0).getMessage() == null) {
                throw new LlmClassificationException("LLM response did not include choices[0].message.content");
            }
            String content = response.getChoices().get(0).getMessage().getContent();
            if (content == null) {
                throw new LlmClassificationException("LLM response content was null");
            }
            return content;
        } catch (JsonProcessingException e) {
            throw new LlmClassificationException("Could not parse LLM response", e);
        }
    }
}
