package main.modules.noticeboard;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class NoticeServer {
    private static List<String> notices = Collections.synchronizedList(new ArrayList<>());
    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/notices", new NoticeHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        System.out.println("[Module 3] REST API & Web Server running on port " + port);
        server.start();
        notices.add("Welcome to the Hostel Assist Distributed System!");
        notices.add("Lunch today will be served at 1:00 PM.");
    }
    static class NoticeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String response = convertListToJson();
                sendResponse(exchange, response, 200, "application/json");
            } 
            else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());
                if (!body.trim().isEmpty()) {
                    notices.add(body);
                    sendResponse(exchange, "{\"status\":\"added\"}", 200, "application/json");
                } else {
                    sendResponse(exchange, "{\"error\":\"empty\"}", 400, "application/json");
                }
            }
        }
        private String convertListToJson() {
            StringBuilder json = new StringBuilder("[");
            synchronized (notices) {
                for (int i = 0; i < notices.size(); i++) {
                    json.append("\"").append(notices.get(i)).append("\"");
                    if (i < notices.size() - 1) json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String root = "src/web"; 
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File(root + path);
            
            if (file.exists()) {
                String mime = "text/html";
                if (path.endsWith(".css")) mime = "text/css";
                else if (path.endsWith(".js")) mime = "application/javascript";
                
                sendResponse(exchange, file, 200, mime);
            } else {
                String response = "404 Not Found (Check your src/web folder location)";
                sendResponse(exchange, response, 404, "text/plain");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    private static void sendResponse(HttpExchange exchange, File file, int statusCode, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, file.length());
        OutputStream os = exchange.getResponseBody();
        Files.copy(file.toPath(), os);
        os.close();
    }
}