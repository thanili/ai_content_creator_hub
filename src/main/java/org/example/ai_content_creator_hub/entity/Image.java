package org.example.ai_content_creator_hub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.ai_content_creator_hub.entity.auth.User;

import java.time.LocalDateTime;

/**
 * The Image class represents an entity for storing information
 * about images that are associated with a specific user.
 * It includes details about the text input used to generate the image,
 * the URL of the generated image, the upload timestamp, and the user who uploaded it.
 *
 * This class is annotated as a JPA entity and includes the necessary mappings
 * for database persistence.
 */
@Getter
@Setter
@ToString
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String inputText;

    @Column(nullable = false)
    private String imageUrl;

    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
