package org.example.ai_content_creator_hub.controller;

import jakarta.validation.Valid;
import org.example.ai_content_creator_hub.dto.ImageRequestDto;
import org.example.ai_content_creator_hub.dto.TextRequestDto;
import org.example.ai_content_creator_hub.dto.TextResponseDto;
import org.example.ai_content_creator_hub.dto.google.request.AnalyzeSentimentRequestDto;
import org.example.ai_content_creator_hub.dto.google.response.AnalyzeSentimentResponseDto;
import org.example.ai_content_creator_hub.dto.openai.request.OpenAIImageRequestDto;
import org.example.ai_content_creator_hub.dto.openai.request.OpenAITextRequestDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAIImageResponseDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.service.ai.AWSRekognitionService;
import org.example.ai_content_creator_hub.service.ai.DallEImageService;
import org.example.ai_content_creator_hub.service.ai.GoogleNLPService;
import org.example.ai_content_creator_hub.service.ai.OpenAIService;
import org.example.ai_content_creator_hub.util.AIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for AI-related functionalities, providing endpoints for text generation,
 * conversation handling, text summarization, sentiment analysis, image generation,
 * and image analysis using various third-party AI services.
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    @Value("${openai.api.model}")
    private String openAiModel;
    @Value("${openai.api.image.model}")
    private String openAiImageMode;

    private final OpenAIService openAIService;
    private final GoogleNLPService googleNLPService;
    private final AWSRekognitionService awsRekognitionService;
    private final DallEImageService dallEImageService;

    /**
     * Constructor for AIController.
     *
     * @param openAIService         Service for interacting with OpenAI API.
     * @param googleNLPService      Service for interacting with Google NLP API.
     * @param awsRekognitionService Service for interacting with AWS Rekognition API.
     * @param dallEImageService     Service for interacting with DALL-E image generation API.
     */
    @Autowired
    public AIController(OpenAIService openAIService,
                        GoogleNLPService googleNLPService,
                        AWSRekognitionService awsRekognitionService,
                        DallEImageService dallEImageService
    ) {
        this.openAIService = openAIService;
        this.googleNLPService = googleNLPService;
        this.awsRekognitionService = awsRekognitionService;
        this.dallEImageService = dallEImageService;
    }

    /**
     * Endpoint to start a new conversation using OpenAI.
     *
     * @param user The authenticated user starting the conversation.
     * @return ResponseEntity containing the created Conversation object.
     */
    @PostMapping("/start-conversation")
    public ResponseEntity<TextResponseDto> startConversation(@AuthenticationPrincipal User user) {
        logger.info("Starting conversation for user: {}", user.getUsername());
        Conversation conversation = openAIService.createConversation(user);
        return ResponseEntity.ok(new TextResponseDto(conversation.getId().toString()));
    }

    /**
     * Endpoint to generate text for an ongoing conversation using OpenAI.
     *
     * @param textRequest    The request body containing the text request details.
     * @param user           The authenticated user.
     * @param conversationId The ID of the conversation.
     * @return ResponseEntity containing the generated text response.
     */
    @PostMapping("/do-conversation")
    public ResponseEntity<TextResponseDto> doConversation(@Valid @RequestBody TextRequestDto textRequest,
                                                        @AuthenticationPrincipal User user,
                                                        @RequestParam("conversationId") Long conversationId) {
        logger.info("Generating text for conversation: {} and user {}", conversationId, user.getUsername());
        // Get all conversation messages
        List<GeneratedContent> conversationMessages = openAIService.prepareConversation(textRequest, user, conversationId);
        // Create OpenAI request from conversation messages
        OpenAITextRequestDto openAiRequest = AIUtils.createOpenAIConversationTextRequest(conversationMessages, openAiModel);
        // Generate text using OpenAI
        OpenAITextResponseDto openAiResponse = openAIService.generateConversationText(openAiRequest, user, conversationId);
        TextResponseDto textResponse = AIUtils.getLastOpenAITextResponse(openAiResponse);
        // Store AI response in the conversation
        GeneratedContent aiContent = openAIService.storeGeneratedContentInConversation(
                ContentType.TEXT, ContentRole.ASSISTANT, ContentSource.OPEN_AI, textResponse.getGeneratedText(), user, conversationId);
        return ResponseEntity.ok(textResponse);
    }

    /**
     * Endpoint to generate text using OpenAI.
     * User enters some text and AI generates a 'single' response to that text.
     * Each text generation request is independent and stateless (no use of assistants).
     *
     * @param textRequest The request body containing the text request details.
     * @return ResponseEntity containing the generated text response.
     */
    @PostMapping("/generate-text")
    public ResponseEntity<TextResponseDto> generateText(@Valid @RequestBody TextRequestDto textRequest,
                                                        @AuthenticationPrincipal User user) {
        logger.info("Generating text for user: {}", user.getUsername());
        OpenAITextRequestDto openAiRequest = AIUtils.createOpenAISingleTextRequest(textRequest, openAiModel);
        OpenAITextResponseDto response = openAIService.generateSimpleText(openAiRequest, user, ContentType.TEXT);
        TextResponseDto textResponse = AIUtils.getLastOpenAITextResponse(response); // Get the last message of openAiResponse
        return ResponseEntity.ok(textResponse);
    }

    /**
     * Endpoint to summarize text using OpenAI.
     *
     * @param textRequest The request body containing the text to be summarized.
     * @return ResponseEntity containing the summarized text.
     */
    @PostMapping("/summarize")
    public ResponseEntity<TextResponseDto> summarizeText(@Valid @RequestBody TextRequestDto textRequest,
                                                         @AuthenticationPrincipal User user) {
        logger.info("Summarizing text for user: {}", user.getUsername());
        OpenAITextRequestDto openAiRequest = AIUtils.createOpenAISummarizeTextRequest(textRequest, openAiModel);
        OpenAITextResponseDto response = openAIService.generateSimpleText(openAiRequest, user, ContentType.SUMMARY);
        TextResponseDto textResponse = AIUtils.getLastOpenAITextResponse(response); // Get the last message of openAiResponse
        return ResponseEntity.ok(textResponse);
    }

    /**
     * Endpoint to analyze sentiment of text using Google NLP.
     *
     * @param textRequest The request body containing the text to be analyzed.
     * @return ResponseEntity containing the sentiment analysis result.
     * @throws Exception if an error occurs during sentiment analysis.
     */
    @PostMapping("/analyze-sentiment")
    public ResponseEntity<AnalyzeSentimentResponseDto> analyzeSentiment(@Valid @RequestBody TextRequestDto textRequest,
                                                   @AuthenticationPrincipal User user) {
        logger.info("Analyzing Sentiment for text submitted by user: {}", user.getUsername());
        AnalyzeSentimentRequestDto googleNlpRequest = AIUtils.createGoogleNLPSentimentAnalysisRequest(textRequest);
        AnalyzeSentimentResponseDto response = googleNLPService.analyzeSentiment(googleNlpRequest, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to generate an image using OpenAI's DALL-E API.
     *
     * @param imageRequest The request body containing the image generation details.
     * @return ResponseEntity containing the generated image response.
     */
    @PostMapping("/generate-image")
    public ResponseEntity<OpenAIImageResponseDto> generateImage(@Valid @RequestBody ImageRequestDto imageRequest,
                                           @AuthenticationPrincipal User user) {
        logger.info("Generating Image for text submitted by user: {}",  user.getUsername());
        OpenAIImageRequestDto openAIImageRequest = AIUtils.createOpenAIGenerateImageRequest(imageRequest, openAiImageMode);
        OpenAIImageResponseDto response = dallEImageService.generateImage(openAIImageRequest, user, ContentType.IMAGE);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to analyze an image using AWS Rekognition.
     *
     * @param imageFile The image file to be analyzed.
     * @return ResponseEntity containing the image analysis result.
     * @throws Exception if an error occurs during image analysis.
     */
    @PostMapping("/analyze-image")
    public ResponseEntity<String> analyzeImage(@RequestParam("image") MultipartFile imageFile) {
        String result = awsRekognitionService.analyzeImage(imageFile);
        return ResponseEntity.ok(result);
    }
}
