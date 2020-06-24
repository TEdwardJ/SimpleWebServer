package edu.ted.server;

import edu.ted.entity.*;
import edu.ted.io.ResourceReader;
import edu.ted.util.RequestParser;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public
class Handler {
    private static final HttpResponse NOT_FOUND_RESPONSE = new HttpResponse(HttpResponseCode.NOT_FOUND);
    private static final HttpResponse INTERNAL_ERROR_RESPONSE = new HttpResponse(HttpResponseCode.INTERNAL_ERROR);
    private static final HttpResponse METHOD_NOT_ALLOWED_RESPONSE = new HttpResponse(HttpResponseCode.METHOD_NOT_ALLOWED);

    private static final String EOL = "\n";

    private final Socket socket;
    private final String rootDirectory;

    public Handler(Socket socket, String rootDirectory) {
        this.socket = socket;
        this.rootDirectory = rootDirectory;
    }

    void handleSocketEvent() {
            try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedOutputStream binaryOutputStream = new BufferedOutputStream(socket.getOutputStream())) {
                HttpRequest request = RequestParser.parseRequestString(socketReader);
                if(request == null){
                    return;
                }
                HttpResponse response = processRequest(request, rootDirectory);
                sendResponse(binaryOutputStream, response);
        } catch (IOException e) {
            System.out.println("Some unexpected error happens during processing of the request");
            e.printStackTrace();
        }
    }

    static void sendResponse(OutputStream out, HttpResponse response) throws IOException {
        StringBuilder headerText = new StringBuilder();
        headerText
                .append(response.getResponseCode())
                .append(EOL);
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            headerText
                    .append(header.getKey())
                    .append(": ")
                    .append(header.getValue());
            headerText.append(EOL);
        }
        headerText.append(EOL);
        out.write(headerText.toString().getBytes());
        if (response.getHeader("Content-Length") != null) {
            out.write(response.getBinaryBody());
        }
        out.flush();
    }

    static HttpResponse processRequest(HttpRequest request, String rootDirectory) {
        if (request.getMethodType() == null) {
            return METHOD_NOT_ALLOWED_RESPONSE;
        }
        try {
            StaticResource resource = ResourceReader.getResource(request.getResource(), rootDirectory);
            if (resource == null) {
                return NOT_FOUND_RESPONSE;
            }
            HttpResponse response = new HttpResponse(HttpResponseCode.OK);
            response.setBinaryBody(resource.getResourceContent());
            response.setHeader("Content-Length", Integer.toString(response.getBinaryBody().length));
            response.setHeader("Content-Type", resource.getResourceType());
            return response;
        } catch (Exception e) {
            return INTERNAL_ERROR_RESPONSE;
        }
    }
}
