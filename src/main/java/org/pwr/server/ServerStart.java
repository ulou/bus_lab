package org.pwr.server;

import org.pwr.Configuration;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class ServerStart {
    public static void main(String args[]) {
        Server server = new Server(Configuration.PORT);
        new Thread(server).start();
        while (!server.isStopped) {
        }
        System.out.println("Server stopped.");
        server.stop();
    }
}
