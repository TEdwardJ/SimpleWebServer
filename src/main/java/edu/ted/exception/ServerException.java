package edu.ted.exception;

import edu.ted.entity.HttpRequest;
import edu.ted.entity.HttpResponseCode;

public class ServerException extends RuntimeException{

    private HttpRequest request;
    private HttpResponseCode responseCode;

    public ServerException(HttpResponseCode httpStatus) {
        this.responseCode = httpStatus;
    }

    public ServerException(HttpResponseCode responseCode, HttpRequest request) {
        this.request = request;
        this.responseCode = responseCode;
    }

    public HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }
}
