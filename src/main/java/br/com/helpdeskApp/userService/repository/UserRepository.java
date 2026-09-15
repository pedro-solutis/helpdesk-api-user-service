package br.com.helpdeskApp.userService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.helpdeskApp.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
