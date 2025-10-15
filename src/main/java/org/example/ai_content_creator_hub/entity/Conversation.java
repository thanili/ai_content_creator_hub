package org.example.ai_content_creator_hub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.ai_content_creator_hub.entity.auth.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a conversation between a user and the platform.
 * A conversation consists of messages and is associated with a specific user.
 * <p>
 * This entity is mapped to the "conversation" table in the database.
 */
@Table(name = "conversation",
        indexes = {
                @Index(name = "idx_convo_user", columnList = "user_id"),
                @Index(name = "idx_convo_created_at", columnList = "createdAt")
        })
@Getter
@Setter
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedContent> messages = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}