package br.com.helpdeskApp.userService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.helpdeskApp.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByActiveTrue(Pageable pageable);

}
