package org.example.ai_content_creator_hub.dto.openai.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {
    private String type;
    private String text;
}