package org.example.ai_content_creator_hub.dto.google.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a document to be analyzed.
 * This class encapsulates the data required for a document in a sentiment analysis request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO {
    /**
     * Document type (text, html, unspecified).
     * This field is required and specifies the type of the document.
     */
    @JsonProperty(value = "type", required = true)
    private DocumentType documentType;
    /**
     * The language of the document (if not specified, the language is automatically detected).
     * Both ISO and BCP-47 language codes are accepted.
     * Language Support lists currently supported languages for each API method.
     * If the language (either specified by the caller or automatically detected) is not supported by the called API method, an INVALID_ARGUMENT error is returned.
     */
    private String languageCode;
    /**
     * The content of the input in string format.
     * Cloud audit logging exempt since it is based on user data.
     */
    private String content;
    /**
     * The Google Cloud Storage URI where the file content is located.
     * This URI must be of the form: gs://bucketName/object_name.
     * For more details, see https://cloud.google.com/storage/docs/reference-uris.
     * NOTE: Cloud Storage object versioning is not supported.
     */
    private String gcsContentUri;
}
