package org.openmeetings.server;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
//import org.apache.tools.bzip2.CBZip2InputStream;
//import org.apache.tools.bzip2.CBZip2OutputStream;
import org.openmeetings.client.beans.ClientConnectionBean;

//import com.sun.imageio.plugins.png.

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author sebastianwagner
 *
 */
public class TestPNGZipPackage extends TestCase {

	private static Logger log = Logger.getLogger(TestPNGZipPackage.class);
	
	
	public TestPNGZipPackage(String testname){
		super(testname);
	}
	
	public void testTestSocket(){
		try {
			
			
			Robot robot = new Robot();
			
			Rectangle screenRectangle = new Rectangle(0,0,400,400);
			
			BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			//PNGEncoder
			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam encpar = encoder.getDefaultJPEGEncodeParam(imageScreen);
			encpar.setQuality(ClientConnectionBean.imgQuality, false);
			encoder.setJPEGEncodeParam(encpar);
			encoder.encode(imageScreen);
			
			imageScreen.flush();
			
			byte[] payload = out.toByteArray();
			
			
			//GZip
			ByteArrayOutputStream byteGzipOut = new ByteArrayOutputStream();
	    	GZIPOutputStream gZipOut = new GZIPOutputStream(byteGzipOut);
	    	
	    	gZipOut.write(payload);
	    	gZipOut.close();
	    	
	    	
	    	//BZip2
//			ByteArrayOutputStream bytebzip2Out = new ByteArrayOutputStream();
//	    	CBZip2OutputStream bZip2Out = new CBZip2OutputStream(bytebzip2Out);
//	    	
//	    	bZip2Out.write(payload);
//	    	bZip2Out.close();
	    	
	    	log.debug("byteGzipOut LENGTH "+byteGzipOut.toByteArray().length);
			log.debug("payload LENGTH "+payload.length);
			
			log.debug("JPEG RAW: "+payload.length);
    		log.debug("JPEG GZIP: "+byteGzipOut.toByteArray().length);
//    		log.debug("JPEG BZIP2: "+bytebzip2Out.toByteArray().length);
    		
			String imagePath_1 = "pic_.jpg";
			FileOutputStream fos_1 = new FileOutputStream(imagePath_1);
			fos_1.write(payload);
			fos_1.close();
			
			
			byte[] myBytes = byteGzipOut.toByteArray();
			
			
			String imagePath = "pic_.gzip";
			
			FileOutputStream fos = new FileOutputStream(imagePath);
			fos.write(myBytes);
			fos.close();
			
			ByteArrayInputStream byteGzipIn = new ByteArrayInputStream(myBytes);
    		GZIPInputStream gZipIn = new GZIPInputStream(byteGzipIn);

    		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    		
    		byte[] buffer = new byte[1024];
    		int count = 0;
    		while ((count = gZipIn.read(buffer)) > 0 ){
    			bytesOut.write(buffer,0,count);
			}
			bytesOut.close();
			gZipIn.close();
			
			log.debug("gZipIn CLosed");
			
			
//			ByteArrayInputStream byteBZip2In = new ByteArrayInputStream(myBytes);
//    		CBZip2InputStream bZipIn = new CBZip2InputStream(byteGzipIn);
//
//    		ByteArrayOutputStream bytesOut_bzip2 = new ByteArrayOutputStream();
//    		
//    		byte[] buffer_bzip2 = new byte[1024];
//    		int count_1 = 0;
//    		while ((count_1 = gZipIn.read(buffer_bzip2)) > 0 ){
//    			bytesOut_bzip2.write(buffer_bzip2,0,count_1);
//			}
//    		bytesOut_bzip2.close();
//    		bZipIn.close();
//    		
//    		log.debug("bZipIn CLosed");
    		
    		
		} catch (Exception err) {
			log.error("[TestSocket] ",err);
		}
	}

}
