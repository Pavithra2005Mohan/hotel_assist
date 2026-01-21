package main.modules.complaints;

public class ComplaintService {
    public static String processComplaint(String complaint) {
        System.out.println("[Service] Processing Complaint: " + complaint);
        long id = System.currentTimeMillis() % 10000;
        return "ACK: Complaint Registered. Ticket ID #" + id;
    }
}