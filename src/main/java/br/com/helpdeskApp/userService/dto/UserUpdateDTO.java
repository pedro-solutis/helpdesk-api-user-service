package br.com.helpdeskApp.userService.dto;

import br.com.helpdeskApp.userService.model.Role;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
    
    @Size (min = 3, message = "Name must have a minimum of 3 characteres")
    String name,

    @Size (min = 8, message = "Password must have a minimum of 8 characteres")
    String password,
    
    @ValueOfEnum(enumClass = Role.class, message = "Invalid role. Accepted Values: CLIENT, TECHNICIAN, ADMIN")
    String role

) {

}
