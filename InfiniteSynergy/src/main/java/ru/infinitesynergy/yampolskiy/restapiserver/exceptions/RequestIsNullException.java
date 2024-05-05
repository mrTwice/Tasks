package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class RequestIsNullException extends RuntimeException{
    public RequestIsNullException(String message) {
        super(message);
    }
}
