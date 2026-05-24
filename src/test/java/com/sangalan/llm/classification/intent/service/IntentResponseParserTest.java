package com.sangalan.llm.classification.intent.service;

import com.sangalan.llm.classification.intent.TestFixtures;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntentResponseParserTest {

    private final IntentResponseParser parser = new IntentResponseParser(TestFixtures.properties());

    @Test
    void parsesAllowedIntent() {
        assertEquals("SEARCH_DOCUMENTS", parser.parse("SEARCH_DOCUMENTS"));
    }

    @Test
    void removesQuotes() {
        assertEquals("SEARCH_DOCUMENTS", parser.parse("\"SEARCH_DOCUMENTS\""));
    }

    @Test
    void removesMarkdownCodeFences() {
        assertEquals("SEARCH_DOCUMENTS", parser.parse("```SEARCH_DOCUMENTS```"));
    }

    @Test
    void returnsDefaultOptionForInvalidOption() {
        assertEquals("UNKNOWN", parser.parse("SOMETHING_ELSE"));
    }

    @Test
    void returnsDefaultOptionForBlankResponse() {
        assertEquals("UNKNOWN", parser.parse("   "));
    }

    @Test
    void parsesJsonIntentWithParameters() {
        IntentResponseParser taskParser = new IntentResponseParser(TestFixtures.taskProperties());

        IntentClassificationResult result = taskParser.parseResult("""
                {"intent":"LIST_TASKS_BY_STATUS_AND_USER","parameters":{"status":"pending","user":"Maria"}}
                """);

        assertEquals("LIST_TASKS_BY_STATUS_AND_USER", result.intent());
        assertEquals("pending", result.parameters().get("status"));
        assertEquals("Maria", result.parameters().get("user"));
    }

    @Test
    void parsesJsonInsideMarkdownFence() {
        IntentResponseParser taskParser = new IntentResponseParser(TestFixtures.taskProperties());

        IntentClassificationResult result = taskParser.parseResult("""
                ```json
                {"intent":"LIST_TASKS_EXPIRING_IN_DAYS","parameters":{"days":7}}
                ```
                """);

        assertEquals("LIST_TASKS_EXPIRING_IN_DAYS", result.intent());
        assertEquals(7, result.parameters().get("days"));
    }

    @Test
    void returnsDefaultResultForJsonWithInvalidIntent() {
        IntentResponseParser taskParser = new IntentResponseParser(TestFixtures.taskProperties());

        IntentClassificationResult result = taskParser.parseResult("""
                {"intent":"DELETE_EVERYTHING","parameters":{"user":"Maria"}}
                """);

        assertEquals("UNKNOWN", result.intent());
        assertEquals(0, result.parameters().size());
    }
}
