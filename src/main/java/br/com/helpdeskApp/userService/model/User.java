package br.com.helpdeskApp.userService.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@SQLRestriction ("active = true")
@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User(UserRegistrationDTO user, PasswordEncoder encoder) {
        this.name = user.name();
        this.email = user.email();
        this.password = passwordEncryptation(user.password(), encoder);
        this.role = user.role();
    }

    public void updateUser(UserUpdateDTO userUpdateDTO, PasswordEncoder encoder){
        if(userUpdateDTO.name() != null){
            name = userUpdateDTO.name();
        }
        if(userUpdateDTO.password() != null){
            password = passwordEncryptation(userUpdateDTO.password(), encoder);
        }
        if(userUpdateDTO.role() != null){
            role = Role.valueOf(userUpdateDTO.role());
        }
    }

    public boolean isActive(){
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    private String passwordEncryptation(String password, PasswordEncoder passwordEncoder){
        return passwordEncoder.encode(password);
    }
}
