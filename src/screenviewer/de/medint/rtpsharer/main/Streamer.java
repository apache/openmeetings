package de.medint.rtpsharer.main;

import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;

import javax.media.Codec;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.NoProcessorException;
import javax.media.Owned;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.rtcp.SourceDescription;

import org.apache.log4j.Logger;

import de.medint.rtpsharer.datasource.*;
import de.medint.rtpsharer.util.ConfigUtil;



/**
 * Class for Streaming ScreenCaptures via RTP
 * @author o.becherer
 *
 */
public class Streamer {
	
	private static Logger log = Logger.getLogger(Streamer.class);
	
	/** RTP Target Address */
	private String ipAddress = "10.136.110.255";
	 
	/** PortBase for RTP*/
	private int portBase = 22224;
	 
	/** SourcePort RTP */
	private int sourcePort = 22300;
	 
	/** Processor*/
	private Processor processor = null;
	    
	/** RTP MANAGER */
	private RTPManager rtpMgrs[];
	 
	/** DataSource from ImageDataSource via Processor*/
	private DataSource dataOutput = null;
	 
	
	/** Error Indicator (waitSync)*/
	private boolean failed = false;
	
	private Integer stateLock = new Integer(0);
	
	
	/**
	 * 
	 * @param address
	 * @param destPort
	 * @param sourcePort
	 */
	public Streamer(){
		
		this.ipAddress = ConfigUtil.destinationAddress;
		this.portBase = ConfigUtil.destinationPort;
		this.sourcePort = 22300; //ConfigUtil.sourcePort; //TODO  :CHANGE TO NOT HARDCODED PORT!!
									//Why do we have to change that? swagner 11.09.2009
		
		
		//Extend and capture different Screen Resolutions
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		ConfigUtil.videoWidth = Long.valueOf(Math.round(screenSize.getWidth())).intValue();
		ConfigUtil.videoHeight = Long.valueOf(Math.round(screenSize.getHeight())).intValue();
		
		ConfigUtil.videoWidth = 640;
		ConfigUtil.videoHeight = 480;
	}
	 
	 /**
	  * Start Streaming Captured
	  */
	 //------------------------------------------------------------------------------------------
	 public synchronized String start(){
		 try {
			log.debug("START IP:Port , Source-Port" + this.ipAddress + ":"
					+ this.portBase + " , " + this.sourcePort);

			log.debug("Screen Width and Height "+ConfigUtil.videoWidth+" "+ConfigUtil.videoHeight);
			 
			String result;

			// Create a processor for the specified media locator
			// and program it to output JPEG/RTP
			result = createProcessor();
			
			//If Result is NULL then everything is ok
			if (result != null) {
				System.out.println(result);
				throw new Exception("ERROR IN CREATING PROCESSOR "+result);
				//return result;
			}

			// Create an RTP session to transmit the output of the
			// processor to the specified IP address and port no.
			result = createTransmitter();
			if (result != null) {
				processor.close();
				processor = null;
				System.out.println(result);
				throw new Exception("ERROR IN CREATING Transmitter "+result);
				//return result;
			}

			// Start the transmission
			processor.start();
			
		 } catch (Exception err) {
			 log.error("[start]",err);
		 }
		
		return null;

	 }
	 //------------------------------------------------------------------------------------------
	
	 
	/**
	 * Use the RTPManager API to create sessions for each media 
	 * track of the processor.
	 */
	 private String createTransmitter() {

		// Cheated.  Should have checked the type.
		PushBufferDataSource pbds = (PushBufferDataSource)dataOutput;
		PushBufferStream pbss[] = pbds.getStreams();

		rtpMgrs = new RTPManager[pbss.length];
		SessionAddress localAddr, destAddr;
		InetAddress ipAddr;
		SendStream sendStream;
		int port;
		SourceDescription srcDesList[];

		for (int i = 0; i < pbss.length; i++) {
		    try {
			rtpMgrs[i] = RTPManager.newInstance();	    
			
			port = portBase + 2*i;
			ipAddr = InetAddress.getByName(ipAddress);
			
			
			System.out.println("createTransmitter  localAddress: " + InetAddress.getLocalHost() + "=" + sourcePort);

			localAddr = new SessionAddress( InetAddress.getLocalHost(),sourcePort);
			
			System.out.println("createTransmitter  destAddress: " + ipAddress + "=" + port);

			destAddr = new SessionAddress( ipAddr, port);

			rtpMgrs[i].initialize( localAddr);
			
			rtpMgrs[i].addTarget( destAddr);
			
			sendStream = rtpMgrs[i].createSendStream(dataOutput, i);
			
			sendStream.start();
		    } catch (Exception  e) {
		    	return e.getMessage();
		    }
		}

		return null;
	    }
	
	 /** Stop Streaming Captures*/
	 //------------------------------------------------------------------------------------------
	 public void stop(){
		 
		 synchronized (this) {
			    if (processor != null) {
			    	processor.stop();
			    	processor.close();
			    	processor = null;
				
			    	for (int i = 0; i < rtpMgrs.length; i++) {
			    		rtpMgrs[i].removeTargets( "Session ended.");
			    		rtpMgrs[i].dispose();
			    	}
			    }
			}
	 }
	 //------------------------------------------------------------------------------------------
	 
	 /**
	  * Creating the Processor
	  */
	 //------------------------------------------------------------------------------------------
	 private String createProcessor() {
		try {	
		 
			// ImageDataSource reading ScreenCaptures
			ImageDataSource ids = new ImageDataSource(ConfigUtil.videoWidth, ConfigUtil.videoHeight, ConfigUtil.frameRate);
			 	
				
			 // Try to create a processor to handle the input media locator
			try {
			    processor = javax.media.Manager.createProcessor(ids);
			} catch (NoProcessorException npe) {
			    return "Couldn't create processor : " + npe.getMessage();
			} catch (IOException ioe) {
			    return "IOException creating processor " + ioe.getMessage();
			} 
	
			// Wait for it to configure
			boolean result = waitForState(processor, Processor.Configured);
			
			if (result == false) {
				return "Couldn't configure processor";
			}

			// Get the tracks from the processor
			TrackControl [] tracks = processor.getTrackControls();

			// Do we have atleast one track?
			if (tracks == null || tracks.length < 1) {
			    return "Couldn't find tracks in processor";
			}
			// Set the output content descriptor to RAW_RTP
			// This will limit the supported formats reported from
			// Track.getSupportedFormats to only valid RTP formats.
			ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
			
			processor.setContentDescriptor(cd);

			Format supported[];
			Format chosen;
			boolean atLeastOneTrack = false;

			// Program the tracks.
			for (int i = 0; i < tracks.length; i++) {
			    Format format = tracks[i].getFormat();
			    if (tracks[i].isEnabled()) {

				supported = tracks[i].getSupportedFormats();

				// We've set the output content to the RAW_RTP.
				// So all the supported formats should work with RTP.
				// We'll just pick the first one.

				if (supported.length > 0) {
				    if (supported[0] instanceof VideoFormat) {
					// For video formats, we should double check the
					// sizes since not all formats work in all sizes.
					chosen = checkForVideoSizes(format, 
									supported[0], ConfigUtil.videoWidth, ConfigUtil.videoHeight);
				    } else
					chosen = supported[0];
				    tracks[i].setFormat(chosen);
				    System.err.println("Track " + i + " is set to transmit as:");
				    System.err.println("  " + chosen);
				    atLeastOneTrack = true;
				} else
				    tracks[i].setEnabled(false);
			    } else
				tracks[i].setEnabled(false);
			}

			if (!atLeastOneTrack)
			    return "Couldn't set any of the tracks to a valid RTP format";

			log.debug("atLeastOneTrack IS Done ");
			
			// Realize the processor. This will internally create a flow
			// graph and attempt to create an output datasource for JPEG/RTP
			// audio frames.
			result = waitForState(processor, Controller.Realized);
			
			if (result == false)
			    return "Couldn't realize processor";

			
			// Setting JPEG Quality
			setJPEGQuality(processor, ConfigUtil.quality);

			// Get the output data source of the processor
			dataOutput = processor.getDataOutput();

		} catch (Exception err) {
			log.error("[createProcessor]",err);
		}
		return null;
	 }
	 //------------------------------------------------------------------------------------------
	 
	 /**
	  * Waiting for ProcessorStates
	  */
	 //------------------------------------------------------------------------------------------
	private synchronized boolean waitForState(Processor p, int state) {
		try {
			
			p.addControllerListener(new StateListener());
			failed = false;
	
			// Call the required method on the processor
			if (state == Processor.Configured) {
				p.configure();
			} else if (state == Processor.Realized) {
				log.debug("DO Realize");
				p.realize();
			}
	
			// Wait until we get an event that confirms the
			// success of the method, or a failure event.
			// See StateListener inner class
			while (p.getState() < state && !failed) {
				synchronized (getStateLock()) {
					try {
						getStateLock().wait();
					} catch (InterruptedException ie) {
						return false;
					}
				}
			}
	
			if (failed)
				return false;
			else
				return true;
		
		} catch (Exception err) {
			log.error("[waitForState]",err);
		}
		return false;
	}
	 //------------------------------------------------------------------------------------------
	 
	 
	 
	 /**
	  * Setting the encoding quality to the specified value on the JPEG encoder.
	  * 0.5 is a good default.
	  */
	 //------------------------------------------------------------------------------------------
	  void setJPEGQuality(Player p, float val) {

		Control cs[] = p.getControls();
		QualityControl qc = null;
		VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);

		// Loop through the controls to find the Quality control for
	 	// the JPEG encoder.
		for (int i = 0; i < cs.length; i++) {

		    if (cs[i] instanceof QualityControl &&
			cs[i] instanceof Owned) {
			Object owner = ((Owned)cs[i]).getOwner();

			// Check to see if the owner is a Codec.
			// Then check for the output format.
			if (owner instanceof Codec) {
			    Format fmts[] = ((Codec)owner).getSupportedOutputFormats(null);
			    for (int j = 0; j < fmts.length; j++) {
				if (fmts[j].matches(jpegFmt)) {
					
				    qc = (QualityControl)cs[i];
		    		    qc.setQuality(val);
		    		  
				    System.err.println("- Setting quality to " + 
						val + " on " + qc);
				    break;
				}
			    }
			}
			if (qc != null)
			    break;
		    }
		}
	    }
	  	//---------------------------------------------------------------------------------------
		 
	 
	 /**
	   * For JPEG and H263, we know that they only work for particular
	   * sizes.  So we'll perform extra checking here to make sure they
	   * are of the right sizes.
	  */
	 //------------------------------------------------------------------------------------------
	  private Format checkForVideoSizes(Format original, Format supported, int videoWidth, int videoHeight) {

			
		Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
		
		//Format h263Fmt = new Format(VideoFormat.H263_RTP);
		
		// Setting our Dimensions within jpegFormat
		Format myFormat =new VideoFormat(null, 
				new Dimension(videoWidth, videoHeight), 
				Format.NOT_SPECIFIED,
				null,
				Format.NOT_SPECIFIED).intersects(jpegFmt);
		
			
		
		return myFormat;
		
	    }

	  //------------------------------------------------------------------------------------------
		
	 /**
	  * InnerClass for StateListening
	  */
	 class StateListener implements ControllerListener {

			public void controllerUpdate(ControllerEvent ce) {

			    // If there was an error during configure or
			    // realize, the processor will be closed
			    if (ce instanceof ControllerClosedEvent)
			    	setFailed();

			    // All controller events, send a notification
			    // to the waiting thread in waitForState method.
			    if (ce instanceof ControllerEvent) {
			    	synchronized (getStateLock()) {
			    		getStateLock().notifyAll();
			    	}
			    }
			}
	 }
	 
	 void setFailed() {
		failed = true;
	 }
	 
	 Integer getStateLock() {
		return stateLock;
	 }
}
