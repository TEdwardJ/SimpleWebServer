package edu.ted.server;

public final class HttpResponseCode {

    public static final HttpResponseCode OK = new HttpResponseCode(200, "OK");
    public static final HttpResponseCode NOT_FOUND = new HttpResponseCode(404, "Not Found");
    public static final HttpResponseCode METHOD_NOT_ALLOWED = new HttpResponseCode(405, "Method Not Allowed");
    public static final HttpResponseCode INTERNAL_ERROR = new HttpResponseCode(500, "Internal Server Error");

    private int code;
    private String ReasonPhrase;

    private HttpResponseCode(int code, String reasonPhrase) {
        this.code = code;
        ReasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return ReasonPhrase;
    }
}
