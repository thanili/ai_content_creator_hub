package org.example.ai_content_creator_hub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextRequestDto {
    @NotNull(message = "Input text cannot be null")
    @JsonProperty(value = "inputText", required = true)
    private String inputText;
}
