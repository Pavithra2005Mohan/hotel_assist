package main.modules.complaints;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ComplaintService {
    // In-memory storage for complaints
    private static List<Complaint> complaints = new ArrayList<>();
    private static AtomicLong complaintIdCounter = new AtomicLong(1);
    
    public static class Complaint {
        private long id;
        private String roomNumber;
        private String category;
        private String description;
        private long timestamp;
        
        public Complaint(long id, String roomNumber, String category, String description) {
            this.id = id;
            this.roomNumber = roomNumber;
            this.category = category;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
        
        public long getId() { return id; }
        public String getRoomNumber() { return roomNumber; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("ID:%d|Room:%s|Category:%s|Desc:%s|Time:%d", 
                id, roomNumber, category, description, timestamp);
        }
    }
    
    public static String processComplaint(String complaintData) {
        try {
            // Parse complaint: format "ROOM_NUMBER|CATEGORY|DESCRIPTION"
            String[] parts = complaintData.split("\\|", 3);
            if (parts.length < 3) {
                return "ERROR: Invalid complaint format. Expected: ROOM_NUMBER|CATEGORY|DESCRIPTION";
            }
            
            String roomNumber = parts[0].trim();
            String category = parts[1].trim();
            String description = parts[2].trim();
            
            // Validate category
            if (!isValidCategory(category)) {
                return "ERROR: Invalid category. Must be: Water, Electricity, Cleanliness, or Other";
            }
            
            // Create and store complaint
            long id = complaintIdCounter.getAndIncrement();
            Complaint complaint = new Complaint(id, roomNumber, category, description);
            synchronized (complaints) {
                complaints.add(complaint);
            }
            
            System.out.println("[Service] Complaint Registered: " + complaint);
            return "ACK: Complaint Registered. Ticket ID #" + id + " | Room: " + roomNumber + " | Category: " + category;
            
        } catch (Exception e) {
            System.err.println("[Service] Error processing complaint: " + e.getMessage());
            return "ERROR: Failed to process complaint. " + e.getMessage();
        }
    }
    
    private static boolean isValidCategory(String category) {
        String cat = category.toLowerCase();
        return cat.equals("water") || cat.equals("electricity") || 
               cat.equals("cleanliness") || cat.equals("other");
    }
    
    public static List<Complaint> getAllComplaints() {
        synchronized (complaints) {
            return new ArrayList<>(complaints);
        }
    }
    
    public static int getComplaintCount() {
        synchronized (complaints) {
            return complaints.size();
        }
    }
}