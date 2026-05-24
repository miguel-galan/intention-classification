package com.sangalan.llm.classification.intent.client;

import java.util.ArrayList;
import java.util.List;

public class OpenAiCompatibleResponse {

    private List<Choice> choices = new ArrayList<>();

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {

        private OpenAiCompatibleRequest.Message message;

        public OpenAiCompatibleRequest.Message getMessage() {
            return message;
        }

        public void setMessage(OpenAiCompatibleRequest.Message message) {
            this.message = message;
        }
    }
}
