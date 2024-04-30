package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

public class HttpResponse extends Http {
    private int statusCode;

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
    }
}
