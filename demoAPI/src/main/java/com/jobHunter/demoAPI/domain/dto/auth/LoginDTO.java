package com.jobHunter.demoAPI.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotBlank(message = "Username is required!")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters!")
    @Pattern(
            regexp = "^[a-zA-Z0-9_.@]+$",
            message = "Username can only contain letters, numbers, dots, underscores, or @"
    )
    private String username;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, message = "Password must be at least 6 characters long!")
    private String password;
}
