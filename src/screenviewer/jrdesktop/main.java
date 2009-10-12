package jrdesktop;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import jrdesktop.server.rmi.Server;
import jrdesktop.viewer.rmi.Viewer;

/**
 * main.java
 * @author benbac
 */

public class main {
    
    public static final URL IDLE_ICON = main.class.getResource("images/idle.png");
    public static final URL ALIVE_ICON = main.class.getResource("images/background.png");        
    public static final URL WAIT_ICON = main.class.getResource("images/display.png");
    public static final URL START_ICON = main.class.getResource("images/player_play.png");
    public static final URL STOP_ICON = main.class.getResource("images/player_stop.png");
 
    public static String CONFIG_FILE;
    public static String SERVER_CONFIG_FILE;
    public static String VIEWER_CONFIG_FILE;
    
    /** Connection Values for URLConnection to OpenMeetings */
    public static String OPENMEETINGS_URL;
    public static String OPENMEETINGS_SID;
    public static String OPENMEETINGS_ROOM;
    public static String OPENMEETINGS_DOMAIN;
    public static String OPENMEETINGS_PUBLICSID;
    public static String OPENMEETINGS_RECORDER;
    
        
    public static void main (String args[]) {          
        if (System.getSecurityManager() == null)
	    System.setSecurityManager(new SecurityMng());   
        
        OPENMEETINGS_URL = args[0];
        OPENMEETINGS_SID = args[1]; 
        OPENMEETINGS_ROOM = args[2];
        OPENMEETINGS_DOMAIN = args[3];
        OPENMEETINGS_PUBLICSID = args[4];
        OPENMEETINGS_RECORDER = args[5];

        CONFIG_FILE = getCurrentDirectory() + "config";
        SERVER_CONFIG_FILE = getCurrentDirectory() + "server.config";
        VIEWER_CONFIG_FILE = getCurrentDirectory() + "viewer.config";       
    
        
        //System.getProperties().remove("java.rmi.server.hostname");        
        //startServer(6666);  
        System.out.println("Viewer start");
        
        startViewer("localhost", 6666);
       
        
        
        Config.loadConfiguration();
        if (!Config.Systray_disabled)
            SysTray.Show();
        
        /*
        if (!Config.GUI_disabled)
            mainFrame.main(null);
            */         
    }      
       
   
    
    public static void startServer(int port) {
        
        jrdesktop.server.Config.SetConfiguration(port);
        
        Server.Start();
    }    
    
    public static void startViewer(String server, int port) {        
        jrdesktop.viewer.Config.SetConfiguration(server, port);       
        new Viewer().Start();     
    }                

    public static String getCurrentDirectory () {
        String currentDirectory = null;
        try {
            currentDirectory = new File(".").getCanonicalPath() + File.separatorChar;            
        } catch (IOException e) {
            e.getStackTrace();
        }
        return currentDirectory;
    } 
    
    public static void exit() {
        if (Server.isRunning())       
            Server.Stop();
        System.setSecurityManager(null);
        System.exit(0);
    }
}
