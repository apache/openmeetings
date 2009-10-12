package org.openmeetings.screen.codec;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientImageFrame;
import org.openmeetings.client.beans.ClientVirtualScreenBean;
import org.openmeetings.screen.codec.beans.ClientImageFrameModus;


/**
 * @author sebastianwagner
 *
 */
public class ClientRasterByMode {
	
	private static Logger log = Logger.getLogger(ClientRasterByMode.class);
	
	
	public static synchronized List<ClientImageFrameModus> tileScreen() {
		try {
			
			List<ClientImageFrameModus> clientImageFrames = new LinkedList<ClientImageFrameModus>();
			
			int tileNumberWidth = Double.valueOf(Math.floor(ClientVirtualScreenBean.vScreenSpinnerWidth / ClientConnectionBean.tileWidth)).intValue();
			int tileNumberHeight = Double.valueOf(Math.floor(ClientVirtualScreenBean.vScreenSpinnerHeight / ClientConnectionBean.tileHeight)).intValue();
			
			int xOffset = ClientVirtualScreenBean.vScreenSpinnerX;
			int yOffset = ClientVirtualScreenBean.vScreenSpinnerY;
			
			log.debug("tileNumberWidth,tileNumberHeight "+tileNumberWidth+","+tileNumberHeight);
			log.debug("xOffset,yOffset "+xOffset+","+yOffset);
			
			Robot robot = ClientVirtualScreenBean.robot;
			if (robot==null) robot = new Robot();
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			
			for (int x=0;x<=tileNumberWidth;x++) {
				
				for (int y=0;y<=tileNumberHeight;y++) {
					
					int rect_x = xOffset + ( x * ClientConnectionBean.tileWidth );
					int rect_y = yOffset + ( y * ClientConnectionBean.tileHeight );
					
					log.debug("rect_x,rect_y,tileWidth,tileHeight "+rect_x+","+rect_y+","+ClientConnectionBean.tileWidth+","+ClientConnectionBean.tileHeight);
					
					int rectWidth = ClientConnectionBean.tileWidth;
					int rectHeight = ClientConnectionBean.tileHeight;
					
					if (rect_x + rectWidth > screenSize.width) {
						rectWidth = screenSize.width - rect_x;
					}
					if (rect_y + rectHeight > screenSize.height) {
						rectHeight = screenSize.height - rect_y;
					}
					
					if (rectWidth == 0 || rectHeight == 0) {
						continue;
					}
					
					Rectangle screenRectangle = new Rectangle(rect_x,rect_y,rectWidth,rectHeight);
					
					Rectangle shrinkedRectAngle =  new Rectangle(Math.round(rect_x * ClientConnectionBean.imgQuality) , Math.round(rect_y * ClientConnectionBean.imgQuality),
							Math.round(rectWidth * ClientConnectionBean.imgQuality), Math.round(rectHeight * ClientConnectionBean.imgQuality));
					
					
					BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);
					
					int scaledWidth = Math.round(rectWidth * ClientConnectionBean.imgQuality);
					int scaledHeight = Math.round(rectHeight * ClientConnectionBean.imgQuality);
					
					Image img = imageScreen.getScaledInstance(scaledWidth, scaledHeight,Image.SCALE_SMOOTH);
					
					//BufferedImage.TYPE_INT_RGB
					//TYPE_3BYTE_BGR
					BufferedImage image = new BufferedImage(scaledWidth, scaledHeight,BufferedImage.TYPE_3BYTE_BGR);
					
					Graphics2D biContext = image.createGraphics();
					biContext.drawImage(img, 0, 0, null);
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					
					ImageWriter writer = ImageIO.getImageWritersByFormatName( "gif" ).next(); 
				    ImageOutputStream ios = ImageIO.createImageOutputStream( out ); 
				    writer.setOutput( ios ); 
				    
				    writer.write( null, new IIOImage(image, null, null), null ); 
				    
				    ios.flush(); 
				    writer.dispose(); 
				    ios.close(); 
					
					imageScreen.flush();
					
					byte[] payload = out.toByteArray();
					
			    	image.flush();
			    	
			    	clientImageFrames.add(new ClientImageFrameModus(img,payload,shrinkedRectAngle,rectWidth,rectHeight));
			    	
			    	img.flush();
			    	
					log.debug("payload LENGTH "+payload.length);
					
					
				}
				
			}
			
			return clientImageFrames;
			
		} catch (Exception err) {
			log.error("[tileScreen]",err);
		}
		return null;
	}

}
