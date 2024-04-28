package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class PasswordIsNullException extends RuntimeException{
    public PasswordIsNullException(String message) {
        super(message);
    }
}
