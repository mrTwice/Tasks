package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.util.HashMap;
import java.util.Map;


public class Http {
    private Map<String, String> headers;
    private String body;

    public Http() {
        this.headers = new HashMap<>();
        this.body ="";
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
