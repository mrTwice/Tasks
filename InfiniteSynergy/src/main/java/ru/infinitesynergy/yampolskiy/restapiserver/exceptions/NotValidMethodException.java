package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class NotValidMethodException extends RuntimeException{
    public NotValidMethodException(String message) {
        super(message);
    }
}
