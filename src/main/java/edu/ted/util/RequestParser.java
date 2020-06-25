package edu.ted.util;

import edu.ted.entity.HttpMethodType;
import edu.ted.entity.HttpRequest;
import edu.ted.entity.HttpResponseCode;
import edu.ted.exception.ServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestParser {

    private static final Pattern METHOD_AND_URL_PATTERN = Pattern.compile("^(?<method>[A-Z]+) (?<resource>[^ ]+) (?<version>[^ ]+)");

    static final String EOL = "\n";

    private RequestParser() {
        throw new AssertionError("No com.study.util.RequestParser instances for you!");
    }

    public static HttpRequest parseRequestString(BufferedReader socketReader) {
        try {
            String requestString = readSocket(socketReader);
            if (requestString.isEmpty()) {
                return null;
            }
            return createRequest(requestString);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerException(HttpResponseCode.INTERNAL_ERROR, new HttpRequest());
        }
    }

    static String readSocket(BufferedReader in) throws IOException {
        StringBuilder request = new StringBuilder();
        boolean continueReceiving = true;
        while (continueReceiving) {
            String line = in.readLine();
            if (line != null && !line.isEmpty()) {
                request.append(line).append(EOL);
            } else {
                continueReceiving = false;
            }
        }
        return request.toString();
    }

    static HttpRequest createRequest(String requestString) {
        HttpRequest request = new HttpRequest();
        String[] requestLines = requestString.split("\n");
        enrichRequestWithUrlAndMethod(request, requestLines[0]);
        for (int i = 1; i < requestLines.length; i++) {
            if (requestLines[i].startsWith("Host")) {
                enrichRequestWithHostAndPort(request, requestLines[i]);
            } else {
                enrichRequestWithHeaders(request, requestLines[i]);
            }
        }
        return request;
    }

    static void enrichRequestWithHostAndPort(HttpRequest request, String requestLine) {
        String[] secondRequestLineParams = requestLine.split("[:]");
        request.setHost(secondRequestLineParams[1].trim());
        request.setPort(Integer.parseInt(secondRequestLineParams[2]));
    }

    static void enrichRequestWithUrlAndMethod(HttpRequest request, String requestLine) {
        Matcher requestMatcher = METHOD_AND_URL_PATTERN.matcher(requestLine);
        if (requestMatcher.find()) {
            String methodText = requestMatcher.group("method");
            String url = requestMatcher.group("resource");
            String version = requestMatcher.group("version");
            request.setResource(url);
            request.setVersion(version);
            HttpMethodType methodType;
            try {
                methodType = HttpMethodType.valueOf(methodText);
            } catch (IllegalArgumentException e) {
                throw new ServerException(HttpResponseCode.METHOD_NOT_ALLOWED, request);
            }
            request.setMethodType(methodType);
        } else {
            throw new ServerException(HttpResponseCode.BAD_REQUEST, new HttpRequest());
        }
    }

    static void enrichRequestWithHeaders(HttpRequest request, String line) {
        String[] headerNameValuePairArray = line.split(": ");
        request.setHeader(headerNameValuePairArray[0], headerNameValuePairArray[1]);
    }
}
