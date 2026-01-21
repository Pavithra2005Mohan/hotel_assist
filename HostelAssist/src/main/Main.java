package main;

import main.modules.complaints.ComplaintServer;
import main.modules.roominfo.RoomInfoServer;
import main.modules.noticeboard.NoticeController;
import main.modules.p2p.PeerNode;
import main.modules.mess.MessController;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   HOSTEL ASSIST - DISTRIBUTED BACKEND");
        System.out.println("==========================================");

        try {
            new NoticeController().start(8080);

            new Thread(() -> new ComplaintServer().start(5000)).start();

            new RoomInfoServer().start(1099);

            new Thread(() -> new PeerNode().start(6000)).start();

            new MessController().start(8081);

            System.out.println("\n[SUCCESS] All systems running.");
            System.out.println(">>> Local Dashboard: http://localhost:8080");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}