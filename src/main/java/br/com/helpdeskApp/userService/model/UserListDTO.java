package br.com.helpdeskApp.userService.model;

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

