package br.com.helpdeskApp.userService.infra.seed;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.helpdeskApp.userService.repository.UserRepository;
import br.com.helpdeskApp.userService.model.*;

@Component 
public class AdminUserSeed implements CommandLineRunner{

    @Value("${ADMIN_USERNAME:admin@admin.com.br}")
    private String username;

    @Value("${ADMIN_PASSWORD:admin}")
    private String password;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail(username)){
            User userAdmin = new User(
                null,
                "admin",
                username,
                encoder.encode(password),
                Role.ADMIN, 
                true,
                LocalDateTime.now()
            );
            userRepository.save(userAdmin);
            System.out.println("User admin created");
        }
    }

}
