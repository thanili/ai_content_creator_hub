package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAICompletionTokensDetails {
    private int reasoning_tokens;
}
