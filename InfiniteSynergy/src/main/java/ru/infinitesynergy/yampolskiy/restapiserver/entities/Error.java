package ru.infinitesynergy.yampolskiy.restapiserver.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Error {
    private String error;
    private int statusCode;
    private String details;

    private Error(String error, int statusCode, String details) {
        this.error = error;
        this.statusCode = statusCode;
        this.details = details;
    }

    public static Error createError(String error, int statusCode, String details) {
        return new Error(error, statusCode, details);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
