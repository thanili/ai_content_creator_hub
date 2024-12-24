package org.example.ai_content_creator_hub.dto.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIImageDto {
    private String b64_json;
    private String url;
    private String revised_prompt;
}
