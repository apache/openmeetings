package jrdesktop.viewer.rmi;

import jrdesktop.main;
import jrdesktop.server.rmi.ServerImpl;
import jrdesktop.server.rmi.ServerInterface;
import jrdesktop.viewer.Recorder;
import jrdesktop.utilities.InetAdrUtility;
import jrdesktop.utilities.ZipUtility;
import jrdesktop.viewer.Config;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JOptionPane;


/**
 * Viewer.java
 * @author benbac
 */

public class Viewer extends Thread {
    
    private int index = -1;
    private Recorder recorder;
    
    private Registry registry; 
    private ServerInterface rmiServer;
       
    private String server = "127.0.0.1";
    private int port = 6666;
    
    private boolean connected = false;
    
    private ArrayList<Object> Objects;        
        
    public Viewer () {
        Config.loadConfiguration();
        server = Config.server_address;
        port = Config.server_port;              
    }   
    
    public boolean isConnected() {
        return connected;
    }
    
    public void Start() { 
        System.out.println("Viewer.Start(9");
        
        try{
        	rmiServer = new ServerImpl();
        }catch(Exception e){
        	System.out.println("Viewer.start() : " + e.getMessage());
        }
        
    	Objects = new ArrayList<Object>();
    	
    	recorder = new Recorder(this);
    	
    	recorder.Start();
    	
    	/*
    	connect();
        if (connected) {
                    
            
        } 
              
        else Stop();
        */ 
    }
    
    /*
    public void Stop() {
        if (recorder != null) {
            recorder.Stop();
            recorder.interrupt();
        }
        disconnect();    
        interrupt();
    }
    */
    /*
    public int connect() {  
        connected = false;
        
        try {       
            registry = LocateRegistry.getRegistry(server, port);        
          
            rmiServer = (ServerInterface) registry.lookup("ServerImpl");                          
         
            index = rmiServer.startViewer(InetAdrUtility.getLocalAdr());                        
            
            displayStatus();
            Objects = new ArrayList<Object>();
            connected = true;
            return index;
       } catch (Exception e) {    
           JOptionPane.showMessageDialog(null, e.getMessage(), "Error !!",
                    JOptionPane.ERROR_MESSAGE);
           return -1;
       }     
    }
    */
    /*
    public void disconnect() {
        connected = false;
        try {
            if (rmiServer != null) {
                    rmiServer.stopViewer(index);
                    UnicastRemoteObject.unexportObject(rmiServer, true);
            }
        }
        catch (Exception e) {
            e.getStackTrace(); 
        } 
      rmiServer = null;
      registry = null;
    }
    */
    public void updateData(Object object) {
    	System.out.println("Viewer.updateData (Object)");
        byte[] data;
        try {
            data = ZipUtility.objecttoByteArray(object);

            updateData(data);  
        }
        catch (IOException e) {
            e.getStackTrace();
        }
    }
    
    public void updateData(byte[] data) {
    	System.out.println("Viewer.updateData (byte[])");
        try {
        	
        	rmiServer.updateData(data, index);
        	 
        } 
        catch (Exception re) {
            re.getStackTrace();
        }        
    }
    
    public void AddObject(Object object) {
        Objects.add(object);
    }   
   
   /* public void updateOptions () {     
        try {rmiServer.updateOptions(
                recorder.getViewerData(), index);
        } 
        catch (Exception re) {
            re.getStackTrace();
        }          
    }*/
    
    public void sendData() {
    	System.out.println("Viewer.sendData");
        ArrayList SendObjects;       
        synchronized(Objects){                       
            
            SendObjects = Objects; 
            Objects = new ArrayList<Object>();
        }
        updateData(SendObjects);
    }     
    
    public void recieveData() {
    	System.out.println("Viewer.receiveData");
        Object object = null;
        try {
            byte[] data = rmiServer.updateData(index);
            object = ZipUtility.byteArraytoObject(data);   
            
            String endUrl = main.OPENMEETINGS_URL + "?" +
            				"sid="+main.OPENMEETINGS_SID+"" +
            				"&room="+main.OPENMEETINGS_ROOM+"" +
            				"&domain="+main.OPENMEETINGS_DOMAIN+"" +
    						"&publicSID="+main.OPENMEETINGS_PUBLICSID +
    						"&record="+main.OPENMEETINGS_RECORDER;
            
            String fileName = "myscreenRemote.jpg";
            
            System.out.println("sendJpegToUrl url  " +endUrl);
			
			URL u = new URL(endUrl);
			URLConnection c = u.openConnection();

			// post multipart data
			c.setDoOutput(true);
			c.setDoInput(true);
			c.setUseCaches(false);

			// set request headers
			c.setRequestProperty("Content-Type","multipart/form-data; boundary=AXi93A");

			// open a stream which can write to the url
			DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

			// write content to the server, begin with the tag that says a content element is comming
			dstream.writeBytes("--AXi93A\r\n");

			
			System.out.println("data length:" + data.length);
			
			// discribe the content
			dstream.writeBytes("Content-Disposition: form-data; name=\"Filedata\"; filename=\""+fileName+"\" \r\nContent-Type: image/jpeg\r\nContent-Transfer-Encoding: binary\r\n");
			dstream.write(data, 0, data.length);

			// close the multipart form request
			dstream.writeBytes("\r\n--AXi93A--\r\n\r\n");
			dstream.flush();
			dstream.close();
			
			System.out.println("sendJpegToUrl complete ");
			
			
		    // read the output from the URL
			DataInputStream in = new DataInputStream(
					new BufferedInputStream(c.getInputStream()));
			String sIn = in.readLine();
			while (sIn != null) {
				if (sIn != null) {
					System.out.println(sIn);
				}
				sIn += in.readLine();
			}
                         
            recorder.updateData((ArrayList) object);    
        }
        catch (Exception e) {
            e.getStackTrace();
            System.out.println("Viewer" + e.getMessage());
        }           
    }       
    
    public void displayStatus() {          
        System.out.println("Viewer connected to " + rmiServer);        
    }
}
