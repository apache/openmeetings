package jrdesktop.viewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JLabel;

import jrdesktop.utilities.ImageUtility;

/**
 * ScreenPlayer.java
 * @author benbac
 */

public class ScreenPlayer extends JLabel {
      
    private Recorder recorder;
    
    private Image img;
    
    private Rectangle screenRect = new Rectangle(0, 0, 0, 0);
    private Rectangle oldScreenRect = new Rectangle(-1, -1, -1, -1);    
    
    private KeyAdapter keyAdapter;
    private MouseAdapter mouseAdapter;    
    private MouseWheelListener mouseWheelListener;
    private MouseMotionAdapter mouseMotionAdapter;    
   
    public ScreenPlayer(Recorder recorder) { 
        this.recorder = recorder;
        
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                ScreenPlayer.this.recorder.viewer.AddObject(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e){
                ScreenPlayer.this.recorder.viewer.AddObject(e);
            }          
        };
       
        mouseWheelListener = new MouseWheelListener() {
            //@Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                ScreenPlayer.this.recorder.viewer.AddObject(e);
            }
        };
        
        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ScreenPlayer.this.recorder.viewer.AddObject(e);
            } 
            
            @Override
            public void mouseDragged(MouseEvent e) {
                ScreenPlayer.this.recorder.viewer.AddObject(e);                     
            }             
        };  
        
        mouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                ScreenPlayer.this.recorder.viewer.AddObject(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {                
                 ScreenPlayer.this.recorder.viewer.AddObject(e);          
            }        
        };    
        
        setFocusable(true);  
    };

    public void addAdapters() {
        addKeyListener(keyAdapter); 
        addMouseWheelListener(mouseWheelListener);
        addMouseMotionListener(mouseMotionAdapter);
        addMouseListener(mouseAdapter);        
    }
     
    public void removeAdapters() {
        removeKeyListener(keyAdapter);
        removeMouseWheelListener(mouseWheelListener);
        removeMouseMotionListener(mouseMotionAdapter);
        removeMouseListener(mouseAdapter);
    }  
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, screenRect.width, screenRect.height, this);            
    }

    public void setScteenRect(Rectangle rect) {
        screenRect = rect;
    }
    
    public void UpdateScreen(byte[] data) { 
    	System.out.println("ScreenPlayer : updateData : " + data.length);
        if (!screenRect.equals(oldScreenRect)) {
               oldScreenRect = screenRect;
               setSize(screenRect.getSize());
               setPreferredSize(screenRect.getSize());   
        }

        img = ImageUtility.read(data);  
        repaint();  
    }
    
    public void clearScreen() {
        setSize(new Dimension(1, 1));
        setPreferredSize(new Dimension(1, 1));
        img = createImage(getWidth(), getHeight());
        repaint();
        oldScreenRect = new Rectangle(-1, -1, -1, -1); 
    }    
    
    public boolean isScreenRectChanged () {
        boolean bool = (!screenRect.equals(oldScreenRect));     
        oldScreenRect = screenRect;
        return bool;
    }
}