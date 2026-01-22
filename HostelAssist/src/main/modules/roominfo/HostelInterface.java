package main.modules.roominfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HostelInterface extends Remote {
    String getRoomDetails(String roomNo) throws RemoteException;
    String getWardenContact(String roomNo) throws RemoteException;
}