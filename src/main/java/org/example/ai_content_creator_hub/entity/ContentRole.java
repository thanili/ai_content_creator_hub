package org.example.ai_content_creator_hub.entity;

public enum ContentRole {
    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant");

    private final String displayName;

    ContentRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}