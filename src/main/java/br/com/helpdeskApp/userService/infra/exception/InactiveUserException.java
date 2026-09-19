package br.com.helpdeskApp.userService.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InactiveUserException extends RuntimeException{

    public InactiveUserException(String message) {
        super(message);
    }

}
