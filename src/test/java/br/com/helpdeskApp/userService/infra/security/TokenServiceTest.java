package br.com.helpdeskApp.userService.infra.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.model.User;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-test-secret");
    }

    @Test
    @DisplayName("Deve gerar um token válido para um usuário")
    void testGenerateToken_Success() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("pedro@email.com");
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.ADMIN);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao falhar na geração do token (ex: secret nulo)")
    void testGenerateToken_Failure() {
        ReflectionTestUtils.setField(tokenService, "secret", null);

        User user = mock(User.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenService.generateToken(user));
        assertEquals("Error generating token", exception.getMessage());
    }

    @Test
    @DisplayName("Deve recuperar o subject (email) de um token válido")
    void testGetSubject_Success() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("pedro@email.com");
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.ADMIN);

        String token = tokenService.generateToken(user);

        String subject = tokenService.getSubject(token);

        assertEquals("pedro@email.com", subject);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar extrair subject de um token inválido")
    void testGetSubject_Failure_InvalidToken() {
        String invalidToken = "isso.nao.e-um-token-valido";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tokenService.getSubject(invalidToken));
        assertEquals("Invalid or expired token", exception.getMessage());
    }
}

