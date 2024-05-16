package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.Error;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public static HttpResponse getErrorResponse(HttpRequest httpRequest, HttpStatus status, String message) throws JsonProcessingException {
        Error error = new Error(status.getMessage(), status.getCode(), message);
        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(error);

        return new HttpResponse.Builder()
                .setProtocolVersion(httpRequest.getProtocolVersion())
                .setStatus(status)  // Использование переданного статуса
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
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

