package br.com.helpdeskApp.userService.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;

class UserTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0) + "-criptografada");
    }

    @Test
    @DisplayName("Should initialize User entity correctly through constructor")
    void testUserCreation() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.CLIENT);
        
        User user = new User(registrationDTO, passwordEncoder);

        assertEquals("Pedro", user.getName());
        assertEquals("pedro@email.com", user.getEmail());
        assertEquals("senha123-criptografada", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Should update user data when new values are provided")
    void testUpdateUser_AllFields() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.CLIENT);
        User user = new User(registrationDTO, passwordEncoder);

        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "novasenha", "ADMIN");
        
        user.updateUser(updateDTO, passwordEncoder);

        assertEquals("Pedro Atualizado", user.getName());
        assertEquals("pedro@email.com", user.getEmail());
        assertEquals("novasenha-criptografada", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should not update user data if null fields are sent")
    void testUpdateUser_IgnoreNullFields() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.CLIENT);
        User user = new User(registrationDTO, passwordEncoder);

        var updateDTO = new UserUpdateDTO(null, null, null);
        
        user.updateUser(updateDTO, passwordEncoder);

        assertEquals("Pedro", user.getName());
        assertEquals("senha123-criptografada", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
    }

    @Test
    @DisplayName("Should deactivate user correctly")
    void testDeactivate() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.CLIENT);
        User user = new User(registrationDTO, passwordEncoder);

        assertTrue(user.isActive());
        
        user.deactivate();
        
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should return authorities correctly based on Role")
    void testGetAuthorities() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.TECHNICIAN);
        User user = new User(registrationDTO, passwordEncoder);

        var authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_TECHNICIAN", authorities.iterator().next().getAuthority());
    }
}

