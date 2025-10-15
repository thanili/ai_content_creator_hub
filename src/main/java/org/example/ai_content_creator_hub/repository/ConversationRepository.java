package org.example.ai_content_creator_hub.repository;

import org.example.ai_content_creator_hub.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserId(Long userId);

    List<Conversation> findByUser_Id(Long userId);
}