package com.sangalan.llm.classification.intent;

import com.sangalan.llm.classification.intent.autoconfigure.LlmAutoConfiguration;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = LlmAutoConfiguration.class)
@TestPropertySource("classpath:application-integration.properties")
class LlmRealIntegrationTest {

    @Autowired
    private Llm llm;

    private String userPrompt;

    @BeforeEach
    void setup() {
        userPrompt = System.getProperty(
                "llm.integration.user-prompt",
                System.getenv().getOrDefault(
                        "LLM_INTEGRATION_USER_PROMPT",
                        "Lista todas las tareas expiradas de Carlos"
                )
        );
    }

    @Tag("integration")
    @Test
    void classifiesAgainstRealLlmRunner() {
        Instant start = Instant.now();
        IntentClassificationResult result = llm.classifyIntent(userPrompt);
        Duration elapsed = Duration.between(start, Instant.now());

        assertNotNull(result);
        assertTrue(llm.getProperties().getAllowedIntentNames().contains(result.intent()));
        assertNotNull(result.parameters());

        long totalMillis = elapsed.toMillis();
        long minutes = totalMillis / 60000;
        long seconds = (totalMillis % 60000) / 1000;
        double totalSeconds = totalMillis / 1000.0;

        System.out.println("Prompt: " + userPrompt);
        System.out.println("Intent: " + result.intent());
        System.out.println("Parameters: " + result.parameters());
        System.out.printf("Elapsed: %dm %ds (%.3f s)%n", minutes, seconds, totalSeconds);
    }
}
