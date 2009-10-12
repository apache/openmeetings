package jrdesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Config.java
 * @author benbac
 */

public class Config {
    
    public static boolean GUI_disabled = true;
    
    public static boolean Systray_disabled = false;
    
    public static void loadConfiguration() {
        if (new File(main.CONFIG_FILE).canRead())
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(main.CONFIG_FILE));
                GUI_disabled = Boolean.valueOf(properties.getProperty("GUI-disabled"));
                Systray_disabled = Boolean.valueOf(properties.getProperty("Systray-disabled"));         
            }
            catch (Exception e) {
                e.getStackTrace();
            }
       else
            storeConfiguration();  
    }
    
    public static void storeConfiguration () {
        try {
            new File(main.CONFIG_FILE).createNewFile();        
            Properties properties = new Properties();
            properties.put("GUI-disabled", String.valueOf(GUI_disabled));
            properties.put("Systray-disabled", String.valueOf(Systray_disabled));        
            properties.store(new FileOutputStream(main.CONFIG_FILE), 
                "jrdesktop configuration file"); 
            
            
        } catch (Exception e) {
            e.getStackTrace();
        }            
    }            
}
