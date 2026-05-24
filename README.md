# LLM Intent Classification

Reusable Spring Boot library for classifying user prompts into one configured intent option through an OpenAI-compatible LLM endpoint.

The library can return either the selected intent only or a structured result with extracted parameters.

## Usage

Add the library as a dependency, configure the endpoint and allowed intents, then inject `Llm`:

```java
import com.sangalan.llm.classification.intent.Llm;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private final Llm llm;

    public MyService(Llm llm) {
        this.llm = llm;
    }

    public String detectIntent(String userPrompt) {
        return llm.classify(userPrompt);
    }
}
```

For intent plus extracted parameters:

```java
import com.sangalan.llm.classification.intent.Llm;
import com.sangalan.llm.classification.intent.model.IntentClassificationResult;
import org.springframework.stereotype.Service;

@Service
public class TaskQueryService {

    private final Llm llm;

    public TaskQueryService(Llm llm) {
        this.llm = llm;
    }

    public IntentClassificationResult classifyTaskQuery(String userPrompt) {
        return llm.classifyIntent(userPrompt);
    }
}
```

Example result:

```json
{
  "intent": "LIST_TASKS_BY_STATUS_AND_USER",
  "parameters": {
    "status": "pending",
    "user": "Maria"
  }
}
```

## Configuration

```properties
sangalan.classify.llm.url=http://localhost:12434/engines/llama.cpp/v1/chat/completions
sangalan.classify.llm.model=qwen2.5:7b
sangalan.classify.llm.system-prompt=You are an intent classification assistant. Your task is to classify the user request into exactly one of the allowed intent options.
sangalan.classify.llm.temperature=0
sangalan.classify.llm.timeout-ms=30000
sangalan.classify.llm.default-option=UNKNOWN

sangalan.classify.llm.options[0]=SEARCH_DOCUMENTS
sangalan.classify.llm.options[1]=GET_WORKFLOW_STATUS
sangalan.classify.llm.options[2]=GET_USER_TASKS
sangalan.classify.llm.options[3]=UNKNOWN

sangalan.classify.llm.few-shots[0].user=Find all documents approved by Pepe last week
sangalan.classify.llm.few-shots[0].intent=SEARCH_DOCUMENTS
```

## Intent Options With Parameters

Use `intent-options` when an intent has a description and parameters to extract:

```yaml
sangalan:
  classify:
    llm:
      url: "http://localhost:12434/engines/llama.cpp/v1/chat/completions"
      model: "qwen2.5:7b"
      system-prompt: >
        You classify task-management user requests.
      temperature: 0
      timeout-ms: 30000
      default-option: "UNKNOWN"
      intent-options:
        - name: "LIST_USERS_WITH_ACTIVE_TASKS"
          description: "List the users with active tasks"
        - name: "LIST_TASKS_EXPIRING_IN_DAYS"
          description: "List the tasks that will expire in X days"
          parameters:
            - name: "days"
              type: "integer"
              description: "Number of days before expiration"
              required: true
        - name: "LIST_TOP_DOCUMENTS_IN_TASKS"
          description: "List the top 10 documents that are part of tasks most of the time"
          parameters:
            - name: "limit"
              type: "integer"
              description: "Maximum number of documents to return"
              required: false
        - name: "LIST_EXPIRED_TASKS"
          description: "List the expired tasks"
        - name: "LIST_LAST_ASSIGNED_TASKS"
          description: "List the last X tasks assigned"
          parameters:
            - name: "limit"
              type: "integer"
              description: "Number of recent assigned tasks to return"
              required: true
        - name: "LIST_TASKS_ASSIGNED_TO_USER"
          description: "List all the tasks assigned to the user X"
          parameters:
            - name: "user"
              type: "string"
              description: "User name or identifier"
              required: true
        - name: "LIST_TASKS_BY_STATUS_AND_USER"
          description: "List all the tasks with status Y to the user X"
          parameters:
            - name: "status"
              type: "string"
              description: "Task status"
              required: true
            - name: "user"
              type: "string"
              description: "User name or identifier"
              required: true
        - name: "UNKNOWN"
          description: "Use when the request does not match any task intent"
      few-shots:
        - user: "List the tasks that will expire in 7 days"
          intent: "LIST_TASKS_EXPIRING_IN_DAYS"
          parameters:
            days: 7
        - user: "List all the tasks with status pending to the user Maria"
          intent: "LIST_TASKS_BY_STATUS_AND_USER"
          parameters:
            status: "pending"
            user: "Maria"
```

The LLM is instructed to return JSON in this shape:

```json
{
  "intent": "LIST_TASKS_EXPIRING_IN_DAYS",
  "parameters": {
    "days": 7
  }
}
```

If the LLM returns an invalid intent, the parser returns the configured default option with empty parameters.

## Real LLM Integration Test

The project includes a real integration test:

- [LlmRealIntegrationTest.java](/Users/miguel/Projects/sangalan/git/llm/intent-classification/src/test/java/com/sangalan/llm/classification/intent/LlmRealIntegrationTest.java)

By default it is disabled. Enable it only when your model runner is up.

Configuration template:

- [application-integration.properties](/Users/miguel/Projects/sangalan/git/llm/intent-classification/src/test/resources/application-integration.properties)

### Run from IntelliJ

1. Open `LlmRealIntegrationTest` and run that test class.
2. In Run Configuration, set VM options, for example:

```text
-Dllm.integration.enabled=true
-Dllm.integration.url=http://localhost:12434/engines/llama.cpp/v1/chat/completions
-Dllm.integration.model=qwen2.5:7b
-Dllm.integration.user-prompt=List all the tasks with status pending to the user Maria
```

You can also set env vars with the same keys uppercased and with `_`, for example:

```text
LLM_INTEGRATION_ENABLED=true
LLM_INTEGRATION_URL=http://localhost:12434/engines/llama.cpp/v1/chat/completions
LLM_INTEGRATION_MODEL=qwen2.5:7b
```
