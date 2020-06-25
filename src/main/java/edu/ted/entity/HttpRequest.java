package edu.ted.entity;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.0";

    private HttpMethodType methodType;
    private String version = DEFAULT_HTTP_VERSION;
    private String resource;
    private String host;
    private int port;
    private final Map<String, String> headers = new HashMap<>();

    public HttpMethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(HttpMethodType methodType) {
        this.methodType = methodType;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
