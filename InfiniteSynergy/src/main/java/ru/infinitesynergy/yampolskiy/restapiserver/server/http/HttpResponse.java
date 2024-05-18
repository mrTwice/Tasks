package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.util.Map;

public class HttpResponse extends Http {
    private final String protocolVersion;
    private final HttpStatus status;

    private HttpResponse(Builder builder) {
        this.protocolVersion = builder.protocolVersion;
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(protocolVersion).append(" ").append(status.toString()).append("\r\n");
        for (Map.Entry<String, String> entry : headers.getAllHeaders().entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n").append(body);
        return responseBuilder.toString();
    }

    public static class Builder {
        private String protocolVersion;
        private HttpStatus status;
        private HttpHeaders headers = new HttpHeaders();
        private String body;

        public Builder setProtocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder setStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder addHeader(HttpHeader httpHeader, String value) {
            this.headers.addHeader(httpHeader.getHeaderName(), value);
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}

