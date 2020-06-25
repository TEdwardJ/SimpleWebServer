package edu.ted.server;

import edu.ted.entity.*;
import edu.ted.exception.ServerException;
import edu.ted.io.ResourceReader;
import edu.ted.io.ResponseWriter;
import edu.ted.util.RequestParser;

import java.io.*;
import java.net.Socket;

import static edu.ted.entity.HttpResponseCode.*;

public
class Handler {

    private final Socket socket;
    private final String rootDirectory;

    public Handler(Socket socket, String rootDirectory) {
        this.socket = socket;
        this.rootDirectory = rootDirectory;
    }

    void handleSocketEvent() {
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedOutputStream binaryOutputStream = new BufferedOutputStream(socket.getOutputStream())) {

            HttpRequest request;
            try {
                request = RequestParser.parseRequestString(socketReader);
            } catch (ServerException e) {
                HttpResponse response = new HttpResponse(e.getResponseCode(), e.getRequest().getVersion());
                ResponseWriter.sendResponse(binaryOutputStream, response);
                return;
            }
            if (request == null) {
                return;
            }
            HttpResponse response = processRequest(request, rootDirectory);
            ResponseWriter.sendResponse(binaryOutputStream, response);
        } catch (IOException e) {
            System.out.println("Some unexpected error happens during processing of the request");
            e.printStackTrace();
        }
    }

    static HttpResponse processRequest(HttpRequest request, String rootDirectory) {
        try {
            StaticResource resource = ResourceReader.getResource(request.getResource(), rootDirectory);

            HttpResponse response = new HttpResponse(OK, request.getVersion());
            response.setBinaryBody(resource.getResourceContent());
            response.setHeader("Content-Length", Integer.toString(response.getBinaryBody().length));
            response.setHeader("Content-Type", resource.getResourceType());
            return response;
        } catch (ServerException e) {
            return new HttpResponse(e.getResponseCode(), request.getVersion());
        } catch (Exception e) {
            return new HttpResponse(INTERNAL_ERROR, request.getVersion());
        }
    }
}
