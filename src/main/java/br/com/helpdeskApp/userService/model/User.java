package br.com.helpdeskApp.userService.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@Setter  
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User(UserRegistrationDTO user) {
        this.name = user.name();
        this.email = user.email();
        this.role = user.role();
    }
}
