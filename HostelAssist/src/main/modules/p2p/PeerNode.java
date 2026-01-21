package main.modules.p2p;

import java.io.*;
import java.net.*;

public class PeerNode {
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Module 4] P2P Peer Node listening on Port " + port);
            while (true) {
                new Thread(new PeerHandler(serverSocket.accept())).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static class PeerHandler implements Runnable {
        private Socket socket;
        public PeerHandler(Socket s) { this.socket = s; }
        public void run() {
            try { 
                socket.close(); 
            } catch(IOException e){}
        }
    }
}