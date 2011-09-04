package org.openmeetings.test.server;

import static org.openmeetings.client.beans.ClientConnectionBean.imgQuality;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.client.beans.ClientConnectionBean;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * @author sebastianwagner
 *
 */
public class TestGZipDeltaPackage extends TestCase {

	private static Logger log = Logger.getLogger(TestGZipDeltaPackage.class);
	
	@Test
	public void testTestSocket(){
		try {
			
			
			Robot robot = new Robot();
			
			Rectangle screenRectangle = new Rectangle(0,0,20,20);
			
			BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);
			
			int scaledWidth = Math.round(400 * ClientConnectionBean.imgQuality);
			int scaledHeight = Math.round(400 * ClientConnectionBean.imgQuality);
			
			Image img = imageScreen.getScaledInstance(scaledWidth, scaledHeight,Image.SCALE_SMOOTH);
			
			//BufferedImage.TYPE_INT_RGB //21234
			//TYPE_INT_ARGB //21729
			//TYPE_3BYTE_BGR //14795
			//TYPE_BYTE_GRAY //13202
			//TYPE_BYTE_BINARY //13812--
			//TYPE_BYTE_INDEXED //24545
			BufferedImage image = new BufferedImage(scaledWidth, scaledHeight,BufferedImage.TYPE_3BYTE_BGR);
			
			Graphics2D biContext = image.createGraphics();
			biContext.drawImage(img, 0, 0, null);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// Find a jpeg writer
			ImageWriter writer = null;
			Iterator<ImageWriter> iter = ImageIO
					.getImageWritersByFormatName("jpg");
			if (iter.hasNext()) {
				writer = iter.next();
			}
			writer.setOutput(out);
			ImageWriteParam iwparam = new JPEGImageWriteParam(
					Locale.getDefault());
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(imgQuality);

			writer.write(null, new IIOImage(image, null, null),
					iwparam);
			
			imageScreen.flush();
			
			byte[] payload = out.toByteArray();
			
			
			ByteArrayInputStream in = new ByteArrayInputStream(payload);
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
			//JPEGDecodeParam decpar = decoder.getJPEGDecodeParam();
			
			BufferedImage decodedImage = decoder.decodeAsBufferedImage();
			
			log.debug("decodedImage W: "+decodedImage.getWidth());
			log.debug("decodedImage H: "+decodedImage.getHeight());
			
			//log.debug("decodedImage "+);
			//decoder.setJPEGDecodeParam(encpar);
			

		} catch (Exception err) {
			log.error("[TestSocket] ",err);
		}
	}
	
//	private BufferedImage calcDeltaImage(BufferedImage image1, BufferedImage image2) throws Exception {
//		
//		BufferedImage fImage = new BufferedImage(image1.getWidth(), image2.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//		
//		for (int width = 0;width < image1.getWidth(); width ++) {
//			
//			for (int height = 0;height < image1.getHeight(); height ++) {
//				
//				int color1 = image1.getRGB(width, height);
//				int color2 = image2.getRGB(width, height);
//				
//				
//				
//			}
//			
//		}
//		
//	}

}
