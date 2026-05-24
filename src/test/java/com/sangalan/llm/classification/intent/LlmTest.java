package com.sangalan.llm.classification.intent;

import com.sangalan.llm.classification.intent.client.LlmHttpClient;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import com.sangalan.llm.classification.intent.service.IntentPromptBuilder;
import com.sangalan.llm.classification.intent.service.IntentResponseParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmTest {

    @Test
    void classifyBuildsPromptSendsItAndParsesResponse() {
        LlmProperties properties = TestFixtures.properties();
        CapturingClient client = new CapturingClient(properties);
        Llm llm = new Llm(
                properties,
                client,
                new IntentPromptBuilder(properties),
                new IntentResponseParser(properties)
        );

        String intent = llm.classify("Find the documents approved by Pepe last week.");

        assertEquals("SEARCH_DOCUMENTS", intent);
        assertTrue(client.capturedPrompt.contains("Find the documents approved by Pepe last week."));
        assertTrue(client.capturedPrompt.contains("Return only valid JSON."));
    }

    @Test
    void classifyIntentReturnsIntentAndParameters() {
        LlmProperties properties = TestFixtures.taskProperties();
        CapturingClient client = new CapturingClient(properties);
        client.response = """
                {"intent":"LIST_TASKS_BY_STATUS_AND_USER","parameters":{"status":"pending","user":"Maria"}}
                """;
        Llm llm = new Llm(
                properties,
                client,
                new IntentPromptBuilder(properties),
                new IntentResponseParser(properties)
        );

        IntentClassificationResult result = llm.classifyIntent("List all the tasks with status pending to the user Maria");

        assertEquals("LIST_TASKS_BY_STATUS_AND_USER", result.intent());
        assertEquals("pending", result.parameters().get("status"));
        assertEquals("Maria", result.parameters().get("user"));
    }

    private static class CapturingClient extends LlmHttpClient {

        private String capturedPrompt;
        private String response = "\"SEARCH_DOCUMENTS\"";

        CapturingClient(LlmProperties properties) {
            super(properties);
        }

        @Override
        public String sendPrompt(String prompt) {
            this.capturedPrompt = prompt;
            return response;
        }
    }
}
