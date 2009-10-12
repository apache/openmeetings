package jrdesktop.server.rmi;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

/**
 * ServerImpl.java
 * @author benbac
 */

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {        
     
    public ServerImpl () throws RemoteException {} 
    
  /*  public ServerImpl (
            RMIClientSocketFactory csf,
            RMIServerSocketFactory ssf) throws RemoteException {
        super(0, csf, ssf);
    }  */ 
    
    //@Override
    public int startViewer(InetAddress inetAddress) throws RemoteException {
        return Server.addViewer(inetAddress);
    }
    
    //@Override
    public void stopViewer(int index) throws RemoteException {
        Server.removeViewer(index);
    }
    
   /* @Override
    public void updateOptions(Object data, int index) {
        Server.updateOptions(data, index);
    }*/
    
    //@Override
    public void updateData(byte[] data, int index) {
       Server.updateData(data, index); 
    }
    
    //@Override
    public byte[] updateData(int index) {
        return Server.updateData(index);
    }      
}
