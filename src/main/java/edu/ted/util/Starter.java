package edu.ted.util;

import edu.ted.server.Server;

public class Starter {

    public static void main(String[] args) {
        if (args.length < 2){
            throw new IllegalArgumentException("Not enough arguments to start SimpleWebServer");
        }
        System.out.println(args[1]);
        Server webServer = new Server(Integer.parseInt(args[0]), args[1]);
        webServer.start();
    }
}
