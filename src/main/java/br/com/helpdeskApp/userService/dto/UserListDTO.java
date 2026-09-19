package br.com.helpdeskApp.userService.dto;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.model.User;

public record UserListDTO(
        Long id,
        String name,
        String email,
        Role role
) {
    public UserListDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}

