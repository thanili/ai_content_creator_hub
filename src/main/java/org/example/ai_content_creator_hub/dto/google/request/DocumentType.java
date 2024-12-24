package org.example.ai_content_creator_hub.dto.google.request;

public enum DocumentType {
    TYPE_UNSPECIFIED("TYPE_UNSPECIFIED"),
    PLAIN_TEXT("PLAIN_TEXT"),
    HTML("HTML");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
