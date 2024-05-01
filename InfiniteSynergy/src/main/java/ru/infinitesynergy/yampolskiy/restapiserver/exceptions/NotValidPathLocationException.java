package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class NotValidPathLocationException extends RuntimeException {
    public NotValidPathLocationException(String message) {
        super(message);
    }
}
