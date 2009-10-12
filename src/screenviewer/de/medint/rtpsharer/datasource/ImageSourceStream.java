package de.medint.rtpsharer.datasource;



import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import de.medint.rtpsharer.capture.CaptureRobot;

/**
 * ImageSourceStream
 * @author o.becherer
 *
 */
class ImageSourceStream implements PushBufferStream, Runnable {
	
	/** Running?*/
    boolean run = true;

    /** Dimensions of Stream */	
	int width, height;
	
	/** Chosen Format */
	VideoFormat format;
	
	/** data */
	protected int [] data;

	boolean ended = false;

	/** Our Capture Robot */
	private CaptureRobot robot = new CaptureRobot();
	
	/** Packet Counter */
	static int counter = 0;
	
	protected boolean started;
	
	protected Thread thread;
	
	protected BufferTransferHandler transferHandler;
	
	protected int maxDataLength;
	
	/** FrameRate */
	protected float frameRate = 1f;
	
	int seqNo = 0;
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param frameRate
	 */
	public ImageSourceStream(int width, int height, int frameRate) {
	    this.width = width;
	    this.height = height;
	    this.frameRate = frameRate;
	  
	    
	    maxDataLength = width * height *3;
	    Dimension size = new Dimension(width, height);
	    
	    format = new RGBFormat(size, maxDataLength,
				  Format.intArray,
				  frameRate,
				  32,
				  0xFF0000, 0xFF00, 0xFF,
				  1, size.width,
				  VideoFormat.FALSE,
				  Format.NOT_SPECIFIED);
	    
	    
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    try{
	    	// init robot
	    	robot.init(0, 0, Double.valueOf(d.getWidth()).intValue(), Double.valueOf(d.getHeight()).intValue(), width,height, BufferedImage.TYPE_3BYTE_BGR);
	    }catch(Exception e){
	    	throw new RuntimeException(e.getMessage());
	    }
	    
	    data = new int[maxDataLength];
		thread = new Thread(this, "Screen Grabber");
	}

	/**
	 * We should never need to block assuming data are read from files.
	 */
	public boolean willReadBlock() {
	    return false;
	}
	
	
	/**
	 * This is called from the Processor to read a frame worth
	 * of video data.
	 */
 	public void read(Buffer buffer) throws IOException {
 		
 		synchronized (this) {
 		    Object outdata = buffer.getData();
 		    if (outdata == null || !(outdata.getClass() == Format.intArray) ||
 			((int[])outdata).length < maxDataLength) {
 		    	outdata = new int[maxDataLength];
 			
 		    	buffer.setData(outdata);
 		    }
 		    buffer.setFormat( format );
 		    buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) * 1000000) );
 		    BufferedImage bi = robot.getCapture();
 		    
 		    bi.getRGB(0, 0, width, height,(int[])outdata, 0, width);
 		    
 		    buffer.setSequenceNumber( seqNo );
 		    buffer.setLength(maxDataLength);
 		    buffer.setFlags(Buffer.FLAG_KEY_FRAME);
 		    buffer.setHeader(null);
 		  
 		    seqNo++;
 		    bi.flush();
 		    outdata = null;
 		}
	    
	}
 	
 	 public void setTransferHandler(BufferTransferHandler transferHandler) {
 		synchronized (this) {
 		    this.transferHandler = transferHandler;
 		    notifyAll();
 		}
 	    }

 	    void start(boolean started) {
 		synchronized ( this ) {
 		    this.started = started;
 		    if (started && !thread.isAlive()) {
 			thread = new Thread(this);
 			thread.start();
 		    }
 		    notifyAll();
 		}
 	    }
 	
 	/**
 	 * Running as Thread
 	 */
 	public void run() {
 		while (started) {
 		    synchronized (this) {
 			while (transferHandler == null && started) {
 			    try {
 				wait(1000);
 			    } catch (InterruptedException ie) {
 			    }
 			} // while
 		    }

 		    if (started && transferHandler != null) {
 			transferHandler.transferData(this);
 			try {
 			    Thread.currentThread().sleep( 10 );
 			} catch (InterruptedException ise) {
 			}
 		    }
 		} // while (started)
 	    } // r

	/**
	 * Return the format of each video frame.  That will be JPEG.
	 */
	public Format getFormat() {
	    return format;
	}

	public ContentDescriptor getContentDescriptor() {
	    return new ContentDescriptor(ContentDescriptor.RAW);
	}

	public long getContentLength() {
	    return LENGTH_UNKNOWN;
	}

	public boolean endOfStream() {
	    return false;
	}

	public Object[] getControls() {
	    return new Object[0];
	}

	public Object getControl(String controlType) {
	       try {
	          Class  cls = Class.forName(controlType);
	          Object cs[] = getControls();
	          for (int i = 0; i < cs.length; i++) {
	             if (cls.isInstance(cs[i]))
	                return cs[i];
	          }
	          return null;

	       } catch (Exception e) {   // no such controlType or such control
	         return null;
	       }
	    }

}