package org.example.ai_content_creator_hub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextResponseDto {
    @JsonProperty("generatedText")
    private String generatedText;
}
