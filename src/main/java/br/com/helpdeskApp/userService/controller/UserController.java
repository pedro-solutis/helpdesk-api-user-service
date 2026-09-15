package br.com.helpdeskApp.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdeskApp.userService.model.User;
import br.com.helpdeskApp.userService.service.UserService;

@RestController 
@RequestMapping ("/users")
public class UserController {

    @Autowired
    private UserService userService;

    public void createUser(User user) {
        // Implement user creation logic here
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
