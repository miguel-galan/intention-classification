package com.sangalan.llm.classification.intent.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class OpenAiCompatibleRequest {

    private String model;
    private double temperature;
    private List<Message> messages;

    public OpenAiCompatibleRequest() {
    }

    public OpenAiCompatibleRequest(String model, double temperature, List<Message> messages) {
        this.model = model;
        this.temperature = temperature;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {

        private String role;
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
