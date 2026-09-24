package br.com.helpdeskApp.userService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.helpdeskApp.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByActiveTrue(Pageable pageable);

    UserDetails findByEmail(String username);

    boolean existsByEmail(String email);

    @Query (
        value = 
        "SELECT * FROM users u WHERE " +
        "(:name IS NULL OR u.name ILIKE %:name%) AND " +
        "(:email IS NULL OR u.email = :email) AND " +
        "(:role IS NULL OR u.role ILIKE :role) AND " +
        "(u.active = TRUE)",
        nativeQuery = true
    )
    Page<User> getAllFilter(String name, String email, String role, Pageable pageable);

}
