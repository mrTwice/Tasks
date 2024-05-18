package ru.infinitesynergy.yampolskiy.restapiserver.exceptions;

public class NotFundsEnoughInAccountException extends RuntimeException {
    public NotFundsEnoughInAccountException(String message) {
        super(message);
    }
}
