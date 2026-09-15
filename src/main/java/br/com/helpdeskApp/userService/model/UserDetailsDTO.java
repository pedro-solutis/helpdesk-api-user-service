package br.com.helpdeskApp.userService.model;

import java.time.LocalDateTime;

public record UserDetailsDTO(
        Long id,
        String name,
        String email,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
    // Construtor auxiliar para facilitar a conversão da entidade User para o DTO
    public UserDetailsDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}

