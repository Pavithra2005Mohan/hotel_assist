package main.modules.roominfo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RoomInfoServer {
    public void start(int port) {
        try {
            HostelInterface stub = new RoomInfoImpl();
            
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(port);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(port);
            }

            registry.rebind("RoomInfo", stub);
            System.out.println("[Module 2] RMI Registry running on Port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}