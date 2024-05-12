package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getAllHeaders() {
        return headers;
    }
}
