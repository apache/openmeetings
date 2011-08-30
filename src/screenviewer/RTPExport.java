/*
 * @(#)RTPExport.java	1.1 01/04/10
 *
 * Copyright (c) 1996-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.util.Vector;
import java.io.File;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.control.QualityControl;
import javax.media.Format;
import javax.media.format.*;
import javax.media.datasink.*;
import javax.media.protocol.*;
import java.io.IOException;
import com.sun.media.format.WavAudioFormat;


/**
 * A sample program to receive RTP transmission, transcode the stream to an output 
 * location with a default data format.
 */
public class RTPExport implements ControllerListener, DataSinkListener {

    private Processor thisProcessor;
    
    /**
     * Given a source media locator, destination media locator and a duration
     * this method will receive RTP transmission, transcode the source to the 
     * destination into a default format. 
     */
    public boolean doIt(MediaLocator inML, MediaLocator outML, int duration) {

	Processor p;

	try {
	    System.err.println("- create processor for: " + inML);
	    p = Manager.createProcessor(inML);
	    thisProcessor = p;
	} catch (Exception e) {
	    System.err.println("Yikes!  Cannot create a processor from the given url: " + e);
	    return false;
	}

	p.addControllerListener(this);

	// Put the Processor into configured state.
	p.configure();
	if (!waitForState(p, p.Configured)) {
	    System.err.println("Failed to configure the processor.");
	    return false;
	}

	// Set the output content descriptor based on the media locator.
	setContentDescriptor(p, outML);

	// Program the tracks.
	if (!setTrackFormats(p))
	    return false;

	// We are done with programming the processor.  Let's just
	// realize it.
	p.realize();
	if (!waitForState(p, p.Realized)) {
	    System.err.println("Failed to realize the processor.");
	    return false;
	}

	// Set the JPEG quality to .5.
	setJPEGQuality(p, 0.5f);

	// Now, we'll need to create a DataSink.
	DataSink dsink;
	if ((dsink = createDataSink(p, outML)) == null) {
	    System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
	    return false;
	}

	dsink.addDataSinkListener(this);
	fileDone = false;

	// Set the stop time if there's one set.
	if (duration > 0)
	    p.setStopTime(new Time((double)duration));

	System.err.println("start transcoding ...");

	// OK, we can now start the actual transcoding.
	try {
	    p.start();
	    dsink.start();
	} catch (IOException e) {
	    System.err.println("IO error during transcoding");
	    return false;
	}

	// Wait for EndOfStream event.
	waitForFileDone(duration);

	// Cleanup.
	try {
	    dsink.close();
	} catch (Exception e) {}
	p.removeControllerListener(this);

	System.err.println("...done RTPExporting.");

	return true;
    }


    /**
     * Set the content descriptor based on the given output MediaLocator.
     */
    void setContentDescriptor(Processor p, MediaLocator outML) {

 	ContentDescriptor cd;

	// If the output file maps to a content type,
	// we'll try to set it on the processor.

	if ((cd = fileExtToCD(outML.getRemainder())) != null) {

	    System.err.println("- set content descriptor to: " + cd);

	    if ((p.setContentDescriptor(cd)) == null) {

		// The processor does not support the output content
		// type.  But we can set the content type to RAW and 
		// see if any DataSink supports it.

		p.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
	    }
	}
    }


    /**
     * Set the target transcode format on the processor.
     */
    boolean setTrackFormats(Processor p) {

	Format supported[];

	TrackControl [] tracks = p.getTrackControls();

	// Do we have at least one track?
	if (tracks == null || tracks.length < 1) {
	    System.err.println("Couldn't find tracks in processor");
	    return false;
	}

	for (int i = 0; i < tracks.length; i++) {
	    if (tracks[i].isEnabled()) {
		supported = tracks[i].getSupportedFormats();
		if (supported.length > 0) {
		    tracks[i].setFormat(supported[0]);
		} else {
		    System.err.println("Cannot transcode track [" + i + "]");
		    tracks[i].setEnabled(false);
		    return false;
		}
	    } else {
		tracks[i].setEnabled(false);		
		return false;
	    }
	}
	return true;
    }

  
    /**
     * Setting the encoding quality to the specified value on the JPEG encoder.
     * 0.5 is a good default.
     */
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
			    System.err.println("- Set quality to " + 
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


    /**
     * Create the DataSink.
     */
    DataSink createDataSink(Processor p, MediaLocator outML) {

	DataSource ds;

	if ((ds = p.getDataOutput()) == null) {
	    System.err.println("Something is really wrong: the processor does not have an output DataSource");
	    return null;
	}

	DataSink dsink;

	try {
	    System.err.println("- create DataSink for: " + outML);
	    dsink = Manager.createDataSink(ds, outML);
	    dsink.open();
	} catch (Exception e) {
	    System.err.println("Cannot create the DataSink: " + e);
	    return null;
	}

	return dsink;
    }


    Object waitSync = new Object();
    boolean stateTransitionOK = true;

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }


    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {

	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof MediaTimeSetEvent) {
	    System.err.println("- mediaTime set: " + 
		((MediaTimeSetEvent)evt).getMediaTime().getSeconds());
	} else if (evt instanceof StopAtTimeEvent) {
	    System.err.println("- stop at time: " +
		((StopAtTimeEvent)evt).getMediaTime().getSeconds());
	    evt.getSourceController().close();
	}
    }


    Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;

    /**
     * Block until file writing is done. 
     */
    boolean waitForFileDone(int duration) {
	System.err.print("  ");
	synchronized (waitFileSync) {
	    try {
		while (!fileDone) {
		    if(thisProcessor.getMediaTime().getSeconds() > duration)
			thisProcessor.close();
		    waitFileSync.wait(1000);
		    System.err.print(".");
		}
	    } catch (Exception e) {}
	}
	System.err.println("");
	return fileSuccess;
    }


    /**
     * Event handler for the file writer.
     */
    public void dataSinkUpdate(DataSinkEvent evt) {

	if (evt instanceof EndOfStreamEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		waitFileSync.notifyAll();
	    }
	} else if (evt instanceof DataSinkErrorEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		fileSuccess = false;
		waitFileSync.notifyAll();
	    }
	}
    }


    /**
     * Convert a file name to a content type.  The extension is parsed
     * to determine the content type.
     */
    ContentDescriptor fileExtToCD(String name) {

	String ext;
	int p;

	// Extract the file extension.
	if ((p = name.lastIndexOf('.')) < 0)
	    return null;

	ext = (name.substring(p + 1)).toLowerCase();

	String type;

	// Use the MimeManager to get the mime type from the file extension.
	if ( ext.equals("mp3")) {
	    type = FileTypeDescriptor.MPEG_AUDIO;
	} else {
	    if ((type = com.sun.media.MimeManager.getMimeType(ext)) == null)
		return null;
	    type = ContentDescriptor.mimeTypeToPackageName(type);
	}

	return new FileTypeDescriptor(type);
    }


    /**
     * Main program
     */
    public static void main(String [] args) {

	String outputURL = null, inputURL = null;
	int duration = -1;

	if (args.length == 0)
	    prUsage();

	// Parse the arguments.
	int i = 0;
	while (i < args.length) {
	    if (args[i].equals("-o")) {
		i++;
		if (i >= args.length)
		    prUsage();
		outputURL = args[i];
	    } else if (args[i].equals("-d")) {
		i++;
		if (i >= args.length)
		    prUsage();
		Integer integer = Integer.valueOf(args[i]);
		if (integer != null)
		    duration = integer.intValue();
	    } else if (inputURL == null){
		inputURL = "rtp://" + args[i];
	    } else {
		inputURL = inputURL  + "&" + args[i];		
	    }
	    i++;
	}

	if (inputURL == null) {
	    System.err.println("No reception session is specified");
	    prUsage();
	}

	if (outputURL == null) {
	    System.err.println("No output url is specified");
	    prUsage();
	}

	// Generate the input and output media locators.
	MediaLocator iml, oml;

	if ((iml = createMediaLocator(inputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + inputURL);
	    System.exit(0);
	}

	if ((oml = createMediaLocator(outputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + outputURL);
	    System.exit(0);
	}

	// RTPExport with the specified parameters.
	RTPExport RTPExport  = new RTPExport();

	if (!RTPExport.doIt(iml, oml, duration)) {
	    System.err.println("RTPExporting failed");
	}

	System.exit(0);
    }


    /**
     * Create a media locator from the given string.
     */
    @SuppressWarnings("unused")
	static MediaLocator createMediaLocator(String url) {

	MediaLocator ml;

	if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
	    return ml;

	if (url.startsWith(File.separator)) {
	    if ((ml = new MediaLocator("file:" + url)) != null)
		return ml;
	} else {
	    String file = "file:" + System.getProperty("user.dir") + File.separator + url;
	    if ((ml = new MediaLocator(file)) != null)
		return ml;
	}

	return null;
    }

    static void prUsage() {
	System.err.println("Usage: java RTPExport -o <output> -d <duration> <session> <session> ...");
	System.err.println("     <output>: output URL or file name");
	System.err.println("     <duration>: duration in seconds");
	System.err.println("     <session>: [SourceIP]:[SourcePort]/[ContentType]");
	System.exit(0);
    }
}
