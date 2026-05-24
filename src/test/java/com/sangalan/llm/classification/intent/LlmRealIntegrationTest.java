package com.sangalan.llm.classification.intent;

import com.sangalan.llm.classification.intent.client.LlmHttpClient;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import com.sangalan.llm.classification.intent.service.IntentPromptBuilder;
import com.sangalan.llm.classification.intent.service.IntentResponseParser;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmRealIntegrationTest {

    @Test
    void classifiesAgainstRealLlmRunnerWhenEnabled() throws IOException {
        Properties props = loadIntegrationProperties();
        boolean enabled = Boolean.parseBoolean(readProperty("llm.integration.enabled", props, "false"));
        Assumptions.assumeTrue(enabled, "Real LLM integration test is disabled. Set llm.integration.enabled=true.");

        LlmProperties llmProperties = TestFixtures.taskProperties();
        llmProperties.setUrl(readProperty("llm.integration.url", props, llmProperties.getUrl()));
        llmProperties.setModel(readProperty("llm.integration.model", props, llmProperties.getModel()));
        llmProperties.setSystemPrompt(readProperty("llm.integration.system-prompt", props, llmProperties.getSystemPrompt()));
        llmProperties.setTimeoutMs(Integer.parseInt(readProperty(
                "llm.integration.timeout-ms",
                props,
                String.valueOf(llmProperties.getTimeoutMs())
        )));

        String userPrompt = readProperty(
                "llm.integration.user-prompt",
                props,
                "List all the tasks with status pending to the user Maria"
        );

        Llm llm = new Llm(
                llmProperties,
                new LlmHttpClient(llmProperties),
                new IntentPromptBuilder(llmProperties),
                new IntentResponseParser(llmProperties)
        );

        IntentClassificationResult result = llm.classifyIntent(userPrompt);

        assertNotNull(result);
        assertTrue(llmProperties.getAllowedIntentNames().contains(result.intent()));
        assertNotNull(result.parameters());

        System.out.println("Prompt: " + userPrompt);
        System.out.println("Intent: " + result.intent());
        System.out.println("Parameters: " + result.parameters());
    }

    private Properties loadIntegrationProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application-integration.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        }
        return properties;
    }

    private String readProperty(String key, Properties fileProperties, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String envValue = System.getenv(toEnvKey(key));
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return fileProperties.getProperty(key, defaultValue);
    }

    private String toEnvKey(String key) {
        return key.toUpperCase().replace('.', '_').replace('-', '_');
    }
}
