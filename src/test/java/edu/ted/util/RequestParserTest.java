package edu.ted.util;

import edu.ted.entity.HttpMethodType;
import edu.ted.entity.HttpRequest;
import edu.ted.server.Handler;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    private final String incomeHTTPRequest = "GET /JavaLogo.png HTTP/1.1\n" +
            "Host: 127.0.0.1:3000\n" +
            "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
            "Sec-Fetch-Site: none\n" +
            "Sec-Fetch-Mode: navigate\n" +
            "Sec-Fetch-User: ?1\n" +
            "Sec-Fetch-Dest: document\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7";

    @Test
    void givenMockSocketInputStream_whenReadsInputAndCoincideWithStreamContent_thenCorrect() throws IOException {
        //given
        StringReader stringReader = new StringReader(incomeHTTPRequest);
        BufferedReader socketReader = new BufferedReader(stringReader);
        //when
        String actualRequest = RequestParser.readSocket(socketReader);
        //then
        assertEquals(incomeHTTPRequest + "\n", actualRequest);
    }

    @Test
    void givenStringRequestConstructRequestObject_whenAllFieldsSet_thenCorrect() {
        //given incomeHTTPRequest
        //when
        HttpRequest request = RequestParser.createRequest(incomeHTTPRequest);
        //then
        assertEquals("127.0.0.1", request.getHost());
        assertEquals(3000, request.getPort());
        assertEquals(HttpMethodType.GET, request.getMethodType());
        assertEquals("/JavaLogo.png", request.getResource());
        assertEquals("document", request.getHeader("Sec-Fetch-Dest"));
        assertEquals("max-age=0", request.getHeader("Cache-Control"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9", request.getHeader("Accept"));
        assertEquals("gzip, deflate, br", request.getHeader("Accept-Encoding"));
    }
}