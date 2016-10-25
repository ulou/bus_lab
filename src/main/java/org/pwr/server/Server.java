package org.pwr.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Server implements Runnable{
    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;

    public Server(int port) {
        this.serverPort = port;
    }

    public void run() {
        System.out.println("Server Starting");
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                new Thread(new SocketThread(clientSocket, "Multithreaded Server")).start();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException("Error with client connection", e);
            }
        }
        System.out.println("Server Stopped.");
    }



    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port"+serverPort, e);
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }
}
