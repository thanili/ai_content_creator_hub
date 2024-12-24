package org.example.ai_content_creator_hub.dto.google.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentenceDTO {
    /**
     * The sentence text content.
     */
    private String textContent;
    /**
     * For calls to [AnalyzeSentiment][] or
     * if AnnotateTextRequest.Features.extract_document_sentiment is set to true, this field will contain the sentiment for the sentence.
     */
    private SentimentDTO sentiment;
}
