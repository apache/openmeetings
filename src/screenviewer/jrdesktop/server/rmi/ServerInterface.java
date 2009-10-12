package jrdesktop.server.rmi;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ServerInterface.java
 * @author benbac
 */

public interface ServerInterface extends Remote {
    public void updateData(byte[] data, int index) throws RemoteException;
    public byte[] updateData(int index) throws RemoteException;
   // public void updateOptions(Object data, int index) throws RemoteException;
    public void stopViewer(int index) throws RemoteException;
    public int startViewer(InetAddress inetAddress) throws RemoteException;
}
