package com.sangalan.llm.classification.intent;

import com.sangalan.llm.classification.intent.config.FewShotExample;
import com.sangalan.llm.classification.intent.config.IntentOption;
import com.sangalan.llm.classification.intent.config.IntentParameter;
import com.sangalan.llm.classification.intent.config.LlmProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static LlmProperties properties() {
        LlmProperties properties = new LlmProperties();
        properties.setUrl("http://localhost:12434/engines/llama.cpp/v1/chat/completions");
        properties.setModel("qwen2.5:7b");
        properties.setSystemPrompt("You are an intent classification assistant.");
        properties.setDefaultOption("UNKNOWN");
        properties.setOptions(List.of("SEARCH_DOCUMENTS", "GET_WORKFLOW_STATUS", "GET_USER_TASKS", "UNKNOWN"));
        properties.setFewShots(List.of(
                fewShot("Find all documents approved by Pepe last week", "SEARCH_DOCUMENTS"),
                fewShot("What is the current status of the invoice approval workflow?", "GET_WORKFLOW_STATUS"),
                fewShot("Show me the tasks assigned to Maria", "GET_USER_TASKS")
        ));
        return properties;
    }

    public static FewShotExample fewShot(String user, String intent) {
        FewShotExample example = new FewShotExample();
        example.setUser(user);
        example.setIntent(intent);
        return example;
    }

    public static LlmProperties taskProperties() {
        LlmProperties properties = new LlmProperties();
        properties.setUrl("http://localhost:12434/engines/llama.cpp/v1/chat/completions");
        properties.setModel("qwen2.5:7b");
        properties.setSystemPrompt("You classify task-management user requests.");
        properties.setDefaultOption("UNKNOWN");
        properties.setIntentOptions(List.of(
                intentOption("LIST_USERS_WITH_ACTIVE_TASKS", "List the users with active tasks"),
                intentOption("LIST_TASKS_EXPIRING_IN_DAYS", "List the tasks that will expire in X days",
                        parameter("days", "integer", "Number of days before expiration", true)),
                intentOption("LIST_TOP_DOCUMENTS_IN_TASKS", "List the top 10 documents that are part of tasks most of the time",
                        parameter("limit", "integer", "Maximum number of documents to return", false)),
                intentOption("LIST_EXPIRED_TASKS", "List the expired tasks"),
                intentOption("LIST_LAST_ASSIGNED_TASKS", "List the last X tasks assigned",
                        parameter("limit", "integer", "Number of recent assigned tasks to return", true)),
                intentOption("LIST_TASKS_ASSIGNED_TO_USER", "List all the tasks assigned to the user X",
                        parameter("user", "string", "User name or identifier", true)),
                intentOption("LIST_TASKS_BY_STATUS_AND_USER", "List all the tasks with status Y to the user X",
                        parameter("status", "string", "Task status", true),
                        parameter("user", "string", "User name or identifier", true)),
                intentOption("UNKNOWN", "Use when the request does not match any task intent")
        ));
        properties.setFewShots(List.of(
                fewShot("List the tasks that will expire in 7 days", "LIST_TASKS_EXPIRING_IN_DAYS", Map.of("days", 7)),
                fewShot("List all the tasks with status pending to the user Maria", "LIST_TASKS_BY_STATUS_AND_USER",
                        Map.of("status", "pending", "user", "Maria"))
        ));
        return properties;
    }

    public static FewShotExample fewShot(String user, String intent, Map<String, Object> parameters) {
        FewShotExample example = fewShot(user, intent);
        example.setParameters(new LinkedHashMap<>(parameters));
        return example;
    }

    public static IntentOption intentOption(String name, String description, IntentParameter... parameters) {
        IntentOption option = new IntentOption();
        option.setName(name);
        option.setDescription(description);
        option.setParameters(List.of(parameters));
        return option;
    }

    public static IntentParameter parameter(String name, String type, String description, boolean required) {
        IntentParameter parameter = new IntentParameter();
        parameter.setName(name);
        parameter.setType(type);
        parameter.setDescription(description);
        parameter.setRequired(required);
        return parameter;
    }
}
