package com.sangalan.llm.classification.intent.autoconfigure;

import com.sangalan.llm.classification.intent.Llm;
import com.sangalan.llm.classification.intent.client.LlmHttpClient;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import com.sangalan.llm.classification.intent.service.IntentPromptBuilder;
import com.sangalan.llm.classification.intent.service.IntentResponseParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LlmProperties.class)
public class LlmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Llm llm(
            LlmProperties properties,
            LlmHttpClient llmHttpClient,
            IntentPromptBuilder promptBuilder,
            IntentResponseParser responseParser
    ) {
        return new Llm(properties, llmHttpClient, promptBuilder, responseParser);
    }

    @Bean
    @ConditionalOnMissingBean
    public LlmHttpClient llmHttpClient(LlmProperties properties) {
        return new LlmHttpClient(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IntentPromptBuilder intentPromptBuilder(LlmProperties properties) {
        return new IntentPromptBuilder(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IntentResponseParser intentResponseParser(LlmProperties properties) {
        return new IntentResponseParser(properties);
    }
}
