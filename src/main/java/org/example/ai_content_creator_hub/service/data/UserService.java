package org.example.ai_content_creator_hub.service.data;

import org.example.ai_content_creator_hub.dto.user.RegisterUserRequestDto;
import org.example.ai_content_creator_hub.dto.user.RegisterUserResponseDto;
import org.example.ai_content_creator_hub.entity.auth.Role;
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
        // Get the role from the database
        Optional<Role> userRole = roleRepository.findByName("USER");
        if (!userRole.isPresent()) {
            Role role = new Role();
            role.setName("USER");
            roleRepository.save(role);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(userRole.get());
        return userRepository.save(newUser);
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
