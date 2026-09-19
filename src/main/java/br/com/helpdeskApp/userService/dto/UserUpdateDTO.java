package br.com.helpdeskApp.userService.dto;

import br.com.helpdeskApp.userService.model.Role;

public record UserUpdateDTO(
    
    String name,

    String password,
    
    @ValueOfEnum(enumClass = Role.class, message = "Invalid role. Accepted Values: CLIENT, TECHNICIAN, ADMIN")
    String role

) {

}
