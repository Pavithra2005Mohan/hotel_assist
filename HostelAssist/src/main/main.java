package main;
import main.modules.noticeboard.NoticeServer;
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   HOSTEL ASSIST DISTRIBUTED SYSTEM");
        System.out.println("   Integrated Dashboard & Server");
        System.out.println("==========================================");
        try {         
            System.out.println("[INIT] Starting Module 3 (REST API & Web UI)...");
            new NoticeServer().start(8080);
            System.out.println("\n[SUCCESS] System is running!");
            System.out.println(">>> Open your browser at: http://localhost:8080");

        } catch (Exception e) {
            System.err.println("[ERROR] System start failure:");
            e.printStackTrace();
        }
    }
}