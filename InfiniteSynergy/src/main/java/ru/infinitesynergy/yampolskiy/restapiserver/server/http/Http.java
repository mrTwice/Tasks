package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.util.HashMap;
import java.util.Map;


public class Http {
    private HttpHeaders headers;
    private String body;

    public Http() {
        this.headers = new HttpHeaders();
        this.body = "";
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
