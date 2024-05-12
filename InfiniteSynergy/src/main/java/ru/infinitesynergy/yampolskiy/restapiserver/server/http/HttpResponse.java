package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.util.Map;

public class HttpResponse extends Http {
    private String protocolVersion;
    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public String toString() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(protocolVersion).append(" ").append(status.toString()).append("\r\n");
        for (Map.Entry<String, String> entry : getHeaders().getAllHeaders().entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n").append(getBody());
        return responseBuilder.toString();
    }

}
