package org.example.ai_content_creator_hub.service.data;

import org.example.ai_content_creator_hub.entity.*;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.exception.ConversationNotFoundException;
import org.example.ai_content_creator_hub.repository.ConversationRepository;
import org.example.ai_content_creator_hub.repository.GeneratedContentRepository;
import org.example.ai_content_creator_hub.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public Conversation createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    public List<GeneratedContent> getUserContent(Long userId) {
        return generatedContentRepository.findByUserId(userId);
    }

    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserId(userId);
    }

    public List<GeneratedContent> getUserConversationMessages(Long userId, Long conversationId) {
        // Throw exception if conversation not found ?
        return generatedContentRepository.findByUserIdAndConversationId(userId, conversationId);
    }

    public Conversation getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    logger.error("Conversation not found: {}", conversationId);
                    return new ConversationNotFoundException("Conversation not found: " + conversationId);
                });
    }
}
