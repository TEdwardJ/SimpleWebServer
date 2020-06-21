package edu.ted.server;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpMethodType methodType;
    private String resource;
    private String host;
    private int port;
    private Map<String, String> headers = new HashMap<>();

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
}
