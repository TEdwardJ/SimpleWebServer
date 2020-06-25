package edu.ted.util;

import edu.ted.entity.HttpMethodType;
import edu.ted.entity.HttpRequest;
import edu.ted.entity.HttpResponseCode;
import edu.ted.exception.ServerException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    private final String requestString = "GET /JavaLogo.png HTTP/1.1\n";
    private final String incomeHeaders =
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
        final String incomeHTTPRequest = requestString + incomeHeaders;
        StringReader stringReader = new StringReader(incomeHTTPRequest);
        BufferedReader socketReader = new BufferedReader(stringReader);
        //when
        String actualHttpRequest = RequestParser.readSocket(socketReader);
        //then
        assertEquals(incomeHTTPRequest + "\n", actualHttpRequest);
    }

    @Test
    void givenStringHttpRequestConstructHttpRequestObject_whenAllFieldsSet_thenCorrect() {
        //given incomeHTTPHttpRequest
        //when
        final String incomeHTTPRequest = requestString + incomeHeaders;
        HttpRequest HttpRequest = RequestParser.createRequest(incomeHTTPRequest);
        //then
        assertEquals("127.0.0.1", HttpRequest.getHost());
        assertEquals(3000, HttpRequest.getPort());
        assertEquals(HttpMethodType.GET, HttpRequest.getMethodType());
        assertEquals("/JavaLogo.png", HttpRequest.getResource());
        assertEquals("document", HttpRequest.getHeader("Sec-Fetch-Dest"));
        assertEquals("max-age=0", HttpRequest.getHeader("Cache-Control"));
        assertEquals("keep-alive", HttpRequest.getHeader("Connection"));
        assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9", HttpRequest.getHeader("Accept"));
        assertEquals("gzip, deflate, br", HttpRequest.getHeader("Accept-Encoding"));
    }

    @Test
    void givenRequestAndFirstRequestString_whenRequestEnrichedWithMethodAndResourceAndVersion_thenCorrect() {
        HttpRequest httpRequest1 = new HttpRequest();
        RequestParser.enrichRequestWithUrlAndMethod(httpRequest1, "GET /logo.gif HTTP/1.1");
        assertEquals(HttpMethodType.valueOf("GET"), httpRequest1.getMethodType());
        assertEquals("/logo.gif", httpRequest1.getResource());
        assertEquals("HTTP/1.1", httpRequest1.getVersion());


        HttpRequest httpRequest2 = new HttpRequest();
        RequestParser.enrichRequestWithUrlAndMethod(httpRequest2, "GET /logo2.gif HTTP/1.0");
        assertEquals(HttpMethodType.valueOf("GET"), httpRequest2.getMethodType());
        assertEquals("/logo2.gif", httpRequest2.getResource());
        assertEquals("HTTP/1.0", httpRequest2.getVersion());
    }

    @Test
    void givenRequestAndHedersString_whenRequestEnrichedWithHeaders_thenCorrect() {
        //given
        HttpRequest request = new HttpRequest();
        String[] headers = incomeHeaders.split("\\n");
        //when
        for (String headerLine : headers) {
            RequestParser.enrichRequestWithHeaders(request, headerLine);
        }
        //then
        assertEquals("ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7", request.getHeader("Accept-Language"));
        assertEquals("document", request.getHeader("Sec-Fetch-Dest"));
        assertEquals("none", request.getHeader("Sec-Fetch-Site"));
        assertEquals("127.0.0.1:3000", request.getHeader("Host"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("?1", request.getHeader("Sec-Fetch-User"));
        assertEquals("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36", request.getHeader("User-Agent"));
    }

    @Test
    void givenRequestAndHostHeaderString_whenRequestEnrichedWithHostAndPort_thenCorrect() {
        //given
        HttpRequest request = new HttpRequest();
        String hostHeader = "Host: 127.0.0.1:3030";

        //when
        RequestParser.enrichRequestWithHostAndPort(request, hostHeader);
        //then
        assertEquals("127.0.0.1", request.getHost());
        assertEquals(3030, request.getPort());
    }

    @Test
    void givenBadRequest_whenRequestEnrichedWithHostAndPort_thenCorrect() {
        //given
        String requestString = "Some HogWash\n";

        //when
        ServerException thrown = assertThrows(ServerException.class,()-> RequestParser.createRequest(requestString));
        //then
        assertEquals(HttpResponseCode.BAD_REQUEST, thrown.getResponseCode());

    }
}