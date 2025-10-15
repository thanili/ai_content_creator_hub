package org.example.ai_content_creator_hub.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserRequestDto {
    @NotNull(message = "Username can not be null")
    @JsonProperty(required = true)
    private String username;
    @NotNull(message = "Password can not be null")
    @JsonProperty(required = true)
    private String password;
    private String role;
}
