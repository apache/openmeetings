package jrdesktop.server;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import jrdesktop.utilities.ImageUtility;

/**
 * robot.java
 * @author benbac
 */

public class robot {

    private Robot rt;

    private Toolkit tk = null;
    private Rectangle screenRect;
    
    public robot() {
        tk = Toolkit.getDefaultToolkit();
        screenRect = new Rectangle(tk.getScreenSize()); 
        try {               
            rt = new Robot();
        }
        catch (AWTException awte) {
            awte.getStackTrace();
        }     
    }

    public BufferedImage captureScreen() {    
        screenRect = new Rectangle(tk.getScreenSize()); 
       
        return rt.createScreenCapture(screenRect); 
    }

    public byte[] CaptureScreenByteArray() {  
        return ImageUtility.toByteArray(captureScreen());
    }        
    
    public Rectangle getScreenRect() {  
        return screenRect;
    }  
    
    public void updateData(Object object) {            
        ArrayList Objects = (ArrayList) object;     
        for (int i=0; i<Objects.size(); i++) {            
            Object obj = Objects.get(i);

            if (obj instanceof MouseEvent)
                applyMouseEvent((MouseEvent)obj);
            else if (obj instanceof KeyEvent)
                applyKeyEvent((KeyEvent)obj);                            
        }
    }
    
    public void applyMouseEvent(MouseEvent evt) {
        rt.mouseMove(evt.getX(), evt.getY());
        int buttonMask = 0;
        int buttons = evt.getButton();
        if ((buttons == MouseEvent.BUTTON1)) buttonMask = InputEvent.BUTTON1_MASK;
        if ((buttons == MouseEvent.BUTTON2)) buttonMask |= InputEvent.BUTTON2_MASK;
        if ((buttons == MouseEvent.BUTTON3)) buttonMask |= InputEvent.BUTTON3_MASK;     
        switch(evt.getID()) {         
            case MouseEvent.MOUSE_PRESSED: rt.mousePress(buttonMask); break;
            case MouseEvent.MOUSE_RELEASED: rt.mouseRelease(buttonMask); break;
            case MouseEvent.MOUSE_WHEEL: rt.mouseWheel(
                    ((MouseWheelEvent) evt).getUnitsToScroll()); break;
        }          
    }
    
    public void applyKeyEvent(KeyEvent evt) {
        switch(evt.getID()) {
            case KeyEvent.KEY_PRESSED: rt.keyPress(evt.getKeyCode()); break;
            case KeyEvent.KEY_RELEASED: rt.keyRelease(evt.getKeyCode()); break; 
        }
    }       
}
