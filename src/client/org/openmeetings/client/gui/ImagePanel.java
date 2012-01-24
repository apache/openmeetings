/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.client.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -7984328751866538787L;


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
