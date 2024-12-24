package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIChoice {
    private int index;
    private OpenAIMessage message;
    //private Object logprobs;
    private String finish_reason;
}
