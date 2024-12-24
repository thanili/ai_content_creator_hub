package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIUsage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
    private OpenAIPromptTokensDetails prompt_tokens_details;
    private OpenAICompletionTokensDetails completion_tokens_details;
}
