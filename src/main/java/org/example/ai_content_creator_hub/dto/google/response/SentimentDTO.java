package org.example.ai_content_creator_hub.dto.google.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing sentiment analysis results.
 * This class encapsulates the sentiment score and magnitude returned from a sentiment analysis request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentDTO {
    /**
     * Sentiment score between -1.0 (negative) and 1.0 (positive).
     * This field represents the overall sentiment score of the analyzed text.
     */
    private float score;
    /**
     * A non-negative number in the [0, +inf) range, which represents the absolute magnitude of sentiment regardless of score (positive or negative).
     * This field indicates the strength of the sentiment expressed in the text.
     */
    private float magnitude;
}
