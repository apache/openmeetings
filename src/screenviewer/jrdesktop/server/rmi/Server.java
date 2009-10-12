package jrdesktop.server.rmi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JOptionPane;


import jrdesktop.SysTray;
import jrdesktop.main;
import jrdesktop.server.Config;
import jrdesktop.server.robot;
import jrdesktop.utilities.ZipUtility;

/**
 * Server.java
 * @author benbac
 */

public class Server extends Thread {
    
    private static boolean idle = true;
    private static boolean running = false;
    
    private static Registry registry;
    private static ServerImpl serverImpl;
    
    private static robot rt = new robot();
    
    private static ArrayList<Object> Objects = new ArrayList<Object>();            
    public static Hashtable<Integer, InetAddress> viewers = 
            new Hashtable<Integer, InetAddress>();        
        
    public static void Start() { 
        idle = false;
        running = false;                 
        Config.loadConfiguration();       
        
        if (Config.default_address)
            System.setProperty("java.rmi.server.hostname", Config.server_address);
        else
            System.getProperties().remove("java.rmi.server.hostname");        
         
        try{
            serverImpl = new ServerImpl();                   
            registry = LocateRegistry.createRegistry(Config.server_port); 
            registry.rebind("ServerImpl", serverImpl); 
        } catch (Exception e) {                  
            e.getStackTrace();
            Stop();          
            
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error !!",
                    JOptionPane.ERROR_MESSAGE); 
            return;
        }   

        System.out.println(getStatus());
        running = true;      
        SysTray.updateServerStatus(SysTray.SERVER_STARTED);        
    }              
    
    public static void Stop() {
        if (running) {
            running = false;
            disconnectAllViewers();
            SysTray.updateServerStatus(SysTray.SERVER_STOPPED);            
        }
        else
            SysTray.updateServerStatus(SysTray.CONNECTION_FAILED);
        try {            
            if (registry != null)
                UnicastRemoteObject.unexportObject(registry, true);            
        } catch (Exception e) {
            e.getStackTrace();
        }  
        registry = null;
        serverImpl = null;
    }
    
    public static boolean isRunning() {
        return running;
    }
    
    public static boolean isIdle() {
        return idle;
    }
        
    public static void updateData(byte[] data, int index) {  
    	System.out.println("Server Update Data!");
        Object object;
        try {
            object = ZipUtility.byteArraytoObject(data);
            
			rt.updateData(object); 
			
			
        }
        catch (Exception e) {
            e.getStackTrace();
            System.out.println("Server.update : " + e.getMessage());
        }       
    }

    public static byte[] updateData(int index) {  
    	System.out.println("Server Update Data index");
        byte[] data = null;            
        Objects.add(rt.CaptureScreenByteArray());     
        Objects.add(rt.getScreenRect());             

        synchronized(Objects) {
            try {
                data = ZipUtility.objecttoByteArray(Objects);
            } catch (IOException ioe) {
                ioe.getStackTrace();
            }
            Objects = new ArrayList<Object>();            
        }
        
        return data;   
    }  

    public static void AddObject(Object object) {
        Objects.add(object);
    } 
    
    public static synchronized int addViewer(InetAddress inetAddress) {
        int index = viewers.size();        
        viewers.put(index, inetAddress);            

        SysTray.displayViewer(inetAddress.toString(), index, true);        
        return index;
    }
         
    public static synchronized int removeViewer(int index) {
        String viewer = viewers.get(index).toString();
        
        viewers.remove(index);
        
        SysTray.displayViewer(viewer, viewers.size(), false);        
        return index;
    } 
    
    public static void disconnectAllViewers() {
        Enumeration<Integer> viewerEnum = viewers.keys();
        while (viewerEnum.hasMoreElements())
            removeViewer(viewerEnum.nextElement());
    }         
    
    public static ArrayList<InetAddress> getViewersAds () {
        ArrayList<InetAddress> viewersAds = new ArrayList<InetAddress>(); 
        for (int i=0; i<viewers.size(); i++)
            viewersAds.add(viewers.get(i));
        return viewersAds;
    }
    
    public static int getViewersCount () {
        return viewers.size();
    }    
    
    public static String getStatus() {
        String status = "Running ...at:\n\t" + 
            Config.server_address + ":" + Config.server_port;
        return status;
    }
}
