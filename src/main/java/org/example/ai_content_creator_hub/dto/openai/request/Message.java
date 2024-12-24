package org.example.ai_content_creator_hub.dto.openai.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private String role;
    private List<Content> content;
}