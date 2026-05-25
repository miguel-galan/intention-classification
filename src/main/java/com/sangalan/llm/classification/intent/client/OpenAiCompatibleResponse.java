package com.sangalan.llm.classification.intent.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiCompatibleResponse {

    private List<Choice> choices = new ArrayList<>();

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {

        private OpenAiCompatibleRequest.Message message;
        private String text;

        public OpenAiCompatibleRequest.Message getMessage() {
            return message;
        }

        public void setMessage(OpenAiCompatibleRequest.Message message) {
            this.message = message;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
