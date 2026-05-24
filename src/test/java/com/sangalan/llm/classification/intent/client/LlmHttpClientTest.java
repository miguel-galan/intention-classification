package com.sangalan.llm.classification.intent.client;

import com.sangalan.llm.classification.intent.TestFixtures;
import com.sangalan.llm.classification.intent.config.LlmProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmHttpClientTest {

    @Test
    void sendsOpenAiCompatibleRequestAndExtractsResponseContent() throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody("""
                            {
                              "choices": [
                                {
                                  "message": {
                                    "role": "assistant",
                                    "content": "SEARCH_DOCUMENTS"
                                  }
                                }
                              ]
                            }
                            """));
            server.start();

            LlmProperties properties = TestFixtures.properties();
            properties.setUrl(server.url("/v1/chat/completions").toString());
            LlmHttpClient client = new LlmHttpClient(properties);

            String response = client.sendPrompt("Prompt content");

            assertEquals("SEARCH_DOCUMENTS", response);
            RecordedRequest request = server.takeRequest();
            assertNotNull(request);
            assertEquals("POST", request.getMethod());
            assertEquals("/v1/chat/completions", request.getPath());
            assertEquals("application/json", request.getHeader("Content-Type"));
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"model\":\"qwen2.5:7b\""));
            assertTrue(body.contains("\"content\":\"Prompt content\""));
        }
    }
}
