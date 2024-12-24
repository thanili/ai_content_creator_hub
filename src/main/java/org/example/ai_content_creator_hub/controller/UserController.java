package org.example.ai_content_creator_hub.controller;

import jakarta.validation.Valid;
import org.example.ai_content_creator_hub.dto.user.RegisterUserRequestDto;
import org.example.ai_content_creator_hub.dto.user.RegisterUserResponseDto;
import org.example.ai_content_creator_hub.service.data.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController is a REST controller that handles user-related API operations.
 * Primarily, it provides an endpoint for user registration.
 * This class delegates user processing logic to the UserService.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user by processing the provided user registration details.
     * Delegates the user registration logic to the UserService.
     *
     * @param registerUserRequestDto the user registration request containing username, password, and optional role
     * @return a ResponseEntity wrapping a RegisterUserResponseDto indicating the registration success and a message
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(@Valid @RequestBody RegisterUserRequestDto registerUserRequestDto) {
        RegisterUserResponseDto response = userService.registerUser(registerUserRequestDto);
        return ResponseEntity.ok(response);
    }
}
