package edu.ted.util;

import edu.ted.entity.HttpMethodType;
import edu.ted.entity.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestParser {

    private static final Pattern METHOD_AND_URL_PATTERN = Pattern.compile("^(?<method>[A-Z]+) (?<resource>[^ ]+)");

    static final String EOL = "\n";

    private RequestParser() {
        throw new AssertionError("No com.study.util.RequestParser instances for you!");
    }

    public static HttpRequest parseRequestString(BufferedReader socketReader){
        try {
            String requestString = readSocket(socketReader);
            if (requestString.isEmpty()) {
                return null;
            }
            return createRequest(requestString);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
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
        for (int i = 0; i < requestLines.length; i++) {
            if (i == 0) {
                enrichRequestWithUrlAndMethod(request, requestLines[i]);
            } else if (i == 1) {
                String[] secondRequestLineParams = requestLines[i].split("[:]");
                request.setHost(secondRequestLineParams[1].trim());
                request.setPort(Integer.parseInt(secondRequestLineParams[2]));
            } else {
                String[] headerNameValuePairArray = requestLines[i].split(": ");
                request.setHeader(headerNameValuePairArray[0], headerNameValuePairArray[1]);
            }
        }
        return request;
    }

    private static void enrichRequestWithUrlAndMethod(HttpRequest request, String requestLine) {
        Matcher requestMatcher = METHOD_AND_URL_PATTERN.matcher(requestLine);
        if (requestMatcher.find()) {
            String methodText = requestMatcher.group("method");
            String url = requestMatcher.group("resource");
            HttpMethodType methodType;
            try {
                methodType = HttpMethodType.valueOf(methodText);
            } catch (IllegalArgumentException e) {
                return;
            }
            request.setMethodType(methodType);
            request.setResource(url);
        }
    }
}
