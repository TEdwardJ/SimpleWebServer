package edu.ted.server;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public
class Handler {

    private static final String EOL = "\n";

    private final Socket socket;
    private final String rootDirectory;

    public Handler(Socket socket, String rootDirectory) {
        this.socket = socket;
        this.rootDirectory = rootDirectory;
    }

    void handleSocketEvent() {
        try {
            try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedOutputStream binaryOutputStream = new BufferedOutputStream(socket.getOutputStream())) {
                String requestString = readSocket(socketReader);
                if (requestString.isEmpty()) {
                    return;
                }
                HttpRequest request = createRequest(requestString);
                HttpResponse response = processRequest(request, rootDirectory);
                pushAnswer(binaryOutputStream, response);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Some unexpected error happens during processing of the request");
            e.printStackTrace();
        }
    }

    static String readSocket(BufferedReader in) throws IOException {
        String request = "";
        boolean continueReceiving = true;
        while (continueReceiving) {
            String line = in.readLine();
            if (line != null && !line.isEmpty()) {
                request = request.concat(line + EOL);
            } else {
                continueReceiving = false;
            }
        }
        return request;
    }

    static void pushAnswer(OutputStream out, HttpResponse response) throws IOException {
        StringBuilder headerText = new StringBuilder();
        headerText
                .append("HTTP/1.0 ")
                .append(response.getStatus())
                .append(" ")
                .append(response.getReasonPhrase())
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
        HttpResponse response = new HttpResponse();
        if (request.getMethodType() == null) {
            response.setResponseCode(HttpResponseCode.METHOD_NOT_ALLOWED);
            return response;
        }
        try {
            Resource resource = getResource(request.getResource(), rootDirectory);
            if (resource == null) {
                response.setResponseCode(HttpResponseCode.NOT_FOUND);
                return response;
            }
            response.setHeader("Content-Type", resource.getResourceType());
            response.setResponseCode(HttpResponseCode.OK);
            response.setBinaryBody(resource.getResourceContent());
            response.setHeader("Content-Length", Integer.toString(response.getBinaryBody().length));
        } catch (Exception e) {
            response.setResponseCode(HttpResponseCode.INTERNAL_ERROR);
        }
        return response;
    }

    static String resolveResourcePath(String resource) {
        if (resource.equals("/")) {
            return "/index.html";
        }
        return resource;
    }

    static String resolveResourceType(String path) {
        String[] pathFragments = path.split("\\.");
        String extension = pathFragments[1].toLowerCase();
        if ("html".equals(extension) || "htm".equals(extension)) {
            return "text/html; charset=utf-8";
        } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "image/jpeg";
        } else if ("bmp".equals(extension)) {
            return "image/bmp";
        } else if ("gif".equals(extension)) {
            return "image/gif";
        } else if ("ico".equals(extension)) {
            return "image/x-icon";
        } else if ("png".equals(extension)) {
            return "image/png";
        } else if ("pdf".equals(extension)) {
            return "application/pdf";
        } else if ("txt".equals(extension)) {
            return "text/plain; charset=utf-8";
        }
        return "application/octet-stream";
    }

    static Resource getResource(String resourceLocation, String rootDirectory) {
        String resolvedResourcelocation = resolveResourcePath(resourceLocation);
        File resourceFile = new File(rootDirectory + resolvedResourcelocation);

        if (!resourceFile.exists()) {
            return null;
        }
        StaticResource resource = new StaticResource();
        resource.setResourceType(resolveResourceType(resolvedResourcelocation));
        resource.setResourceContent(readStaticResource(resourceFile));

        return resource;
    }

    static byte[] readStaticResource(File resourceFile) {
        try (InputStream source = new BufferedInputStream(new FileInputStream(resourceFile));
             ByteArrayOutputStream byteContent = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = source.read(buffer)) > -1) {
                byteContent.write(buffer, 0, count);
            }
            return byteContent.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    static HttpRequest createRequest(String requestString) {
        HttpRequest request = new HttpRequest();
        String[] requestLines = requestString.split("\n");
        for (int i = 0; i < requestLines.length; i++) {
            if (i == 0) {
                String[] firstRequestLineParams = requestLines[i].split(" ");
                HttpMethodType methodType = null;
                try {
                    methodType = HttpMethodType.valueOf(firstRequestLineParams[0]);
                } catch (IllegalArgumentException e) {
                    //NOP
                }
                request.setMethodType(methodType);
                request.setResource(firstRequestLineParams[1]);
            } else if (i == 1) {
                String[] secondRequestLineParams = requestLines[i].split("[:]");
                request.setHost(secondRequestLineParams[1].trim());
                request.setPort(Integer.parseInt(secondRequestLineParams[2]));
            } else {
                String[] secondRequestLineParams = requestLines[i].split(": ");
                request.setHeader(secondRequestLineParams[0], secondRequestLineParams[1]);
            }
        }
        return request;
    }
}
