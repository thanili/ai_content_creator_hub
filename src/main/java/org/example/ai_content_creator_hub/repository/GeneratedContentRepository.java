package org.example.ai_content_creator_hub.repository;

import org.example.ai_content_creator_hub.entity.GeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {
    List<GeneratedContent> findByUserId(Long userId);
    List<GeneratedContent> findByUserIdAndConversationId(Long userId, Long conversationId);
}