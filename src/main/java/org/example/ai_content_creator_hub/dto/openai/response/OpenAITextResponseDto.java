package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAITextResponseDto {
    private String id;
    private String object;
    private long created;
    private String model;
    @JsonProperty("choices")
    private List<OpenAIChoice> choices;
    @JsonProperty("usage")
    private OpenAIUsage usage;
    private String system_fingerprint;
}
