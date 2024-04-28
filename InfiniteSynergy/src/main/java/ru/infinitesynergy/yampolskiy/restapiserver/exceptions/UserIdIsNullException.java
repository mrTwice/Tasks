package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class UserIdIsNullException extends RuntimeException{
    public UserIdIsNullException(String message) {
        super(message);
    }
}
