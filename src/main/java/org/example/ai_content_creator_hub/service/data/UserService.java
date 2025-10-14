package org.example.ai_content_creator_hub.service.data;

import org.example.ai_content_creator_hub.dto.user.RegisterUserRequestDto;
import org.example.ai_content_creator_hub.dto.user.RegisterUserResponseDto;
import org.example.ai_content_creator_hub.entity.auth.Role;
import org.example.ai_content_creator_hub.entity.auth.RoleName;
import org.example.ai_content_creator_hub.entity.auth.User;
import org.example.ai_content_creator_hub.exception.UserAlreadyExistsException;
import org.example.ai_content_creator_hub.exception.UserNotFoundException;
import org.example.ai_content_creator_hub.repository.auth.RoleRepository;
import org.example.ai_content_creator_hub.repository.auth.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for managing user-related operations, such as creating,
 * registering, and retrieving users.
 *
 * This class interacts with the {@code UserRepository} and {@code RoleRepository}
 * to store and retrieve user and role data. It also leverages a {@code PasswordEncoder}
 * for securely encoding user passwords.
 *
 * The service includes methods for creating users, registering users with validation,
 * and retrieving users by their username with role information.
 *
 * Methods in this service may log error messages and throw custom exceptions, such as
 * {@code UserAlreadyExistsException} and {@code UserNotFoundException}.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password) {
        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));

        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        return userRepository.save(u);
    }

    public RegisterUserResponseDto registerUser(RegisterUserRequestDto registerUserRequestDto) {
        Optional<User> user = userRepository.findByUsername(registerUserRequestDto.getUsername());
        if (user.isPresent()) {
            logger.error("User already exists: {}", registerUserRequestDto.getUsername());
            throw new UserAlreadyExistsException("User already exists: " + registerUserRequestDto.getUsername());
        }

        User u = createUser(registerUserRequestDto.getUsername(), registerUserRequestDto.getPassword());

        RegisterUserResponseDto registerUserResponseDto = new RegisterUserResponseDto();
        registerUserResponseDto.setSuccess(true);
        registerUserResponseDto.setMessage("User created succesfully");

        return registerUserResponseDto;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });
    }
}
