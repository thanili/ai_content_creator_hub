package org.example.ai_content_creator_hub.init;


import org.example.ai_content_creator_hub.entity.auth.Role;
import org.example.ai_content_creator_hub.entity.auth.RoleName;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.repository.ConversationRepository;
import org.example.ai_content_creator_hub.repository.GeneratedContentRepository;
import org.example.ai_content_creator_hub.repository.ImageRepository;
import org.example.ai_content_creator_hub.repository.auth.RoleRepository;
import org.example.ai_content_creator_hub.repository.auth.UserRepository;
import org.example.ai_content_creator_hub.service.ai.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DatabasePopulator {
    private static final Logger logger = LoggerFactory.getLogger(DatabasePopulator.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GeneratedContentRepository generatedContentRepository;
    private final ConversationRepository conversationRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabasePopulator(UserRepository userRepository,
                             RoleRepository roleRepository,
                             GeneratedContentRepository generatedContentRepository,
                             ConversationRepository conversationRepository,
                             ImageRepository imageRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.generatedContentRepository = generatedContentRepository;
        this.conversationRepository = conversationRepository;
        this.imageRepository = imageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void populateApiUsers() {
        // Check for the 'ADMIN' role, and create it if it doesn't exist
        Optional<Role> optionalRoleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN);
        Role roleAdmin = optionalRoleAdmin.orElseGet(() -> {
            Role newRole = new Role(RoleName.ROLE_ADMIN);
            return roleRepository.save(newRole);
        });

        // Check for the 'USER' role, and create it if it doesn't exist
        Optional<Role> optionalRoleUser = roleRepository.findByName(RoleName.ROLE_USER);
        Role roleUser = optionalRoleUser.orElseGet(() -> {
            Role newRole = new Role(RoleName.ROLE_USER);
            return roleRepository.save(newRole);
        });

        // Check if admin and user accounts already exist
        Optional<User> adminExists = userRepository.findByUsername("admin");
        if (adminExists.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEnabled(true);
            admin.setRole(roleAdmin);
            userRepository.save(admin);
        }

        Optional<User> userExists = userRepository.findByUsername("user");
        if (userExists.isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setEnabled(true);
            user.setRole(roleUser);
            userRepository.save(user);
        }
    }

    @Transactional
    public void clearDatabase() {
        generatedContentRepository.deleteAll();
        conversationRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        logger.info("Database cleared.");
    }
}
