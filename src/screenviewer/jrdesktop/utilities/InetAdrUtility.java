package jrdesktop.utilities;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * InetAdrUtility.java
 * @author benbac
 */

public class InetAdrUtility {

   public static InetAddress getLocalAdr() {
        try{
            return (InetAddress.getLocalHost());
        }
        catch(UnknownHostException uhe){
            uhe.getStackTrace();
            return null;
        }          
    }        
    
    public static String[] getLocalIPAdresses() {
        try {
            InetAddress inetAddress;
            ArrayList<String> hosts = new ArrayList<String>();
            Enumeration<NetworkInterface> ifaces =
                NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) { 
                    inetAddress = addrs.nextElement();
                    if (inetAddress instanceof Inet4Address)      
                        hosts.add(inetAddress.getHostAddress());
                }
            }
            return (String[]) hosts.toArray(new String[hosts.size()]);
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }        
    }
}
