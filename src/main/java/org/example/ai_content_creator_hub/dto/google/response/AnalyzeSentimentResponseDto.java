package org.example.ai_content_creator_hub.dto.google.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) for analyzing sentiment responses.
 * This class encapsulates the data returned from a sentiment analysis request to the Google API.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzeSentimentResponseDto {
    /**
     * The overall sentiment of the input document.
     * This field contains the sentiment analysis result for the entire document.
     */
    private SentimentDTO documentSentiment;
    /**
     * The language of the text.
     * Will be the same as the language specified in the request or, if not specified, the automatically-detected language.
     */
    private String languageCode;
    /**
     * The sentences in the input document, each with its own sentiment score.
     * This field contains a list of sentences with individual sentiment analysis results.
     */
    private List<SentenceDTO> sentences;
    /**
     * Whether the language is officially supported.
     * The API may still return a response when the language is not supported, but it is on a best effort basis.
     */
    private boolean languageSupported;
}
