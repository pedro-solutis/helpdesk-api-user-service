package br.com.helpdeskApp.userService.infra.exception;

public class ValidationException extends RuntimeException{

    public ValidationException(String message){
        super(message);
    }
}
