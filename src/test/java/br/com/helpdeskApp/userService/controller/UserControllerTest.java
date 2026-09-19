package br.com.helpdeskApp.userService.controller;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.dto.UserDetailsDTO;
import br.com.helpdeskApp.userService.dto.UserListDTO;
import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest 
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters 
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired 
    private JacksonTester<UserRegistrationDTO> userResgistrationDTO;

    @Test
    @DisplayName("Deve retornar 201 (Created) ao criar um usuário válido")
    void testCreateUser_Success() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);
        var detailsDTO = new UserDetailsDTO(1L, "Pedro", "pedro@email.com", Role.ADMIN, true, LocalDateTime.now());

        Mockito.when(userService.createUser(any(UserRegistrationDTO.class))).thenReturn(detailsDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 (Bad Request) ao tentar criar um usuário com dados inválidos")
    void testCreateUser_Failure_InvalidData() throws Exception {
        // Nome vazio, email inválido e senha vazia
        var registrationDTO = new UserRegistrationDTO("", "email-invalido", "", Role.ADMIN);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 200 (OK) e a lista paginada de usuários")
    void testGetAllUsers_Success() throws Exception {
        var userListDTO = new UserListDTO(1L, "Pedro", "pedro@email.com", Role.ADMIN);
        Page<UserListDTO> page = new PageImpl<>(List.of(userListDTO));

        Mockito.when(userService.getAllUsers(any())).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Pedro"));
    }

    @Test
    @DisplayName("Deve retornar 200 (OK) ao buscar um usuário pelo ID")
    void testGetUserById_Success() throws Exception {
        var detailsDTO = new UserDetailsDTO(1L, "Pedro", "pedro@email.com", Role.ADMIN, true, LocalDateTime.now());

        Mockito.when(userService.getUserById(1L)).thenReturn(detailsDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pedro"));
    }

    @Test
    @DisplayName("Deve retornar 200 (OK) ao atualizar um usuário com dados válidos")
    void testUpdateUser_Success() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro Atualizado", "pedro@email.com", "senha123", Role.ADMIN);
        var detailsDTO = new UserDetailsDTO(1L, "Pedro Atualizado", "pedro@email.com", Role.ADMIN, true, LocalDateTime.now());

        Mockito.when(userService.updateUser(eq(1L), any(UserRegistrationDTO.class))).thenReturn(detailsDTO);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar 400 (Bad Request) ao atualizar um usuário com dados inválidos")
    void testUpdateUser_Failure_InvalidData() throws Exception {
        var registrationDTO = new UserRegistrationDTO("", "email-invalido", "", Role.ADMIN);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 204 (No Content) ao deletar (desativar) um usuário")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 (Not Found) ao buscar todos os usuários (simulando falha no banco)")
    void testGetAllUsers_Failure_NotFound() throws Exception {
        Mockito.when(userService.getAllUsers(any())).thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 (Not Found) ao buscar usuário por ID inexistente")
    void testGetUserById_Failure_NotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 (Not Found) ao atualizar usuário com ID inexistente")
    void testUpdateUser_Failure_NotFound() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);

        Mockito.when(userService.updateUser(eq(1L), any(UserRegistrationDTO.class)))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 (Not Found) ao deletar usuário com ID inexistente")
    void testDeleteUser_Failure_NotFound() throws Exception {
        Mockito.doThrow(new jakarta.persistence.EntityNotFoundException()).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }
}
