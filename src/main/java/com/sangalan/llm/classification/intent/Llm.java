package com.sangalan.llm.classification.intent;

import com.sangalan.llm.classification.intent.client.LlmHttpClient;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import com.sangalan.llm.classification.intent.service.IntentPromptBuilder;
import com.sangalan.llm.classification.intent.service.IntentResponseParser;

public class Llm {

    private final LlmProperties properties;
    private final LlmHttpClient llmHttpClient;
    private final IntentPromptBuilder promptBuilder;
    private final IntentResponseParser responseParser;

    public Llm(
            LlmProperties properties,
            LlmHttpClient llmHttpClient,
            IntentPromptBuilder promptBuilder,
            IntentResponseParser responseParser
    ) {
        this.properties = properties;
        this.llmHttpClient = llmHttpClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
    }

    public String classify(String userPrompt) {
        return classifyIntent(userPrompt).intent();
    }

    public IntentClassificationResult classifyIntent(String userPrompt) {
        String prompt = promptBuilder.buildPrompt(userPrompt);
        String rawResponse = llmHttpClient.sendPrompt(prompt);
        return responseParser.parseResult(rawResponse);
    }

    public LlmProperties getProperties() {
        return properties;
    }
}
