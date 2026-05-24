package com.sangalan.llm.classification.intent.service;

import com.sangalan.llm.classification.intent.config.FewShotExample;
import com.sangalan.llm.classification.intent.config.IntentOption;
import com.sangalan.llm.classification.intent.config.IntentParameter;
import com.sangalan.llm.classification.intent.config.LlmProperties;

import java.util.Map;

public class IntentPromptBuilder {

    private final LlmProperties properties;

    public IntentPromptBuilder(LlmProperties properties) {
        this.properties = properties;
    }

    public String buildPrompt(String userPrompt) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(properties.getSystemPrompt().trim()).append("\n\n");
        appendAllowedOptions(prompt);
        prompt.append("\n");
        prompt.append("Rules:\n");
        prompt.append("- Return only valid JSON.\n");
        prompt.append("- The JSON must contain exactly these top-level fields: intent, parameters.\n");
        prompt.append("- The intent value must be exactly one allowed option name.\n");
        prompt.append("- The parameters value must be an object.\n");
        prompt.append("- Extract only parameters that are explicitly present or clearly implied by the user request.\n");
        prompt.append("- Do not invent parameter values.\n");
        prompt.append("- Do not explain your answer.\n");
        prompt.append("- Do not include markdown.\n");
        prompt.append("- If the request does not match any option, return ")
                .append(properties.getDefaultOption())
                .append(" with an empty parameters object.\n\n");
        prompt.append("Response format:\n");
        prompt.append("{\"intent\":\"")
                .append(properties.getDefaultOption())
                .append("\",\"parameters\":{}}\n\n");

        if (!properties.getFewShots().isEmpty()) {
            prompt.append("Examples:\n\n");
            for (FewShotExample example : properties.getFewShots()) {
                prompt.append("User: ").append(example.getUser()).append("\n");
                prompt.append("Response: {\"intent\":\"")
                        .append(example.getIntent())
                        .append("\",\"parameters\":");
                appendParametersObject(prompt, example.getParameters());
                prompt.append("}\n\n");
            }
        }

        prompt.append("Now classify this user request:\n\n");
        prompt.append("User: ").append(userPrompt == null ? "" : userPrompt).append("\n");
        prompt.append("Response:");
        return prompt.toString();
    }

    private void appendAllowedOptions(StringBuilder prompt) {
        if (!properties.getIntentOptions().isEmpty()) {
            prompt.append("Allowed options:\n");
            for (IntentOption option : properties.getIntentOptions()) {
                prompt.append("- ").append(option.getName());
                if (option.getDescription() != null && !option.getDescription().isBlank()) {
                    prompt.append(": ").append(option.getDescription());
                }
                prompt.append("\n");
                if (!option.getParameters().isEmpty()) {
                    prompt.append("  Parameters:\n");
                    for (IntentParameter parameter : option.getParameters()) {
                        prompt.append("  - ").append(parameter.getName())
                                .append(" (").append(parameter.getType()).append(")");
                        if (parameter.isRequired()) {
                            prompt.append(" required");
                        }
                        if (parameter.getDescription() != null && !parameter.getDescription().isBlank()) {
                            prompt.append(": ").append(parameter.getDescription());
                        }
                        prompt.append("\n");
                    }
                }
            }
            return;
        }

        prompt.append("Allowed options:\n");
        for (String option : properties.getOptions()) {
            prompt.append("- ").append(option).append("\n");
        }
    }

    private void appendParametersObject(StringBuilder prompt, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            prompt.append("{}");
            return;
        }

        prompt.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (!first) {
                prompt.append(",");
            }
            first = false;
            prompt.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof Number || value instanceof Boolean) {
                prompt.append(value);
            } else {
                prompt.append("\"").append(String.valueOf(value)).append("\"");
            }
        }
        prompt.append("}");
    }
}
