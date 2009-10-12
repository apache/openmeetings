package org.openmeetings.client.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ImagePanel extends JPanel {
	
	private static Logger log = Logger.getLogger(ImagePanel.class);

	
	private int xPosition = 0;
	private int yPosition = 0;
	private Image image;
	
	private int sequenceNumber = 0;
	
	/**
	 * @param position
	 * @param position2
	 * @param image
	 *
	 * 20.09.2009 17:10:36
	 * sebastianwagner
	 * 
	 * 
	 */
	public ImagePanel(int xPosition, int yPosition) {
		super();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}


	@Override
    public void paintComponent(Graphics g) {
		try {
	        super.paintComponent(g);
	        
	        if (image != null) {
	
	        	log.debug("DRAW IMAGE "+this.xPosition+" "+this.yPosition);
	        	
	            boolean drawResult = g.drawImage(image, 0, 0, this);
	
	            while(!drawResult) {
	            	log.debug("Could Not Draw "+this.getX()+" "+this.getY());
	            	drawResult = g.drawImage(image, 0, 0, this);
	            }
	            
	            if (!drawResult) {
	            	throw new Exception("Could not Draw Image");
	            }
	        }
		} catch (Exception err) {
			log.error("[paintComponent]",err);
		}
    }
	
    public void setImages(Image image, int width, int height) {

        this.image = image;
        //log.debug("")
        //setPreferredSize(new Dimension(width,height));
        repaint();
        invalidate();

    }

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
    
}
