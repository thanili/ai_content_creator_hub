package org.example.ai_content_creator_hub.dto.openai.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAITextRequestDto {
    private String model;
    private List<Message> messages;
    /**
     * Limits the length of the summary. The maximum number of tokens to generate.
     * Requests can use up to 4096 tokens shared between prompt and completion.
     */
    @JsonProperty(value="max_tokens")
    private Integer maxTokens;
    /**
     * Controls the creativity/randomness of the output.
     * Lower temperature makes the model less random by reducing the variety in token selection.
     * For summarization tasks, keep the temperature low (e.g., 0.2-0.5) to get more focused and deterministic summaries.
     */
    private Double temperature;
    /**
     * Related to nucleus sampling.
     * It controls the diversity of the model's response by restricting the pool of possible tokens to only the highest-probability candidates.
     * It's a probability cutoff.
     *
     * A top_p value of 1 means the model considers all possible token choices (basically unconstrained sampling).
     * Lowering top_p (e.g., to 0.3 or 0.5) makes the model more focused and deterministic, as it will only sample from the tokens with the highest cumulative probability (e.g., the top 30% or 50%).
     */
    @JsonProperty(value="top_p")
    private Double toP;
    /**
     * Number of completions you want to receive from the model for a single request.
     * Example: If n is set to 1, you’ll get one summary. If n is set to 3, the API will return three separate summaries, and you can choose which one is best.
     */
    private Integer n;
}