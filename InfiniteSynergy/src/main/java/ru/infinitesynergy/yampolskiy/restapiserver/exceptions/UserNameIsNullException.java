package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class UserNameIsNullException extends RuntimeException{
    public UserNameIsNullException(String message) {
        super(message);
    }
}
