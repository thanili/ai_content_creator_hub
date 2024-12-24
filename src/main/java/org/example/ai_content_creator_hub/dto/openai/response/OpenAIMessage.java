package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIMessage {
    private String role;
    private String content;
    private String refusal;
}
