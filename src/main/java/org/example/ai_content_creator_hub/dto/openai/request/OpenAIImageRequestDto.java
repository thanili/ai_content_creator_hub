package org.example.ai_content_creator_hub.dto.openai.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIImageRequestDto {
    /**
     * The model to use for image generation.
     * Default:  dall-e-2
     */
    private String model;
    /**
     * A text description of the desired image(s). The maximum length is 1000 characters for dall-e-2 and 4000 characters for dall-e-3.
     */
    private String prompt;
    /**
     * The size of the generated images.
     * Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2.
     * Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
     */
    @JsonProperty(value="size", required = false)
    private String size;
    /**
     * The number of images to generate.
     * Must be between 1 and 10.
     * For dall-e-3, only n=1 is supported.
     */
    private Integer n;
    /**
     * The format in which the generated images are returned.
     * Must be one of url or b64_json.
     * URLs are only valid for 60 minutes after the image has been generated.
     */
    //@JsonProperty(value="response_format", required = false)
    //private String response_format;
    /**
     * The style of the generated images.
     * Must be one of vivid or natural.
     * Vivid causes the model to lean towards generating hyper-real and dramatic images.
     * Natural causes the model to produce more natural, less hyper-real looking images.
     * This param is only supported for dall-e-3.
     */
    //@JsonProperty(value="response_format", required = false)
    //private String style;

    public void setSize(ImageSize imageSize) {
        this.size = imageSize.getWire();
    }
}
