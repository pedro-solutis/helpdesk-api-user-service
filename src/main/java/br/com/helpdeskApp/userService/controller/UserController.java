package br.com.helpdeskApp.userService.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;
import br.com.helpdeskApp.userService.dto.UserDetailsDTO;
import br.com.helpdeskApp.userService.dto.UserListDTO;
import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;
import br.com.helpdeskApp.userService.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping ("/users")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Users", description = "Endpoint for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create new user", description = "Registers a new user in the system")
    @PreAuthorize (value = "hasRole('ADMIN')")
    @PostMapping 
    @Transactional 
    public ResponseEntity<UserDetailsDTO> createUser(
            @Parameter(description = "User data for registration") @RequestBody @Valid UserRegistrationDTO user, 
            @Parameter(hidden = true) UriComponentsBuilder uriBuilder) {
        UserDetailsDTO createdUser = userService.createUser(user);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(summary = "List users", description = "Returns a paginated list of active users")
    @GetMapping 
    public ResponseEntity<Page<UserListDTO>> getAllUsers(
            @Parameter(description = "Optional filter by name")@RequestParam (required = false) String name,
            @Parameter(description = "Optional filter by emal")@RequestParam (required = false) String email,
            @Parameter(description = "Optional filter by role (e.g., ADMIN, TECHNICIAN, CLIENT)") @RequestParam(required = false) String role, 
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        var users = userService.getAllUsers(name, email, role, pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Returns the details of a specific user")
    @GetMapping ("/{id}")
    public ResponseEntity<UserDetailsDTO> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user", description = "Updates the data of an existing user")
    @PutMapping ("/{id}")
    @Transactional 
    public ResponseEntity<UserDetailsDTO> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id, 
            @Parameter(description = "Updated user data") @RequestBody @Valid UserUpdateDTO user) {
        var updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user", description = "Performs a soft delete (deactivation) of a user")
    @PreAuthorize (value = "hasRole('ADMIN')")
    @DeleteMapping ("/{id}")
    @Transactional 
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
