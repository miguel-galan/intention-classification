package com.sangalan.llm.classification.intent.autoconfigure;

import com.sangalan.llm.classification.intent.Llm;
import com.sangalan.llm.classification.intent.client.LlmHttpClient;
import com.sangalan.llm.classification.intent.service.IntentPromptBuilder;
import com.sangalan.llm.classification.intent.service.IntentResponseParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LlmAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LlmAutoConfiguration.class))
            .withPropertyValues(
                    "sangalan.classify.llm.url=http://localhost:12434/engines/llama.cpp/v1/chat/completions",
                    "sangalan.classify.llm.model=qwen2.5:7b",
                    "sangalan.classify.llm.system-prompt=You are an intent classification assistant.",
                    "sangalan.classify.llm.default-option=UNKNOWN",
                    "sangalan.classify.llm.options[0]=SEARCH_DOCUMENTS",
                    "sangalan.classify.llm.options[1]=UNKNOWN",
                    "sangalan.classify.llm.few-shots[0].user=Find all documents approved by Pepe last week",
                    "sangalan.classify.llm.few-shots[0].intent=SEARCH_DOCUMENTS"
            );

    @Test
    void createsClassifierAndSupportingBeansFromProperties() {
        contextRunner.run(context -> {
            assertNotNull(context.getBean(Llm.class));
            assertNotNull(context.getBean(LlmHttpClient.class));
            assertNotNull(context.getBean(IntentPromptBuilder.class));
            assertNotNull(context.getBean(IntentResponseParser.class));
        });
    }
}
