package org.example.ai_content_creator_hub.entity;

public enum ContentType {
    TEXT("text"),
    SUMMARY("summary"),
    SENTIMENT_ANALYSIS("sentiment analysis"),
    IMAGE("image");

    private final String displayName;

    ContentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
