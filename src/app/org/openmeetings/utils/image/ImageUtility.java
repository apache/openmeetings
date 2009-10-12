package org.openmeetings.utils.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


	
public class ImageUtility {

	 static {ImageIO.setUseCache(false);}

	 public static java.awt.image.BufferedImage read(java.io.InputStream in) throws IOException {
	        java.awt.image.BufferedImage image = null;  
	        image = ImageIO.read(in);
	        if (image == null)
	            throw new java.io.IOException("Read fails");                  
	        return image;
	    }
	 
	    public static BufferedImage read(byte[] bytes) {
	        try {
	            return read(new ByteArrayInputStream(bytes));
	        } catch (IOException e) {
	            e.getStackTrace();
	            return null;
	        }
	    } 

	    public static byte[] toByteArray(BufferedImage image) {
	        try {
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            ImageIO.write(image, "jpeg", out); // write without compression                                
	            return out.toByteArray();
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	}


