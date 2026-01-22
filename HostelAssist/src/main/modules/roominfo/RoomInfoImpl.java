package main.modules.roominfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RoomInfoImpl extends UnicastRemoteObject implements HostelInterface {
    
    // In-memory storage for room information
    private static Map<String, RoomData> roomDatabase = new HashMap<>();
    
    static {
        // Initialize sample room data
        roomDatabase.put("101", new RoomData("101", "John Doe, Jane Smith", "Mr. Smith", "9876543210", "smith@hostel.edu"));
        roomDatabase.put("102", new RoomData("102", "Alice Johnson, Bob Williams", "Mr. Smith", "9876543210", "smith@hostel.edu"));
        roomDatabase.put("201", new RoomData("201", "Charlie Brown, David Lee", "Ms. Johnson", "9876543211", "johnson@hostel.edu"));
        roomDatabase.put("202", new RoomData("202", "Emma Wilson, Frank Miller", "Ms. Johnson", "9876543211", "johnson@hostel.edu"));
        roomDatabase.put("301", new RoomData("301", "Grace Taylor, Henry Davis", "Mr. Anderson", "9876543212", "anderson@hostel.edu"));
        roomDatabase.put("302", new RoomData("302", "Iris White, Jack Black", "Mr. Anderson", "9876543212", "anderson@hostel.edu"));
    }
    
    public RoomInfoImpl() throws RemoteException {
        super();
    }

    @Override
    public String getRoomDetails(String roomNo) throws RemoteException {
        RoomData data = roomDatabase.get(roomNo);
        if (data != null) {
            return String.format("Room %s: Occupied by %s. Warden: %s.", 
                data.roomNumber, data.occupants, data.wardenName);
        }
        return "Room " + roomNo + ": Vacant / Not Found.";
    }
    
    @Override
    public String getWardenContact(String roomNo) throws RemoteException {
        RoomData data = roomDatabase.get(roomNo);
        if (data != null) {
            return String.format("Warden: %s | Phone: %s | Email: %s", 
                data.wardenName, data.wardenPhone, data.wardenEmail);
        }
        return "Room " + roomNo + " not found. No warden contact available.";
    }
    
    // Inner class for room data structure
    private static class RoomData {
        String roomNumber;
        String occupants;
        String wardenName;
        String wardenPhone;
        String wardenEmail;
        
        RoomData(String roomNumber, String occupants, String wardenName, 
                String wardenPhone, String wardenEmail) {
            this.roomNumber = roomNumber;
            this.occupants = occupants;
            this.wardenName = wardenName;
            this.wardenPhone = wardenPhone;
            this.wardenEmail = wardenEmail;
        }
    }
}