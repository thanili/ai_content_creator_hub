package org.example.ai_content_creator_hub.repository;

import org.example.ai_content_creator_hub.entity.auth.Role;
import org.example.ai_content_creator_hub.repository.auth.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Use H2 database for testing
@Rollback(value = true)  // Ensure tests are rolled back to keep them isolated
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveRole() {
        // Create a new role
        Role role = new Role("ROLE_ADMIN");
        Role savedRole = roleRepository.save(role);

        // Assert that the role was saved and has a valid ID
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    public void testFindRoleByName() {
        // Create and save a role
        Role role = new Role("ROLE_MANAGER");
        roleRepository.save(role);

        // Retrieve the role by its name
        Optional<Role> retrievedRole = roleRepository.findByName("ROLE_MANAGER");

        // Assert that the role was found and its name is correct
        assertThat(retrievedRole).isNotNull();
        assertThat(retrievedRole.get().getName()).isEqualTo("ROLE_MANAGER");
    }
}
