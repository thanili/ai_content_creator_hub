package org.example.ai_content_creator_hub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageAnalysisResponseDto {
    private String analysisResult;  // For AWS Rekognition results
}
