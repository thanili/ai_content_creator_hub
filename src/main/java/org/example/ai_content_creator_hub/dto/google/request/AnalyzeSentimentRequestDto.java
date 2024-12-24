package org.example.ai_content_creator_hub.dto.google.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for analyzing sentiment requests.
 * This class is used to encapsulate the data required to make a sentiment analysis request to the Google API.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzeSentimentRequestDto {
    /**
     * The document to analyze
     * This field is required and contains the content that needs to be analyzed for sentiment.
     */
    @JsonProperty(value="document", required = true)
    private DocumentDTO document;

    /**
     * The encoding type used by the API to calculate sentence offsets for the sentence sentiment.
     * This field is optional and specifies how the text is encoded.
     */
    @JsonProperty("encodingType")
    private TextEncodingType textEncodingType;
}