package br.com.helpdeskApp.userService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.helpdeskApp.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByActiveTrue(Pageable pageable);

    UserDetails findByEmail(String username);

    boolean existsByEmail(
            String email);

}
