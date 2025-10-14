package org.example.ai_content_creator_hub.repository.auth;

import org.example.ai_content_creator_hub.entity.auth.Role;
import org.example.ai_content_creator_hub.entity.auth.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}