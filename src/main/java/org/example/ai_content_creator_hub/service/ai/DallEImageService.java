package org.example.ai_content_creator_hub.service.ai;

import org.example.ai_content_creator_hub.dto.google.request.AnalyzeSentimentRequestDto;
import org.example.ai_content_creator_hub.dto.google.response.AnalyzeSentimentResponseDto;
import org.example.ai_content_creator_hub.dto.openai.request.OpenAIImageRequestDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAIImageDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAIImageResponseDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.entity.ContentType;
import org.example.ai_content_creator_hub.entity.Image;
import org.example.ai_content_creator_hub.service.ImageService;
import org.example.ai_content_creator_hub.service.data.ContentService;
import org.example.ai_content_creator_hub.service.data.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class DallEImageService {
    private static final Logger logger = LoggerFactory.getLogger(DallEImageService.class);

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final WebClient openAiWebClient;
    private final UserService userService;
    private final ImageService imageService;

    /**
     * Constructor for OpenAIService.
     *
     * @param openAiWebClient the WebClient for making API requests
     * @param userService     the service for user-related operations
     * @param imageService  the service for content-related operations
     */
    @Autowired
    public DallEImageService(WebClient openAiWebClient,
                         UserService userService,
                             ImageService imageService) {
        this.openAiWebClient = openAiWebClient;
        this.userService = userService;
        this.imageService = imageService;
    }

    public OpenAIImageResponseDto generateImage(OpenAIImageRequestDto imageRequest, User user, ContentType contentType) {
        org.example.ai_content_creator_hub.entity.auth.User u = userService.findByUsername(user.getUsername());
        OpenAIImageResponseDto imageResponse = callOpenAIGenerateImageApi(imageRequest);

        for(OpenAIImageDto img: imageResponse.getImages()) {
            // Store in the database
            Image image = new Image();
            image.setInputText(imageRequest.getPrompt());
            image.setImageUrl(img.getUrl());
            image.setUser(u);
            image.setUploadedAt(LocalDateTime.now());

            imageService.createImage(image);
        }



        return imageResponse;
    }

    /**
     * Calls the OpenAI generate image API endpoint.
     *
     * @param request the request object containing the input data
     * @return the response from the Google Cloud NPL API
     */
    private OpenAIImageResponseDto callOpenAIGenerateImageApi(OpenAIImageRequestDto request) {
        return this.openAiWebClient.post()
                .uri("/images/generations")
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAIImageResponseDto.class)
                .block();
    }
}