package org.example.ai_content_creator_hub.dto.google.request;

public enum TextEncodingType {
    NONE("NONE"),
    UTF8("UTF8"),
    UTF16("UTF16"),
    UTF32("UTF32");

    private final String displayName;

    TextEncodingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
