package org.example.ai_content_creator_hub.service.ai;

import org.example.ai_content_creator_hub.dto.google.request.AnalyzeSentimentRequestDto;
import org.example.ai_content_creator_hub.dto.google.response.AnalyzeSentimentResponseDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.service.data.ContentService;
import org.example.ai_content_creator_hub.service.data.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleNLPService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleNLPService.class);

    @Value("${google.api.key}")
    private String googleApiKey;

    private final WebClient googleWebClient;
    private final UserService userService;
    private final ContentService contentService;

    @Autowired
    public GoogleNLPService(WebClient googleWebClient,
                            UserService userService,
                            ContentService contentService) {
        this.googleWebClient = googleWebClient;
        this.userService = userService;
        this.contentService = contentService;
    }

    public AnalyzeSentimentResponseDto analyzeSentiment(AnalyzeSentimentRequestDto analyzeSentimentRequestDto, User user) {
        org.example.ai_content_creator_hub.entity.auth.User u = userService.findByUsername(user.getUsername());
        AnalyzeSentimentResponseDto result = callGoogleCloudNlpAPI(analyzeSentimentRequestDto);
        Conversation conversation = contentService.createConversation(u);

        GeneratedContent userContent = contentService.createGeneratedContent(
                ContentType.SENTIMENT_ANALYSIS,
                ContentRole.USER,
                ContentSource.GOOGLE_NLP,
                analyzeSentimentRequestDto.getDocument().getContent(),
                u,
                conversation
        );

        GeneratedContent aiContent = contentService.createGeneratedContent(
                ContentType.SENTIMENT_ANALYSIS,
                ContentRole.ASSISTANT,
                ContentSource.GOOGLE_NLP,
                "score: " + result.getDocumentSentiment().getScore() + ", magnitude: " + result.getDocumentSentiment().getMagnitude(),
                u,
                conversation
        );

        return result;
    }

    /**
     * Calls the Google Cloud NLP API to Analyze sentiment of text.
     *
     * @param request the request object containing the input data
     * @return the response from the Google Cloud NPL API
     */
    private AnalyzeSentimentResponseDto callGoogleCloudNlpAPI(AnalyzeSentimentRequestDto request) {
        return this.googleWebClient.post()
                .uri("/documents:analyzeSentiment?key=" + googleApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnalyzeSentimentResponseDto.class)
                .block();
    }
}
