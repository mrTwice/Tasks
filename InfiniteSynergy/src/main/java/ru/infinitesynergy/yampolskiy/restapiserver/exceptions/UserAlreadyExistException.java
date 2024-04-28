package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
