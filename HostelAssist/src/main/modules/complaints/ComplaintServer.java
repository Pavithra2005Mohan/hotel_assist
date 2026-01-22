package main.modules.complaints;

import java.io.*;
import java.net.*;

public class ComplaintServer {
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Module 1] Complaint Socket Server running on Port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        public ClientHandler(Socket socket) { this.socket = socket; }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                
                System.out.println("[Socket] Client connected from: " + socket.getRemoteSocketAddress());
                
                String input = in.readLine();
                if (input != null) {
                    System.out.println("[Socket] Received: " + input);
                    String response = ComplaintService.processComplaint(input);
                    out.println(response);
                    System.out.println("[Socket] Sent response: " + response);
                }
            } catch (IOException e) {
                System.err.println("[Socket] Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
}