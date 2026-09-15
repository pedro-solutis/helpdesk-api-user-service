package br.com.helpdeskApp.userService.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;
import br.com.helpdeskApp.userService.model.UserDetailsDTO;
import br.com.helpdeskApp.userService.model.UserListDTO;
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

    @GetMapping 
    public ResponseEntity<Page<UserListDTO>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        var users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<UserDetailsDTO> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping ("/{id}")
    @Transactional 
    public ResponseEntity<UserDetailsDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserRegistrationDTO user) {
        var updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    public void deleteUser(Long id) {
        // Implement user deletion logic here
    }
}
