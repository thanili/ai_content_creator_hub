package org.example.ai_content_creator_hub.repository;

import org.example.ai_content_creator_hub.entity.auth.Role;
import org.example.ai_content_creator_hub.entity.auth.RoleName;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.repository.auth.RoleRepository;
import org.example.ai_content_creator_hub.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // use H2, but avoid replacing an explicit database config
@Rollback(value = true)  // Automatically rollback after each test
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByUsername() {
        // Create a new role
        Role role = new Role(RoleName.ROLE_USER);
        roleRepository.save(role);

        // Create a new user with the saved role
        User user = new User("john_doe", "password123", role);
        userRepository.save(user);

        // Retrieve user by username
        Optional<User> retrievedUser = userRepository.findByUsername("john_doe");

        // Assert that the user was found and the properties are correct
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getUsername()).isEqualTo("john_doe");
        assertThat(retrievedUser.get().getRole().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    public void testFindByUsernameNotFound() {
        // Try to find a user that does not exist
        Optional<User> user = userRepository.findByUsername("non_existent_user");

        // Assert that the user is not found
        assertThat(user).isNotPresent();
    }
}
