package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends Http{
    private HttpMethod method;
    private URI uri;
    private String protocolVersion;
    private Map<String, String> requestParameters;

    public HttpRequest() {
        this.requestParameters = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public void addRequestParameter(String key, String value) {
        requestParameters.put(key, value);
    }
}
