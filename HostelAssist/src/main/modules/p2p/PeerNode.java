package main.modules.p2p;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerNode {
    // In-memory file registry: filename -> file data
    private static Map<String, byte[]> fileRegistry = new ConcurrentHashMap<>();
    private static Map<String, String> fileMetadata = new ConcurrentHashMap<>();
    
    static {
        // Initialize with sample files
        fileRegistry.put("Lab_Manual.pdf", "Sample PDF Content - Distributed Systems Lab Manual".getBytes());
        fileRegistry.put("Notes.txt", "Sample Notes - Important concepts covered in class".getBytes());
        fileMetadata.put("Lab_Manual.pdf", "application/pdf");
        fileMetadata.put("Notes.txt", "text/plain");
    }
    
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Module 4] P2P Peer Node listening on Port " + port);
            System.out.println("[Module 4] Available files: " + fileRegistry.keySet());
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new PeerHandler(clientSocket)).start();
            }
        } catch (IOException e) { 
            System.err.println("[P2P] Server error: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    
    public static void registerFile(String filename, byte[] data, String contentType) {
        fileRegistry.put(filename, data);
        fileMetadata.put(filename, contentType);
        System.out.println("[P2P] File registered: " + filename + " (" + data.length + " bytes)");
    }
    
    public static List<String> listFiles() {
        return new ArrayList<>(fileRegistry.keySet());
    }
    
    public static byte[] getFile(String filename) {
        return fileRegistry.get(filename);
    }
    
    public static String getFileType(String filename) {
        return fileMetadata.getOrDefault(filename, "application/octet-stream");
    }

    private static class PeerHandler implements Runnable {
        private Socket socket;
        
        public PeerHandler(Socket s) { 
            this.socket = s; 
        }
        
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {
                
                System.out.println("[P2P] Peer connected from: " + socket.getRemoteSocketAddress());
                
                String command = in.readLine();
                if (command == null) return;
                
                System.out.println("[P2P] Received command: " + command);
                
                if (command.startsWith("LIST")) {
                    // List available files
                    List<String> files = listFiles();
                    out.println("FILES:" + String.join(",", files));
                    
                } else if (command.startsWith("GET ")) {
                    // Download file
                    String filename = command.substring(4).trim();
                    byte[] fileData = getFile(filename);
                    
                    if (fileData != null) {
                        out.println("OK " + fileData.length + " " + getFileType(filename));
                        dataOut.write(fileData);
                        dataOut.flush();
                        System.out.println("[P2P] Sent file: " + filename + " (" + fileData.length + " bytes)");
                    } else {
                        out.println("ERROR: File not found");
                    }
                    
                } else if (command.startsWith("UPLOAD ")) {
                    // Upload file: format "UPLOAD filename size content-type"
                    String[] parts = command.substring(7).split(" ", 3);
                    if (parts.length >= 2) {
                        String filename = parts[0];
                        int size = Integer.parseInt(parts[1]);
                        String contentType = parts.length > 2 ? parts[2] : "application/octet-stream";
                        
                        // Read file data
                        byte[] buffer = new byte[size];
                        int totalRead = 0;
                        while (totalRead < size) {
                            int read = socket.getInputStream().read(buffer, totalRead, size - totalRead);
                            if (read == -1) break;
                            totalRead += read;
                        }
                        
                        registerFile(filename, buffer, contentType);
                        out.println("OK: File uploaded successfully");
                        System.out.println("[P2P] File uploaded: " + filename + " (" + size + " bytes)");
                    } else {
                        out.println("ERROR: Invalid upload format");
                    }
                    
                } else {
                    out.println("ERROR: Unknown command. Use LIST, GET <filename>, or UPLOAD <filename> <size> <type>");
                }
                
            } catch (IOException e) {
                System.err.println("[P2P] Error handling peer: " + e.getMessage());
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