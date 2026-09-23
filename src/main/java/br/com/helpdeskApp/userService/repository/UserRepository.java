package br.com.helpdeskApp.userService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.helpdeskApp.userService.model.Role;
import br.com.helpdeskApp.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByActiveTrue(Pageable pageable);

    UserDetails findByEmail(String username);

    boolean existsByEmail(String email);

    @Query (
        value = 
        "select u from User u where " +
        "(:name is null or u.name like %:name%) AND " +
        "(:email is null or u.email = :email) AND " +
        "(:role is null or u.role = :role) ", 
        nativeQuery = true
    )
    Page<User> getAllFilter(String name, String email, Role role, Pageable pageable);

}
