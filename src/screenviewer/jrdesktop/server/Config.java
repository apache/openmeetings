package jrdesktop.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import jrdesktop.main;
import jrdesktop.utilities.InetAdrUtility;

/**
 * Config.java
 * @author benbac
 */

public class Config {

    public static String server_address = "127.0.0.1";
    public static int server_port = 6666;     
    public static boolean default_address = false;
    
    public static void loadConfiguration() {
        if (new File(main.SERVER_CONFIG_FILE).canRead())
            try {
                Properties properties = new Properties();            
                properties.load(new FileInputStream(main.SERVER_CONFIG_FILE));
                
                server_address = properties.get("server-address").toString(); 
                server_port = Integer.valueOf(properties.get("server-port").toString());                                          
                
                default_address = Boolean.valueOf(properties.getProperty("default-address"));
            }
            catch (Exception e) {
                e.getStackTrace();
            }
       else
            storeConfiguration();  
    }
    
    public static void storeConfiguration () {
        try {
            new File(main.SERVER_CONFIG_FILE).createNewFile();        
            Properties properties = new Properties();
            properties.put("server-address", server_address);
            properties.put("server-port", String.valueOf(server_port));        
            properties.put("default-address", String.valueOf(default_address));
        
            properties.store(new FileOutputStream(main.SERVER_CONFIG_FILE), 
                "jrdesktop server configuration file"); 
        } catch (Exception e) {
            e.getStackTrace();
        }            
    }    
    
    public static void SetConfiguration(int Port) { 
        server_address = InetAdrUtility.getLocalAdr().getHostAddress(); 
        server_port = Port;                           
        
        storeConfiguration();       
    }    
}
