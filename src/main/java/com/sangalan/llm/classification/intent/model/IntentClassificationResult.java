package com.sangalan.llm.classification.intent.model;

import java.util.Map;

public record IntentClassificationResult(String intent, Map<String, Object> parameters) {
}
