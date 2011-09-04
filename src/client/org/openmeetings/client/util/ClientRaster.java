package org.openmeetings.client.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientImageFrame;
import org.openmeetings.client.beans.ClientVirtualScreenBean;

/**
 * @author sebastianwagner
 *
 */
public class ClientRaster {
	
	private static Logger log = Logger.getLogger(ClientRaster.class);
	
	public static synchronized List<ClientImageFrame> tileScreen() {
		try {
			
			List<ClientImageFrame> clientImageFrames = new LinkedList<ClientImageFrame>();
			
			int tileNumberWidth = Double.valueOf(Math.floor(ClientVirtualScreenBean.vScreenSpinnerWidth / ClientConnectionBean.tileWidth)).intValue();
			int tileNumberHeight = Double.valueOf(Math.floor(ClientVirtualScreenBean.vScreenSpinnerHeight / ClientConnectionBean.tileHeight)).intValue();
			
			int xOffset = ClientVirtualScreenBean.vScreenSpinnerX;
			int yOffset = ClientVirtualScreenBean.vScreenSpinnerY;
			
			log.debug("ClientVirtualScreenBean.vScreenSpinnerWidth "+ClientVirtualScreenBean.vScreenSpinnerWidth);
			log.debug("ClientConnectionBean.tileWidth "+ClientConnectionBean.tileWidth);
			
			log.debug("ClientVirtualScreenBean.vScreenSpinnerWidth / ClientConnectionBean.tileWidth "+(ClientVirtualScreenBean.vScreenSpinnerWidth / ClientConnectionBean.tileWidth));
			log.debug("ClientVirtualScreenBean.vScreenSpinnerHeight / ClientConnectionBean.tileHeight "+(ClientVirtualScreenBean.vScreenSpinnerHeight / ClientConnectionBean.tileHeight));
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
					
					//log.debug("-1- rectWidth,rectHeight"+rectWidth+","+rectHeight);
					
					if (rect_x + rectWidth > screenSize.width) {
						rectWidth = screenSize.width - rect_x;
					}
					if (rect_y + rectHeight > screenSize.height) {
						rectHeight = screenSize.height - rect_y;
					}
					
					//log.debug("-2- rectWidth,rectHeight"+rectWidth+","+rectHeight);
					
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
					
//					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					
//					// Find a jpeg writer
//					ImageWriter writer = null;
//					Iterator<ImageWriter> iter = ImageIO
//							.getImageWritersByFormatName("jpg");
//					if (iter.hasNext()) {
//						writer = iter.next();
//					}
//					writer.setOutput(out);
//					ImageWriteParam iwparam = new JPEGImageWriteParam(
//							Locale.getDefault());
//					iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//					iwparam.setCompressionQuality(imgQuality);
//
//					writer.write(null, new IIOImage(image, null, null),
//							iwparam);
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					
					ImageWriter writer = ImageIO.getImageWritersByFormatName( "jpg" ).next(); 
				    ImageOutputStream ios = ImageIO.createImageOutputStream( out ); 
				    writer.setOutput( ios ); 
				    
				    
				    //ImageWriteParam param = writer.getDefaultWriteParam();
				    
				    //log.debug("Logs "+param.getCompressionType());
				    
				    //param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				    //param.setCompressionQuality(ClientConnectionBean.imgQualityDefault);

				    
				    ImageWriteParam iwparam = new JPEGImageWriteParam( Locale.getDefault() ); 
				    iwparam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT ) ; 
				    iwparam.setCompressionQuality( ClientConnectionBean.imgQualityDefault ); 
				    writer.write( null, new IIOImage(image, null, null), null ); 
				    
				    ios.flush(); 
				    writer.dispose(); 
				    ios.close(); 
					
					imageScreen.flush();
					
					byte[] payload = out.toByteArray();
					
					//GZip
					ByteArrayOutputStream byteGzipOut = new ByteArrayOutputStream();
			    	GZIPOutputStream gZipOut = new GZIPOutputStream(byteGzipOut);
			    	
			    	gZipOut.write(payload);
			    	gZipOut.close();
			    	
			    	image.flush();
			    	if (img!=null)img.flush();
			    	
			    	log.debug("byteGzipOut LENGTH "+byteGzipOut.toByteArray().length);
					log.debug("payload LENGTH "+payload.length);
					
					clientImageFrames.add(new ClientImageFrame(shrinkedRectAngle,byteGzipOut.toByteArray()));
				}
				
			}
			
			return clientImageFrames;
			
		} catch (Exception err) {
			log.error("[checkFrame]",err);
		}
		return null;
	}

}
