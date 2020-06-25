package edu.ted.entity;

public enum HttpResponseCode {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private String reasonPhrase;

    private HttpResponseCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }


    @Override
    public String toString() {
        return code + " " + reasonPhrase;
    }
}
