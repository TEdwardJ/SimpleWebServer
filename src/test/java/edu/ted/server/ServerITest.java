package edu.ted.server;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerITest {

    private final OkHttpClient client = new OkHttpClient();
    private final String rootDirectory = System.getProperty("user.dir") + "/src/main/resources/webapp";
    private final Server server = new Server(3000, rootDirectory);

    @Test
    public void givenServerSendRequest_whenRequestedResult_thenCorrect() throws IOException, InterruptedException {
        List<String> linesList = Files.readAllLines(new File(rootDirectory + "/index.html").toPath());
        new Thread(server::start).start();
        Thread.sleep(500);
        Request request = new Request.Builder().url("http://127.0.0.1:3000").build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("text/html; charset=utf-8", response.header("Content-Type"));
        final String body = response.body().string();
        for (String line : linesList) {
            assertTrue(body.contains(line));
        }
        System.out.println(body);
    }

}
