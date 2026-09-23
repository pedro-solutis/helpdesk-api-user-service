package br.com.helpdeskApp.userService.controller;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.dto.UserDetailsDTO;
import br.com.helpdeskApp.userService.dto.UserListDTO;
import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest 
@AutoConfigureMockMvc
@AutoConfigureJsonTesters 
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired 
    private JacksonTester<UserRegistrationDTO> userResgistrationDTO;

    @Autowired 
    private JacksonTester<UserUpdateDTO> userUpdateDTO;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 201 (Created) when creating a valid user")
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 (Bad Request) when creating a user with invalid data")
    void testCreateUser_Failure_InvalidData() throws Exception {
        var registrationDTO = new UserRegistrationDTO("", "email-invalido", "", Role.ADMIN);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 (Forbidden) when a non-admin tries to create a user")
    void testCreateUser_Forbidden() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser 
    @DisplayName("Should return 200 (OK) and a paginated list of users")
    void testGetAllUsers_Success() throws Exception {
        var userListDTO = new UserListDTO(1L, "Pedro", "pedro@email.com", Role.ADMIN);
        Page<UserListDTO> page = new PageImpl<>(List.of(userListDTO));

        Mockito.when(userService.getAllUsers(any(), any())).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Pedro"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 200 (OK) when getting a user by ID")
    void testGetUserById_Success() throws Exception {
        var detailsDTO = new UserDetailsDTO(1L, "Pedro", "pedro@email.com", Role.ADMIN, true, LocalDateTime.now());

        Mockito.when(userService.getUserById(1L)).thenReturn(detailsDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pedro"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 (OK) when updating a user with valid data")
    void testUpdateUser_Success() throws Exception {
        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "senha123", "ADMIN");
        var detailsDTO = new UserDetailsDTO(1L, "Pedro Atualizado", "pedro@email.com", Role.ADMIN, true, LocalDateTime.now());

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(detailsDTO);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDTO.write(updateDTO).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 (Bad Request) when updating a user with invalid data")
    void testUpdateUser_Failure_InvalidData() throws Exception {
        var updateDTO = new UserUpdateDTO("", "", "ADMIN");

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDTO.write(updateDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 (Forbidden) when a non-admin tries to update a user")
    void testUpdateUser_Forbidden() throws Exception {
        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "senha123", "ADMIN");

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDTO.write(updateDTO).getJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 204 (No Content) when deleting (deactivating) a user")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 (Forbidden) when a non-admin tries to delete a user")
    void testDeleteUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 (Not Found) when getting all users (simulating db failure)")
    void testGetAllUsers_Failure_NotFound() throws Exception {
        Mockito.when(userService.getAllUsers(any(), any())).thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 (Not Found) when getting a user by non-existent ID")
    void testGetUserById_Failure_NotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 (Not Found) when updating a user with non-existent ID")
    void testUpdateUser_Failure_NotFound() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateDTO.class)))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 (Not Found) when deleting a user with non-existent ID")
    void testDeleteUser_Failure_NotFound() throws Exception {
        Mockito.doThrow(new jakarta.persistence.EntityNotFoundException()).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 409 (Conflict) when creating a user with an already registered email")
    void testCreateUser_Failure_DataConflict() throws Exception {
        var registrationDTO = new UserRegistrationDTO("Pedro", "pedro@email.com", "senha123", Role.ADMIN);

        Mockito.when(userService.createUser(any(UserRegistrationDTO.class)))
                .thenThrow(new br.com.helpdeskApp.userService.infra.exception.DataConflictException("This email is already registered"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userResgistrationDTO.write(registrationDTO).getJson()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 (Bad Request) when updating an inactive user")
    void testUpdateUser_Failure_InactiveUser() throws Exception {
        var updateDTO = new UserUpdateDTO("Pedro Atualizado", "senha123", "ADMIN");

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateDTO.class)))
                .thenThrow(new br.com.helpdeskApp.userService.infra.exception.InactiveUserException("The operation cannot be completed: The user is inactive."));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDTO.write(updateDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 (Bad Request) when deleting an inactive user")
    void testDeleteUser_Failure_InactiveUser() throws Exception {
        Mockito.doThrow(new br.com.helpdeskApp.userService.infra.exception.InactiveUserException("The operation cannot be completed: The user is inactive."))
                .when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser 
    @DisplayName("Should return 200 (OK) and a paginated list of users filtered by role")
    void testGetAllUsers_WithRole_Success() throws Exception {
        var userListDTO = new UserListDTO(1L, "Admin", "admin@email.com", Role.ADMIN);
        Page<UserListDTO> page = new PageImpl<>(List.of(userListDTO));

        Mockito.when(userService.getAllUsers(eq("ADMIN"), any())).thenReturn(page);

        mockMvc.perform(get("/users").param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Admin"));
    }
}
