package org.example.ai_content_creator_hub.entity;

public enum ContentSource {
    OPEN_AI("OpenAI"),
    GOOGLE_NLP("GoogleNLP");

    private final String displayName;

    ContentSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
