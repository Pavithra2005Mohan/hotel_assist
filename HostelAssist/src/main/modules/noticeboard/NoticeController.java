package main.modules.noticeboard;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class NoticeController {

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/api/notices", new ApiHandler());
        server.createContext("/api/complaint", new ComplaintProxyHandler()); // Proxy for Socket
        server.createContext("/api/room", new RoomProxyHandler()); // Proxy for RMI
        server.createContext("/api/p2p", new P2PProxyHandler()); // Proxy for P2P
        server.createContext("/", new WebHandler()); // Serves the UI
        
        server.setExecutor(null);
        System.out.println("[Module 3] Notice Controller running on Port " + port);
        server.start();
    }

    static class ApiHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                // Try to parse as JSON: {"title":"...","message":"..."}
                // Or fallback to simple text format
                if (body.trim().startsWith("{")) {
                    try {
                        // Simple JSON parsing (for demo purposes)
                        String title = extractJsonField(body, "title");
                        String message = extractJsonField(body, "message");
                        NoticeService.addNotice(title, message);
                    } catch (Exception e) {
                        // Fallback to simple text
                        NoticeService.addNotice(body);
                    }
                } else {
                    NoticeService.addNotice(body);
                }
            }

            String response = NoticeService.getNoticesAsJSON();
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String extractJsonField(String json, String field) {
            String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            return "";
        }
    }

    // Proxy handler for Socket-based complaints
    static class ComplaintProxyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                // Parse JSON: {"room":"101","category":"Water","description":"..."}
                String room = extractJsonField(body, "room");
                String category = extractJsonField(body, "category");
                String description = extractJsonField(body, "description");
                
                // Connect to socket server
                try (java.net.Socket socket = new java.net.Socket("localhost", 5000);
                     java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                     java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
                    
                    String complaintData = room + "|" + category + "|" + description;
                    out.println(complaintData);
                    String response = in.readLine();
                    
                    String jsonResponse = "{\"status\":\"success\",\"message\":\"" + 
                        response.replace("\"", "\\\"") + "\"}";
                    exchange.sendResponseHeaders(200, jsonResponse.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } catch (Exception e) {
                    String error = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, error.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(error.getBytes());
                    os.close();
                }
            } else {
                String response = "{\"error\":\"Method not allowed\"}";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String extractJsonField(String json, String field) {
            String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            return "";
        }
    }
    
    // Proxy handler for RMI-based room info
    static class RoomProxyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String roomNo = "101";
            if (query != null && query.contains("room=")) {
                roomNo = query.substring(query.indexOf("room=") + 5).split("&")[0];
            }
            
            try {
                java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(1099);
                main.modules.roominfo.HostelInterface stub = 
                    (main.modules.roominfo.HostelInterface) registry.lookup("RoomInfo");
                
                String details = stub.getRoomDetails(roomNo);
                String contact = stub.getWardenContact(roomNo);
                
                String jsonResponse = "{\"room\":\"" + roomNo + "\",\"details\":\"" + 
                    details.replace("\"", "\\\"") + "\",\"contact\":\"" + 
                    contact.replace("\"", "\\\"") + "\"}";
                exchange.sendResponseHeaders(200, jsonResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            } catch (Exception e) {
                String error = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }
    
    // Proxy handler for P2P file operations
    static class P2PProxyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String action = "list";
            String filename = "";
            
            if (query != null) {
                if (query.contains("action=")) {
                    action = query.substring(query.indexOf("action=") + 7).split("&")[0];
                }
                if (query.contains("file=")) {
                    filename = query.substring(query.indexOf("file=") + 5).split("&")[0];
                }
            }
            
            try {
                if (action.equals("list")) {
                    // Connect to P2P server to list files
                    try (java.net.Socket socket = new java.net.Socket("localhost", 6000);
                         java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                         java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
                        
                        out.println("LIST");
                        String response = in.readLine();
                        
                        // Parse response: "FILES:file1,file2,file3"
                        String jsonResponse;
                        if (response != null && response.startsWith("FILES:")) {
                            String filesList = response.substring(6);
                            String[] files = filesList.split(",");
                            java.util.List<String> fileArray = new java.util.ArrayList<>();
                            for (String f : files) {
                                if (!f.trim().isEmpty()) {
                                    fileArray.add("\"" + f.trim() + "\"");
                                }
                            }
                            jsonResponse = "{\"files\":\"FILES:" + filesList + "\",\"fileList\":[" + 
                                String.join(",", fileArray) + "]}";
                        } else {
                            jsonResponse = "{\"files\":\"FILES:\",\"fileList\":[]}";
                        }
                        exchange.sendResponseHeaders(200, jsonResponse.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(jsonResponse.getBytes());
                        os.close();
                    }
                } else if (action.equals("download") && !filename.isEmpty()) {
                    // Download file from P2P
                    try (java.net.Socket socket = new java.net.Socket("localhost", 6000);
                         java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
                         java.io.DataInputStream in = new java.io.DataInputStream(socket.getInputStream())) {
                        
                        out.println("GET " + filename);
                        String header = in.readLine();
                        if (header.startsWith("OK")) {
                            String[] parts = header.split(" ");
                            int size = Integer.parseInt(parts[1]);
                            byte[] data = new byte[size];
                            in.readFully(data);
                            
                            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                            exchange.sendResponseHeaders(200, size);
                            OutputStream os = exchange.getResponseBody();
                            os.write(data);
                            os.close();
                        } else {
                            String error = "{\"error\":\"" + header + "\"}";
                            exchange.sendResponseHeaders(404, error.length());
                            OutputStream os = exchange.getResponseBody();
                            os.write(error.getBytes());
                            os.close();
                        }
                    }
                } else {
                    String error = "{\"error\":\"Invalid action\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(error.getBytes());
                    os.close();
                }
            } catch (Exception e) {
                String error = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class WebHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String root = "src/web";
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            File file = new File(root + path);
            if (file.exists() && file.isFile()) {
                // Set content type based on file extension
                String contentType = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                Files.copy(file.toPath(), exchange.getResponseBody());
                exchange.getResponseBody().close();
            } else {
                String msg = "404 Not Found";
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(404, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.getResponseBody().close();
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            if (path.endsWith(".json")) return "application/json";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            return "application/octet-stream";
        }
    }
}