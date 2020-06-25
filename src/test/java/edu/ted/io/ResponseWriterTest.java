package edu.ted.io;

import edu.ted.entity.HttpResponse;
import edu.ted.entity.HttpResponseCode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResponseWriterTest {

    @Test
    void givenPreparedResponseAndPushAnswer_whenResponseSentAndHasCorrectFields_thenCorrect() throws IOException {
        //given
        HttpResponse response = new HttpResponse(HttpResponseCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        response.setBinaryBody("<body>Hello!!!</body>".getBytes());
        response.setHeader("Content-Length", Integer.toString("<body>Hello!!!</body>".getBytes().length));
        response.setVersion("HTTP/1.1");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        //when
        ResponseWriter.sendResponse(out, response);
        String outcomeHTTPResponse = "HTTP/1.1 200 OK\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: 21\n\n" +
                "<body>Hello!!!</body>";
        //then
        assertEquals(outcomeHTTPResponse, out.toString());
    }


}