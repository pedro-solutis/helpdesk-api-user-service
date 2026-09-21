package br.com.helpdeskApp.userService.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.helpdeskApp.userService.dto.UserLoginDTO;
import br.com.helpdeskApp.userService.infra.security.TokenService;
import br.com.helpdeskApp.userService.model.User;

@SpringBootTest 
@AutoConfigureMockMvc
@AutoConfigureJsonTesters 
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private JacksonTester<UserLoginDTO> userLoginDTO;

    @Test
    @DisplayName("Should return 200 (OK) and the JWT token when authenticating with valid credentials")
    void testAuthenticate_Success() throws Exception {
        var loginDTO = new UserLoginDTO("pedro@email.com", "senha123");
        
        User mockUser = new User();
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);
        
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        Mockito.when(tokenService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginDTO.write(loginDTO).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    @DisplayName("Should return 401 (Unauthorized) when sending incorrect credentials (BadCredentialsException)")
    void testAuthenticate_Failure_BadCredentials() throws Exception {
        var loginDTO = new UserLoginDTO("pedro@email.com", "senha-errada");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginDTO.write(loginDTO).getJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Bad Credentials"));
    }

    @Test
    @DisplayName("Should return 400 (Bad Request) when sending invalid data")
    void testAuthenticate_Failure_InvalidData() throws Exception {
        var loginDTO = new UserLoginDTO("email-invalido", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginDTO.write(loginDTO).getJson()))
                .andExpect(status().isBadRequest());
    }
}
