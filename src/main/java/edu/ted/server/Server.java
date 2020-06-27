package edu.ted.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final static int DEFAULT_PORT = 3000;

    private int port;
    private final String rootDirectory;

    volatile private boolean toBeShutDowned = false;

    public Server(int port, String rootDirectory) {
        this.port = port;
        this.rootDirectory = rootDirectory;
    }

    public Server(String rootDirectory) {
        this(DEFAULT_PORT, rootDirectory);
    }

    public boolean isToBeShutDowned() {
        return toBeShutDowned;
    }

    public void setToBeShutDowned() {
        toBeShutDowned = true;
    }

    public void start() {
        if (!new File(rootDirectory).exists()) {
            System.out.println("The server has not started due to specified root directory " + rootDirectory + " cannot be found");
            return;
        }
        try (ServerSocket socket = new ServerSocket(port)) {
            while (!isToBeShutDowned()) {
                try {
                    Socket clientSocket = socket.accept();
                    startHandler(clientSocket);
                } catch (IOException e) {
                    System.out.println("The attempt to establish connection with client is failed: ");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("The SimpleWebServer is to down immediately due to some unexpected error: ");
            e.printStackTrace();
        }
    }

    private void startHandler(Socket clientSocket) {
        new Thread(new Handler(clientSocket, rootDirectory)).start();
    }
}
