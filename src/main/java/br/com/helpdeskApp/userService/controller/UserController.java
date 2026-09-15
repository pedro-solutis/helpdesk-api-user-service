package br.com.helpdeskApp.userService.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;

import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.model.UserDetailsDTO;
import br.com.helpdeskApp.userService.model.UserRegistrationDTO;
import br.com.helpdeskApp.userService.service.UserService;
import jakarta.transaction.Transactional;

@RestController 
@RequestMapping ("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping 
    @Transactional 
    public ResponseEntity<UserDetailsDTO> createUser(@RequestBody @Valid UserRegistrationDTO user, UriComponentsBuilder uriBuilder) {
        UserDetailsDTO createdUser = userService.createUser(user);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    public void getAllUsers() {
        // Implement logic to retrieve all users here
    }

    public void getUserById(Long id) {
        // Implement logic to retrieve a user by ID here
    }

    public void updateUser(Long id, User user) {
        // Implement user update logic here
    }

    public void deleteUser(Long id) {
        // Implement user deletion logic here
    }
}
