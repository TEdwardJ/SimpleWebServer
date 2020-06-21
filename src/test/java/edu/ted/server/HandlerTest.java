package edu.ted.server;

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
        assertTrue(outputString.contains("HTTP/1.0 200 OK"));
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
        assertTrue(outputString.contains("HTTP/1.0 405 Method Not Allowed"));
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
        assertTrue(outputString.contains("HTTP/1.0 404 Not Found"));
    }

    @Test
    void givenMockSocketInputStream_whenReadsInputAndCoincideWithStreamContent_thenCorrect() throws IOException {
        //given
        StringReader stringReader = new StringReader(incomeHTTPRequest);
        BufferedReader socketReader = new BufferedReader(stringReader);
        //when
        String actualRequest = Handler.readSocket(socketReader);
        //then
        assertEquals(incomeHTTPRequest + "\n", actualRequest);
    }

    @Test
    void givenPreparedResponseAndPushAnswer_whenResponseSentAndHasCorrectFields_thenCorrect() throws IOException {
        //given
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponseCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        response.setBinaryBody("<body>Hello!!!</body>".getBytes());
        response.setHeader("Content-Length", Integer.toString("<body>Hello!!!</body>".getBytes().length));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        //when
        Handler.pushAnswer(out, response);
        String outcomeHTTPResponse = "HTTP/1.0 200 OK\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: 21\n\n" +
                "<body>Hello!!!</body>";
        //then
        assertEquals(outcomeHTTPResponse, out.toString());
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

    @Test
    void givenDiferrentResourcePaths_whenReturnsValidPathForSerrver_thenCorrect() {
        assertEquals("/index.html", Handler.resolveResourcePath("/"));
        assertEquals("/logo.gif", Handler.resolveResourcePath("/logo.gif"));
    }

    @Test
    void resolveResourceType() {
        assertEquals("application/pdf", Handler.resolveResourceType("abcd.pdf"));
        assertEquals("image/png", Handler.resolveResourceType("abcd.png"));
        assertEquals("text/plain; charset=utf-8", Handler.resolveResourceType("abcd.txt"));
        assertEquals("image/jpeg", Handler.resolveResourceType("abcd.jpeg"));
        assertEquals("image/jpeg", Handler.resolveResourceType("abcd.jpg"));
        assertEquals("image/x-icon", Handler.resolveResourceType("abcd.ico"));
        assertEquals("image/gif", Handler.resolveResourceType("abcd.gif"));
        assertEquals("image/bmp", Handler.resolveResourceType("abcd.bmp"));
        assertEquals("text/html; charset=utf-8", Handler.resolveResourceType("abcd.html"));
        assertEquals("text/html; charset=utf-8", Handler.resolveResourceType("abcd.htm"));
    }

    @Test
    void givenResource_whenFetchedWithContentEqualsToPrepared_thenCorrect() {
        //when
        Resource resource = Handler.getResource("/testFile.html", TEST_DIRECTORY.getPath());
        //then
        assertEquals("text/html; charset=utf-8", resource.getResourceType());
        assertArrayEquals(TEST_CONTENT_STRING.getBytes(), resource.getResourceContent());
    }


    @Test
    void givenNonExistingFileAndGetResource_whenReturnsNull_thenCorrect() {
        ///given
        //when
        Resource resource = Handler.getResource("testFile2.html", "");
        //then
        assertNull(resource);
    }

    @Test
    void givenNonExistingFile_thenReadStaticResource_whenFetchedNull_thenCorrect() throws IOException {
        File testFile = new File("testFile0.html");
        byte[] fileBytes = Handler.readStaticResource(testFile);
        assertNull(fileBytes);
        testFile.delete();
    }

    @Test
    void givenNonEmptyFile_thenReadStaticResource_whenFetchedEqualToWritten_thenCorrect() {
        ///given TEST_FILE
        //when
        byte[] fileBytes = Handler.readStaticResource(TEST_FILE);
        //then
        assertArrayEquals(TEST_CONTENT_STRING.getBytes(), fileBytes);
    }

    @Test
    void givenStringRequestConstructRequestObject_whenAllFieldsSet_thenCorrect() {
        //given incomeHTTPRequest
        //when
        HttpRequest request = Handler.createRequest(incomeHTTPRequest);
        //then
        assertEquals("127.0.0.1", request.getHost());
        assertEquals("3000", request.getPort());
        assertEquals(HttpMethodType.GET, request.getMethodType());
        assertEquals("/JavaLogo.png", request.getResource());
        assertEquals("document", request.getHeader("Sec-Fetch-Dest"));
        assertEquals("max-age=0", request.getHeader("Cache-Control"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9", request.getHeader("Accept"));
        assertEquals("gzip, deflate, br", request.getHeader("Accept-Encoding"));
   }


}