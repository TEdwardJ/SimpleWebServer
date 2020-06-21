package edu.ted.server;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void givenServerAndSetToBeShutDowned_whenISToBeShutDowned_thenCorrect() {
        //given
        Server server = new Server(3000, "");
        assertFalse(server.isToBeShutDowned());
        //when
        server.setToBeShutDowned();
        //then
        assertTrue(server.isToBeShutDowned());
    }


    @Test
    void givenServerWithNonExistingRootDirectory_whenNotStarted_thenCorrect() throws InterruptedException {
        Server server = new Server(3000, "nonExistingRootDirectory");
        ExecutorService executorsPool = Executors.newFixedThreadPool(1);
        Future serverTask = executorsPool.submit(()->server.start());
        Thread.sleep(500);
        assertTrue(serverTask.isDone());
    }
}