package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class TokenIsNotValidException extends RuntimeException{
    public TokenIsNotValidException(String message) {
        super(message);
    }
}
