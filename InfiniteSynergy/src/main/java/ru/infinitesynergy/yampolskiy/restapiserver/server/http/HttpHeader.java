package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    AUTHORIZATION("Authorization"),
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    COOKIE("Cookie"),
    HOST("Host"),
    REFERER("Referer"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    SERVER("Server"),
    DATE("Date"),
    LOCATION("Location");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
