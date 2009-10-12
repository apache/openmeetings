package jrdesktop.utilities;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/**
 * ImageUtility.java
 * @author benbac
 */

public class ImageUtility {

    static {ImageIO.setUseCache(false);}

    public static BufferedImage read(InputStream in) throws IOException {
        BufferedImage image = null;  
        image = ImageIO.read(in);
        if (image == null)
            throw new IOException("Read fails");                  
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
