package edu.ted.io;

import edu.ted.entity.HttpResponseCode;
import edu.ted.entity.StaticResource;
import edu.ted.exception.ServerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResourceReaderTest {

    private static final File TEST_DIRECTORY = new File("testWebApp");
    private static final File TEST_FILE = new File("testWebApp/testFile.html");

    private static final String TEST_CONTENT_STRING = "test content";

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
    void givenDiferrentResourcePaths_whenReturnsValidPathForSerrver_thenCorrect() {
        assertEquals("/index.html", ResourceReader.resolveResourcePath("/"));
        assertEquals("/logo.gif", ResourceReader.resolveResourcePath("/logo.gif"));
    }

    @Test
    void resolveResourceType() {
        assertEquals("application/pdf", ResourceReader.resolveResourceType("abcd.pdf"));
        assertEquals("image/png", ResourceReader.resolveResourceType("abcd.png"));
        assertEquals("text/plain; charset=utf-8", ResourceReader.resolveResourceType("abcd.txt"));
        assertEquals("image/jpeg", ResourceReader.resolveResourceType("abcd.jpeg"));
        assertEquals("image/jpeg", ResourceReader.resolveResourceType("abcd.jpg"));
        assertEquals("image/x-icon", ResourceReader.resolveResourceType("abcd.ico"));
        assertEquals("image/gif", ResourceReader.resolveResourceType("abcd.gif"));
        assertEquals("image/bmp", ResourceReader.resolveResourceType("abcd.bmp"));
        assertEquals("text/html; charset=utf-8", ResourceReader.resolveResourceType("abcd.html"));
        assertEquals("text/html; charset=utf-8", ResourceReader.resolveResourceType("abcd.htm"));
    }

    @Test
    void givenResource_whenFetchedWithContentEqualsToPrepared_thenCorrect() {
        //when
        StaticResource resource = ResourceReader.getResource("/testFile.html", TEST_DIRECTORY.getPath());
        //then
        assertEquals("text/html; charset=utf-8", resource.getResourceType());
        assertArrayEquals(TEST_CONTENT_STRING.getBytes(), resource.getResourceContent());
    }

    @Test
    void givenNonExistingFileAndGetResource_whenReturnsNull_thenCorrect() {
        ///given
        //when
        ServerException thrown = assertThrows(ServerException.class,()->ResourceReader.getResource("testFile2.html", ""));
        //then
        assertEquals(HttpResponseCode.NOT_FOUND, thrown.getResponseCode());
    }

    @Test
    void givenNonExistingFile_thenReadStaticResource_whenFetchedNull_thenCorrect() {
        File testFile = new File("testFile0.html");
        byte[] fileBytes = ResourceReader.readStaticResource(testFile);
        assertNull(fileBytes);
        testFile.delete();
    }

    @Test
    void givenNonEmptyFile_thenReadStaticResource_whenFetchedEqualToWritten_thenCorrect() {
        ///given TEST_FILE
        //when
        byte[] fileBytes = ResourceReader.readStaticResource(TEST_FILE);
        //then
        assertArrayEquals(TEST_CONTENT_STRING.getBytes(), fileBytes);
    }

}