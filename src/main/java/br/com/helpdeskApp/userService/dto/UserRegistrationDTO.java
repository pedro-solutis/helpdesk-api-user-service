package br.com.helpdeskApp.userService.dto;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotNull(message = "Role is required")
        Role role
) {

        public UserRegistrationDTO(User newUser) {
                this(newUser.getName(), newUser.getEmail(), newUser.getPassword(), newUser.getRole());
        }
}

