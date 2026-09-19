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
import br.com.helpdeskApp.userService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service 
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetailsDTO createUser(UserRegistrationDTO user) {
        var newUser = new User(user);
        newUser.setPassword(passwordEncoder.encode(user.password()));
        userRepository.save(newUser);
        return new UserDetailsDTO(newUser);
    }

    public Page<UserListDTO> getAllUsers(Pageable pageable) {
        var users = userRepository.findAllByActiveTrue(pageable);
        return users.map(UserListDTO::new);
    }

    public UserDetailsDTO getUserById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserDetailsDTO(user);
    }

    public UserDetailsDTO updateUser(Long id, UserRegistrationDTO user) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.setName(user.name());
        existingUser.setEmail(user.email());
        existingUser.setRole(user.role());
        userRepository.save(existingUser);
        return new UserDetailsDTO(existingUser);
    }

    public void deleteUser(Long id) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.deactivate();
        userRepository.save(existingUser);
    }
}
