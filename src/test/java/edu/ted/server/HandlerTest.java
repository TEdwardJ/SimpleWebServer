package edu.ted.server;

import edu.ted.entity.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HandlerTest {

    private static final File TEST_DIRECTORY = new File("testWebApp");
    private static final File TEST_FILE = new File("testWebApp/testFile.html");

    private static final String TEST_CONTENT_STRING = "test content";

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

    @BeforeAll
    public static void prepareTestRootDirectory() throws IOException {
        TEST_DIRECTORY.mkdir();
        TEST_FILE.createNewFile();
        FileWriter writer = new FileWriter(TEST_FILE);
        writer.write(TEST_CONTENT_STRING);
        writer.flush();
        writer.close();
    }

    @AfterAll
    private static void deleteTestRootDirectory() {
        TEST_FILE.delete();
        TEST_DIRECTORY.delete();
    }

    @Test
    void givenRootDirectoryAndResource_whenHandleSocketEvent_thenCorrect() throws IOException {
        ///given
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
        String incomeRequest = incomeHTTPRequest.replace("/JavaLogo.png", "/testFile.html");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(incomeRequest.getBytes()));
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        //when
        Handler handler = new Handler(socket, "testWebApp");
        handler.handleSocketEvent();
        String outputString = mockOutputStream.toString();
        //then
        assertTrue(outputString.contains("HTTP/1.1 200 OK"));
        assertTrue(outputString.contains("Content-Type: text/html; charset=utf-8"));
        assertTrue(outputString.contains("Content-Length: 12"));
        assertTrue(outputString.contains(TEST_CONTENT_STRING));
    }

    @Test
    void givenRootDirectoryAndResource_whenHandleSocketEventAndReturn405_thenCorrect() throws IOException {
        ///given
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
        String incomeRequest = incomeHTTPRequest.replace("/JavaLogo.png", "/testFile.html").replace("GET","POST");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(incomeRequest.getBytes()));
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        //when
        Handler handler = new Handler(socket, "testWebApp");
        handler.handleSocketEvent();
        String outputString = mockOutputStream.toString();
        //then
        assertTrue(outputString.contains("HTTP/1.1 405 Method Not Allowed"));
    }


    @Test
    void givenRootDirectoryAndSendRequestForNonExistingResource_when404_thenCorrect() throws IOException {
        ///given
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
        String incomeRequest = incomeHTTPRequest.replace("/JavaLogo.png", "/testFileNonExisting.html");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(incomeRequest.getBytes()));
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        //when
        Handler handler = new Handler(socket, "testWebApp");
        handler.handleSocketEvent();
        String outputString = mockOutputStream.toString();
        //then
        assertTrue(outputString.contains("HTTP/1.1 404 Not Found"));
    }

    @Test
    void givenRootDirectoryAndSendBadRequest_when400_thenCorrect() throws IOException {
        ///given
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("HogWash".getBytes()));
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        //when
        Handler handler = new Handler(socket, "testWebApp");
        handler.handleSocketEvent();
        String outputString = mockOutputStream.toString();
        //then
        assertTrue(outputString.contains("HTTP/1.0 400 Bad Request"));
    }


    @Test
    void givenPreparedRequestAndProcess_whenValidResponse_thenCorrect() {
        //given
        HttpRequest request = new HttpRequest();
        request.setMethodType(HttpMethodType.GET);
        request.setResource("/testFile.html");
        request.setHost("127.0.0.1");
        request.setPort(3000);
        //when
        HttpResponse response = Handler.processRequest(request, TEST_DIRECTORY.getPath());
        //then
        assertEquals(200,  response.getStatus());
        assertEquals("OK",  response.getReasonPhrase());
        assertEquals("text/html; charset=utf-8",  response.getHeader("Content-Type"));
        assertArrayEquals(TEST_CONTENT_STRING.getBytes(),  response.getBinaryBody());
    }
}