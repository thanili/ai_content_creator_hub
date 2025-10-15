package org.example.ai_content_creator_hub.service.data;

import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.exception.ConversationNotFoundException;
import org.example.ai_content_creator_hub.repository.ConversationRepository;
import org.example.ai_content_creator_hub.repository.GeneratedContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing content and conversations within the system.
 * This class provides functionality to create and retrieve content, conversations,
 * and their associations with users.
 *
 * The service layer interacts with the persistence layer through the
 * {@code GeneratedContentRepository} and {@code ConversationRepository} interfaces.
 *
 * It also handles transactional operations to manage the creation of entities.
 */
@Service
public class ContentService {
    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

    private final GeneratedContentRepository generatedContentRepository;
    private final ConversationRepository conversationRepository;

    @Autowired
    public ContentService(GeneratedContentRepository generatedContentRepository,
                          ConversationRepository conversationRepository) {
        this.generatedContentRepository = generatedContentRepository;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public GeneratedContent createGeneratedContent(ContentType contentType,
                                                   ContentRole role,
                                                   ContentSource source,
                                                   String content,
                                                   User user,
                                                   Conversation conversation) {
        GeneratedContent newContent = new GeneratedContent();
        newContent.setContentType(contentType);
        newContent.setContentRole(role);
        newContent.setContentSource(source);
        newContent.setContent(content);
        newContent.setGeneratedAt(LocalDateTime.now());
        newContent.setUser(user);
        newContent.setConversation(conversation);
        return generatedContentRepository.save(newContent);
    }

    @Transactional
    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<GeneratedContent> getUserContent(Long userId) {
        return generatedContentRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public Conversation getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    logger.error("Conversation not found: {}", conversationId);
                    return new ConversationNotFoundException("Conversation not found: " + conversationId);
                });
    }

    @Transactional(readOnly = true)
    public List<GeneratedContent> getUserConversationMessagesSorted(Long userId, Long conversationId) {
        return generatedContentRepository.findByUserIdAndConversationIdOrderByGeneratedAtAsc(userId, conversationId);
    }
}
