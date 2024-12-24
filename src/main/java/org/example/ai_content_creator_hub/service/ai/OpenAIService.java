package org.example.ai_content_creator_hub.service.ai;

import org.example.ai_content_creator_hub.dto.TextRequestDto;
import org.example.ai_content_creator_hub.dto.openai.request.OpenAITextRequestDto;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.exception.OpenAIServiceException;
import org.example.ai_content_creator_hub.exception.UserNotFoundException;
import org.example.ai_content_creator_hub.service.data.ContentService;
import org.example.ai_content_creator_hub.service.data.UserService;
import org.example.ai_content_creator_hub.util.AIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Service class for interacting with the OpenAI API.
 */
@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api.key}")
    private String openAiApiKey;

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
     * @param user the user for whom to create the conversation
     * @return the created conversation
     * @throws UserNotFoundException if the user is not found
     */
    public Conversation createConversation(User user) {
        org.example.ai_content_creator_hub.entity.auth.User localUser = userService.findByUsername(user.getUsername());
        return contentService.createConversation(localUser);
    }

    /**
     * Prepares the conversation by adding the user's message to the conversation.
     *
     * @param textRequest    the text request details
     * @param user           the authenticated user
     * @param conversationId the ID of the conversation
     * @return a list of generated content for the conversation
     */
    public List<GeneratedContent> prepareConversation(TextRequestDto textRequest, User user, Long conversationId) {
        logger.info("Generating text for conversation: {}", conversationId);
        // Get the current user
        org.example.ai_content_creator_hub.entity.auth.User localUser = userService.findByUsername(user.getUsername());
        // Get the conversation from database
        Conversation conversation = contentService.getConversation(conversationId);
        // Get the conversation messages from database
        List<GeneratedContent> conversationMessages = contentService.getUserConversationMessages(localUser.getId(), conversationId);
        // Store the user message in the conversation
        GeneratedContent userContent = contentService
                .createGeneratedContent(ContentType.TEXT, ContentRole.USER, ContentSource.OPEN_AI, textRequest.getInputText(), localUser, conversation);
        // Add the current user message to the conversation messages
        conversationMessages.add(userContent);
        return conversationMessages;
    }

    /**
     * Generates text for an ongoing conversation using the OpenAI API.
     *
     * @param request        the request object containing the input data
     * @param user           the authenticated user
     * @param conversationId the ID of the conversation
     * @return the response from the OpenAI API
     */
    public OpenAITextResponseDto generateConversationText(OpenAITextRequestDto request, User user, Long conversationId) {
        org.example.ai_content_creator_hub.entity.auth.User u = userService.findByUsername(user.getUsername());
        return callOpenAIAPI(request);
    }

    /**
     * Generates text using the OpenAI API. It stores the generated content in the database.
     *
     * @param request the request object containing the input data
     * @param user    the user making the request
     * @return the response from the OpenAI API
     * @throws OpenAIServiceException if an error occurs while communicating with the OpenAI API
     */
    public OpenAITextResponseDto generateSimpleText(OpenAITextRequestDto request, User user, ContentType contentType) {
        org.example.ai_content_creator_hub.entity.auth.User u = userService.findByUsername(user.getUsername());
        OpenAITextResponseDto result = callOpenAIAPI(request);
        Conversation conversation = contentService.createConversation(u);
        request.getMessages().stream().filter(m -> m.getRole().equals(ContentRole.USER.getDisplayName())).findFirst().ifPresent(m -> {
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
     * Stores the generated content in the conversation.
     *
     * @param contentType    the type of content being generated
     * @param contentRole    the role of the content (e.g., user, assistant)
     * @param contentSource  the source of the content (e.g., OpenAI)
     * @param generatedText  the generated text
     * @param user           the authenticated user
     * @param conversationId the ID of the conversation
     * @return the stored generated content
     */
    public GeneratedContent storeGeneratedContentInConversation(
            ContentType contentType,
            ContentRole contentRole,
            ContentSource contentSource,
            String generatedText,
            User user,
            Long conversationId) {
        // Get the current user
        org.example.ai_content_creator_hub.entity.auth.User localUser = userService.findByUsername(user.getUsername());
        // Get the conversation from database
        Conversation conversation = contentService.getConversation(conversationId);
        // Store AI response in the conversation
        return contentService.createGeneratedContent(contentType, contentRole, contentSource, generatedText, localUser, conversation);
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
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAITextResponseDto.class)
                .block();
    }
}
