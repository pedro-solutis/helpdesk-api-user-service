package br.com.helpdeskApp.userService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.model.UserDetailsDTO;
import br.com.helpdeskApp.userService.model.UserListDTO;
import br.com.helpdeskApp.userService.model.UserRegistrationDTO;
import br.com.helpdeskApp.userService.repository.UserRepository;

@Service 
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDetailsDTO createUser(UserRegistrationDTO user) {
        var newUser = new User(user);
        userRepository.save(newUser);
        return new UserDetailsDTO(newUser);
    }

    public Page<UserListDTO> getAllUsers(Pageable pageable) {
        var users = userRepository.findAllByActiveTrue(pageable);
        return users.map(UserListDTO::new);
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
