package br.com.helpdeskApp.userService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.dto.UserDetailsDTO;
import br.com.helpdeskApp.userService.dto.UserListDTO;
import br.com.helpdeskApp.userService.dto.UserRegistrationDTO;
import br.com.helpdeskApp.userService.dto.UserUpdateDTO;
import br.com.helpdeskApp.userService.infra.exception.DataConflictException;
import br.com.helpdeskApp.userService.infra.exception.InactiveUserException;
import br.com.helpdeskApp.userService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service 
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetailsDTO createUser(UserRegistrationDTO user) {
        if (userRepository.existsByEmail(user.email()))
            throw new DataConflictException("This email is already registered");
        var newUser = new User(user, passwordEncoder);
        userRepository.save(newUser);
        return new UserDetailsDTO(newUser);
    }

    public Page<UserListDTO> getAllUsers(String name, String email, String role, Pageable pageable) {
        if (name!=null || email != null || role != null){
            var users = userRepository.getAllFilter(name, email, role.toUpperCase(), pageable);
            return users.map(UserListDTO::new);
        }
        var users = userRepository.findAllByActiveTrue(pageable);
        return users.map(UserListDTO::new);
    }

    public UserDetailsDTO getUserById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserDetailsDTO(user);
    }

    public UserDetailsDTO updateUser(Long id, UserUpdateDTO user) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!existingUser.isActive()) {
            throw new InactiveUserException("The operation cannot be completed: The user is inactive.");
        }        
        existingUser.updateUser(user, passwordEncoder);
        userRepository.save(existingUser);
        return new UserDetailsDTO(existingUser);
    }

    public void deleteUser(Long id) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!existingUser.isActive()){
            throw new InactiveUserException("The operation cannot be completed: The user is inactive.");
        }
        existingUser.deactivate();
        userRepository.save(existingUser);
    }
}
