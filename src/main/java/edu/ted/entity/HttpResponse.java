package edu.ted.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private HttpResponseCode responseCode;

    private byte[] binaryBody;

    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse() {
    }

    public HttpResponse(HttpResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public byte[] getBinaryBody() {
        return binaryBody;
    }

    public void setBinaryBody(byte[] binaryBody) {
        this.binaryBody = binaryBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }


    public String getReasonPhrase() {
        return responseCode.getReasonPhrase();
    }


    public int getStatus() {
        return responseCode.getCode();
    }

    public HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
