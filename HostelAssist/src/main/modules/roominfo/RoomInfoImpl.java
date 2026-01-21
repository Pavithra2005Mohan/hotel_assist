package main.modules.roominfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RoomInfoImpl extends UnicastRemoteObject implements HostelInterface {
    
    public RoomInfoImpl() throws RemoteException {
        super();
    }

    @Override
    public String getRoomDetails(String roomNo) throws RemoteException {
        if (roomNo.equals("101")) return "Room 101: Occupied by John Doe. Warden: Mr. Smith.";
        return "Room " + roomNo + ": Vacant / Not Found.";
    }
}