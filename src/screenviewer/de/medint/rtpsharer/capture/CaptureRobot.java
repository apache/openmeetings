package de.medint.rtpsharer.capture;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Class for Taking a scaled screenshot
 * @author o.becherer
 *
 */
public class CaptureRobot {
	
	/** AWTRobot */
	private Robot rt = null;
	
	/** Rectangle, from which screencapture is taken*/
	private Rectangle captureDimension;
	
	/** Point on x axis to define start of capture rectangle*/
	private int captureX = 0;
	
	/** Point on y axis to define start of capture rectangle*/
	private int captureY = 0;
	
	/** Capture Width */
	private int captureWidth = 0;
	
	/** Capture Height */
	private int captureHeight = 0;
	
	/** ScreenDimension*/
	private Dimension screenDimension;
	
	/** if <> width, image will be scaled to that dimension*/
	private int scaledWidth = 0;
	
	/** if <> height, image will be scaled to that dimension*/
	private int scaledHeight = 0;
	
	/** ImageType*/
	private int imageType = BufferedImage.TYPE_3BYTE_BGR;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param scaledWidth
	 * @param scaledHeight
	 * @param imageType
	 * @throws Exception
	 */
	//---------------------------------------------------------------------------------------------------------
	public void init(int x, int y, int width, int height, int scaledWidth, int scaledHeight, int imageType) throws Exception{
		
		
		// define ScreenDimensions
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		
		// check inputParams
		checkParams(x, y, width, height, scaledWidth, scaledHeight);
		
		// Take over checked values
		captureX = x;
		captureY = y;
		captureWidth = width;
		captureHeight = height;
		this.scaledHeight = scaledHeight;
		this.scaledWidth = scaledWidth;
		this.imageType = imageType;
		
		// prepare CaptureLocation
		captureDimension = new Rectangle(Double.valueOf(screenDimension.getWidth()).intValue(), Double.valueOf(screenDimension.getHeight()).intValue());
		captureDimension.setLocation(x, y);
		
		rt = new Robot();
	}
	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param scaledWidth
	 * @param scaledHeight
	 * @throws Exception
	 */
	//----------------------------------------------------------------------------------------------------------------
	private void checkParams(int x, int y, int width, int height, int scaledWidth, int scaledHeight) throws Exception{
		
		// Check X
		if(x < 0)
			throw new Exception("Invalid X ccordinate for Capture - may not be < 0");
		else if(x > screenDimension.getWidth())
			throw new Exception("Invalid X ccordinate for Capture - may not exceed ScreenWidth(" + screenDimension.getWidth() + ")");
		
		// Check Y
		if(y < 0)
			throw new Exception("Invalid Y ccordinate for Capture - may not be < 0");
		else if(y > screenDimension.getHeight())
			throw new Exception("Invalid X ccordinate for Capture - may not exceed Screenheight(" + screenDimension.getHeight() + ")");
		
		
		// Check Rectangle Width
		if(width < 0)
			throw new Exception("Invalid Width for Capture - may not be < 0");
		else if((x + width) > screenDimension.getWidth())
			throw new Exception("Invalid Width for Capture - may not exceed ScreenDimensions");
		
		// Check Rectangle Height
		if(height < 0)
			throw new Exception("Invalid Height for Capture - may not be < 0");
		else if((y + height) > screenDimension.getHeight())
			throw new Exception("Invalid Height for Capture - may not exceed ScreenDimensions");
		
		// Check Scaled Dimensions
		if(scaledWidth < 0 || scaledHeight < 0)
			throw new Exception("Invalid Scaling Dimensions!  < 0");
		

	}
	//----------------------------------------------------------------------------------------------------------------


	/**
	 * Get ScreenCapture (gets scaled, if defined via initParams
	 */
	//----------------------------------------------------------------------------------------------------------------
	public BufferedImage getCapture(){
		
		if(rt == null)
			throw new RuntimeException("Robot not initialized!");
		
		BufferedImage original = rt.createScreenCapture(captureDimension);
		
		// no need to scale
		if(scaledHeight == captureHeight && scaledWidth == captureWidth){
			return convertImage(original, imageType);
		}
		else{
			BufferedImage scaled = scaleImage(original, true);
			return convertImage(scaled, imageType);
		}
	}
	//----------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * stepwise downscaling Image
	 */
	//----------------------------------------------------------------------------------------------------------------
	private BufferedImage scaleImage(BufferedImage input, boolean higherQuality){
		
		
		if(input ==  null)
			throw new RuntimeException("Error on scaling : input null");
		
		
		BufferedImage ret = (BufferedImage)input;
		
		int w, h;
		
    	if (higherQuality) {
    		// Use multi-step technique: start with original size, then
    		// scale down in multiple passes with drawImage()
    		// until the target size is reached
    		w = input.getWidth();
    		h = input.getHeight();
    	} else {
    		// Use one-step technique: scale directly from original
    		// size to target size with a single drawImage() call
    		w = scaledWidth;
    		h = scaledHeight;
    	}
    	
    	do {
    		if (higherQuality && w > scaledWidth) {
    			w /= 2;
    			if (w < scaledWidth) {
    				w = scaledWidth;
    			}
    		}

    		if (higherQuality && h > scaledHeight) {
    			h /= 2;
    			if (h < scaledHeight) {
    				h = scaledHeight;
    			}
    		}

    		BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g2 = tmp.createGraphics();
    		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    		g2.drawImage(ret, 0, 0, w, h, null);
    		g2.dispose();

    		ret = tmp;
    		
    		
    	} while (w != scaledWidth || h != scaledHeight);

    	
    	return ret;
	}
	//----------------------------------------------------------------------------------------------------------------
	
	 
	/**
     * 
     * @param original
     * @return
     */
	//----------------------------------------------------------------------------------------------------------------
	public static BufferedImage convertImage(BufferedImage original, int type){
		
		if(original == null){
    		throw new RuntimeException("Conversion Error : null input");
    	}
		
		if(type == original.getType())
			return original;
    	
    	BufferedImage bgrImage = new BufferedImage(original.getWidth(), original.getHeight(), type); 
    	bgrImage.getGraphics().drawImage(original, 0, 0, null); 
    	
    	return bgrImage;
     }
	//----------------------------------------------------------------------------------------------------------------
	
}
