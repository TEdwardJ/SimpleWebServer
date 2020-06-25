package edu.ted.io;

import edu.ted.entity.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static edu.ted.util.Constants.EOL;

public class ResponseWriter {

    public static void sendResponse(OutputStream out, HttpResponse response) throws IOException {
        StringBuilder headerText = new StringBuilder();
        headerText
                .append(response.getVersion())
                .append(" ")
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
}
