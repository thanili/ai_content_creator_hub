package org.example.ai_content_creator_hub.service.ai;

import org.example.ai_content_creator_hub.dto.openai.request.OpenAITextRequestDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.exception.AIServiceException;
import org.example.ai_content_creator_hub.exception.ConversationNotFoundException;
import org.example.ai_content_creator_hub.exception.UserNotFoundException;
import org.example.ai_content_creator_hub.service.data.ContentService;
import org.example.ai_content_creator_hub.service.data.UserService;
import org.example.ai_content_creator_hub.util.AIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service class for interacting with the OpenAI API.
 */
@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;
    private final UserService userService;
    private final ContentService contentService;

    /**
     * Constructor for OpenAIService.
     *
     * @param openAiWebClient the WebClient for making API requests
     * @param userService     the service for user-related operations
     * @param contentService  the service for content-related operations
     */
    @Autowired
    public OpenAIService(WebClient openAiWebClient,
                         UserService userService,
                         ContentService contentService) {
        this.openAiWebClient = openAiWebClient;
        this.userService = userService;
        this.contentService = contentService;
    }

    /**
     * Creates a new conversation for the user and stores it in the database.
     *
     * @param username the username for whom to create the conversation
     * @return the created conversation
     * @throws UserNotFoundException if the user is not found
     */
    @Transactional
    public Conversation createConversation(String username) {
        User localUser = userService.findByUsername(username);
        return contentService.createConversation(localUser);
    }

    /**
     * Generates text using the OpenAI API. It stores the generated content in the database.
     *
     * @param request  the request object containing the input data
     * @param username the user's username making the request
     * @return the response from the OpenAI API
     * @throws AIServiceException if an error occurs while communicating with the OpenAI API
     */
    @Transactional
    public OpenAITextResponseDto generateSimpleText(OpenAITextRequestDto request, String username, ContentType contentType) {
        User u = userService.findByUsername(username);
        OpenAITextResponseDto result = callOpenAIAPI(request);
        Conversation conversation = contentService.createConversation(u);
        request.getMessages().stream()
                .filter(m -> m.getRole().equals(ContentRole.USER.getDisplayName()))
                .findFirst()
                .ifPresent(m -> {
                    contentService.createGeneratedContent(
                            contentType,
                            ContentRole.USER,
                            ContentSource.OPEN_AI,
                            m.getContent().get(0).getText(),
                            u,
                            conversation);
                });
        GeneratedContent assistantContent =
                contentService.createGeneratedContent(
                        contentType,
                        ContentRole.ASSISTANT,
                        ContentSource.OPEN_AI,
                        AIUtils.getLastOpenAITextResponse(result).getGeneratedText(),
                        u,
                        conversation);

        return result;
    }

    /**
     * Appends a user turn, calls OpenAI with full history, stores assistant turn,
     * and returns the assistant text.
     */
    @Transactional
    public OpenAITextResponseDto continueConversation(Long conversationId, String username, String userText, String model) {
        var user = userService.findByUsername(username);
        var convo = contentService.getConversation(conversationId);

        // Ensure conversation belongs to caller
        if (!convo.getUser().getId().equals(user.getId())) {
            throw new ConversationNotFoundException("Conversation not found for user: " + conversationId);
        }

        // Append user message
        contentService.createGeneratedContent(
                ContentType.TEXT, ContentRole.USER, ContentSource.OPEN_AI, userText, user, convo);

        // Load history (sorted oldest -> newest)
        var history = contentService.getUserConversationMessagesSorted(user.getId(), conversationId);
        // Build OpenAI request from history
        var request = AIUtils.createOpenAIConversationTextRequest(history, model);
        // Call OpenAI
        var response = callOpenAIAPI(request);

        // Store assistant message
        var assistantText = AIUtils.getLastOpenAITextResponse(response).getGeneratedText();
        contentService.createGeneratedContent(
                ContentType.TEXT, ContentRole.ASSISTANT, ContentSource.OPEN_AI, assistantText, user, convo);

        return response;
    }

    /**
     * Calls the OpenAI API to generate text.
     *
     * @param request the request object containing the input data
     * @return the response from the OpenAI API
     */
    private OpenAITextResponseDto callOpenAIAPI(OpenAITextRequestDto request) {
        return this.openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAITextResponseDto.class)
                .block();
    }
}
