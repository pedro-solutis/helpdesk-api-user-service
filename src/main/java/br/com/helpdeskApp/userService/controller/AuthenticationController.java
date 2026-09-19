package br.com.helpdeskApp.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdeskApp.userService.infra.security.TokenDataDTO;
import br.com.helpdeskApp.userService.infra.security.TokenService;
import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.dto.UserLoginDTO;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/login")
public class AuthenticationController {

    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired 
    private TokenService tokenService;

    @PostMapping 
    public ResponseEntity<TokenDataDTO> authenticate(@RequestBody @Valid UserLoginDTO userLogin) {
        var token = new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.password());
        var authentication = authenticationManager.authenticate(token);
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenDataDTO(tokenJWT));
    }
}
