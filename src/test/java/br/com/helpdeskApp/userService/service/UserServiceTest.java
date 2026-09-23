package br.com.helpdeskApp.userService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.helpdeskApp.userService.dto.UserDetailsDTO;
import br.com.helpdeskApp.userService.dto.UserListDTO;
import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;
import br.com.helpdeskApp.userService.infra.exception.DataConflictException;
import br.com.helpdeskApp.userService.infra.exception.InactiveUserException;
import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve criar um usuário com sucesso quando o e-mail não estiver em uso")
    void testCreateUser_Success() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);
        
        when(userRepository.existsByEmail(registrationDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(registrationDTO.password())).thenReturn("senha-criptografada");

        UserDetailsDTO result = userService.createUser(registrationDTO);

        assertNotNull(result);
        assertEquals("Pedro", result.name());
        assertEquals("pedro@email.com", result.email());
        assertEquals(Role.ADMIN, result.role());

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar DataConflictException ao criar usuário com e-mail já existente")
    void testCreateUser_Failure_EmailConflict() {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);
        
        when(userRepository.existsByEmail(registrationDTO.email())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.createUser(registrationDTO));

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar uma página de usuários ativos")
    void testGetAllUsers_Success() {
        User user = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAllByActiveTrue(pageable)).thenReturn(page);

        Page<UserListDTO> result = userService.getAllUsers(null,pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Pedro", result.getContent().get(0).name());
        verify(userRepository, times(1)).findAllByActiveTrue(pageable);
    }

    @Test
    @DisplayName("Deve retornar o usuário por ID quando encontrado")
    void testGetUserById_Success() {
        User user = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDetailsDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Pedro", result.name());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar usuário com ID inexistente")
    void testGetUserById_Failure_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve atualizar o usuário com sucesso")
    void testUpdateUser_Success() {
        User existingUser = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "novasenha", "TECHNICIAN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateDTO.password())).thenReturn("nova-senha-criptografada");

        UserDetailsDTO result = userService.updateUser(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Pedro Atualizado", result.name());
        assertEquals(Role.TECHNICIAN, result.role());
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Deve lançar InactiveUserException ao atualizar usuário inativo")
    void testUpdateUser_Failure_Inactive() {
        User existingUser = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        existingUser.deactivate();
        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "novasenha", "TECHNICIAN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(InactiveUserException.class, () -> userService.updateUser(1L, updateDTO));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar logicamente o usuário com sucesso")
    void testDeleteUser_Success() {
        User existingUser = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1L);

        assertFalse(existingUser.isActive());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Deve lançar InactiveUserException ao tentar deletar um usuário já inativo")
    void testDeleteUser_Failure_Inactive() {
        User existingUser = new User(new UserRegistrationDTO("Pedro", "pedro@email.com", "senha", Role.ADMIN), passwordEncoder);
        existingUser.deactivate();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(InactiveUserException.class, () -> userService.deleteUser(1L));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("Deve retornar uma página de usuários filtrados por role")
    void testGetAllUsers_WithRole_Success() {
        User user = new User(new UserRegistrationDTO("Admin", "admin@email.com", "senha", Role.ADMIN), passwordEncoder);
        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.getAllByRole(Role.ADMIN, pageable)).thenReturn(page);

        Page<UserListDTO> result = userService.getAllUsers("ADMIN", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Admin", result.getContent().get(0).name());
        verify(userRepository, times(1)).getAllByRole(Role.ADMIN, pageable);
    }
}

