package com.sangalan.llm.classification.intent.service;

import com.sangalan.llm.classification.intent.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntentPromptBuilderTest {

    @Test
    void buildPromptIncludesInstructionsOptionsExamplesAndUserPrompt() {
        IntentPromptBuilder builder = new IntentPromptBuilder(TestFixtures.properties());

        String prompt = builder.buildPrompt("Find the documents approved by Pepe last week.");

        assertTrue(prompt.contains("You are an intent classification assistant."));
        assertTrue(prompt.contains("- SEARCH_DOCUMENTS"));
        assertTrue(prompt.contains("- GET_WORKFLOW_STATUS"));
        assertTrue(prompt.contains("Return only valid JSON."));
        assertTrue(prompt.contains("\"intent\""));
        assertTrue(prompt.contains("\"parameters\""));
        assertTrue(prompt.contains("User: Find all documents approved by Pepe last week"));
        assertTrue(prompt.contains("\"intent\":\"SEARCH_DOCUMENTS\""));
        assertTrue(prompt.contains("User: Find the documents approved by Pepe last week."));
        assertTrue(prompt.endsWith("Response:"));
    }

    @Test
    void buildPromptIncludesParameterSchemaForRichIntentOptions() {
        IntentPromptBuilder builder = new IntentPromptBuilder(TestFixtures.taskProperties());

        String prompt = builder.buildPrompt("List all the tasks with status pending to the user Maria");

        assertTrue(prompt.contains("- LIST_TASKS_BY_STATUS_AND_USER: List all the tasks with status Y to the user X"));
        assertTrue(prompt.contains("- status (string) required: Task status"));
        assertTrue(prompt.contains("- user (string) required: User name or identifier"));
        assertTrue(prompt.contains("\"status\":\"pending\""));
        assertTrue(prompt.contains("\"user\":\"Maria\""));
    }
}
