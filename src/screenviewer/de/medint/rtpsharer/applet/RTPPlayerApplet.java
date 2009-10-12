
package de.medint.rtpsharer.applet;

import java.applet.Applet;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.lang.String;
import javax.media.*;
import javax.media.protocol.*;


/**
 * 
 * @author o.becherer
 *	This PlayerApplet is a mixture of SUNs reference RTP Implementations AVReceive2 and RTPPlayerApplet
 */
public class RTPPlayerApplet extends Applet implements ControllerListener, ReceiveStreamListener, SessionListener, ActionListener{
    
	/** Destination Host */
    InetAddress destaddr;
    
    /** Address */
    String address;
    
    /** Connection Port */
    String portstr;
    
    /** Media Type */
    String media;
    
    /** Player Window*/
    Player videoplayer = null;
         
    /** AWT Component */
    Component visualComponent = null;
    
    /** AWT Control component */
    Component controlComponent = null;
    
    /** AWT panel */
    Panel panel = null;
    
    /** Button */
    Button videobutton = null;
    
    /** GridLayout*/
    GridBagLayout gridbag = null;
    
    /** GridContraint */
    GridBagConstraints c = null;
    
    
    int width = 320;
    int height =0;
    
    /** Indicator for received JPEG/RTP data*/
    boolean dataReceived = false;
    
    /** Synchronisation object */
    Object dataSync = new Object();
    
    /** RTPManager*/
    RTPManager rtpManager;

    
    /**
     * Applet initialization
     */
    //-----------------------------------------------------------------------------------------------
    public void init(){
    	
    	System.out.println("init");
    	
    	// Preparing Layout
        setLayout( new BorderLayout() );
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout( new FlowLayout() );
        add("North", buttonPanel);
       
        // Retrieiving params from Enclosing HTMLPage
        media = getParameter("video");
        
        if (media.equals("On")){
            
        	address = getParameter("videosession"); // RTP Sender Hostname (in our case, the Red5 server hosting openMeetings)
            portstr = getParameter("videoport"); // RTP Port for single RTP Connection (every viewer owns his own RTP Port)
            
            System.out.println("Desstination Address Red5 Host : " + address);
            System.out.println("Destination RTP PORT: " + portstr);
           
        }
        
        try {
    	    InetAddress ipAddr;
    	    
    	    // Local Interface
    	    SessionAddress localAddr = new SessionAddress();
    	    
    	    // remote interface
    	    SessionAddress destAddr;

    	    // Preparing RTP manager
    	    rtpManager = RTPManager.newInstance();
    	    rtpManager.addSessionListener(this);
    	    rtpManager.addReceiveStreamListener(this);

    		ipAddr = InetAddress.getByName(address);

    		// Defining local RTP interface - be aware, that InetAddress.getLocalHost() is resolvable  - in some cases within linux environment
    		// entries within /etc/hosts have to be modified
    		localAddr= new SessionAddress( InetAddress.getLocalHost(),Integer.parseInt(portstr));
            
    		// Defining Remote RTP Interface (providing JPEG/RTP Data)
    		destAddr = new SessionAddress( ipAddr,Integer.parseInt(portstr));
    			
    		rtpManager.initialize( localAddr);

    		rtpManager.addTarget(destAddr);
    		
    		

          } catch (Exception e){
              System.err.println("Cannot create the RTP Session: " + e.getMessage());
              return ;
          }

    	// Wait for data to arrive before moving on.

    	long then = System.currentTimeMillis();
    	long waitingPeriod = 30000;  // wait for a maximum of 30 secs.

    	try{
    	    synchronized (dataSync) {
    		while (!dataReceived && 
    			System.currentTimeMillis() - then < waitingPeriod) {
    		    if (!dataReceived)
    			System.err.println("  - Waiting for RTP data to arrive...");
    		    dataSync.wait(1000);
    		}
    	    }
    	} catch (Exception e) {}

    	if (!dataReceived) {
    	    System.err.println("No RTP data was received.");
    	    
    	    return ;
    	}

           
      }
    //-----------------------------------------------------------------------------------------------
    
    /**
     * ControllerListener for the Players.
     */
    //-----------------------------------------------------------------------------------------------
    public synchronized void controllerUpdate(ControllerEvent ce) {

    	System.out.println("ControllerUpdate");
        Player player = null;
        
        Controller controller = (Controller)ce.getSource();
        
        if (controller instanceof Player)
            player  =(Player)ce.getSource();
        
        if (player == null)
            return;
        
        
        if (ce instanceof RealizeCompleteEvent) {
        	System.out.println("RealizeCompleteEvent");
            // add the video player's visual component to the applet
            if (( visualComponent =
                  player.getVisualComponent())!= null){
                width = visualComponent.getPreferredSize().width;
                height += visualComponent.getPreferredSize().height;
                if (panel == null) {
                    panel = new Panel();
                    repositionPanel(width, height);
                    panel.setLayout(new BorderLayout());
                }
                panel.add("Center", visualComponent);
                panel.validate();
            }
            // add the player's control component to the applet
            if (( controlComponent = 
                  player.getControlPanelComponent()) != null){
                height += controlComponent.getPreferredSize().height;
                if (panel == null) {
                    panel = new Panel();
                    panel.setLayout(new BorderLayout());
                }
                repositionPanel(width, height);
                panel.add("South", controlComponent);
                panel.validate();
            }
            
            if (panel != null){
                add("Center", panel);
                invalidate();
            }
        }

        if (ce instanceof SizeChangeEvent) {
        	System.out.println("SizeChangeEvent");
            if (panel != null){
                SizeChangeEvent sce = (SizeChangeEvent) ce;
                int nooWidth = sce.getWidth();
                int nooHeight = sce.getHeight();
                
                // Add the height of the default control component
                if (controlComponent != null)
                    nooHeight += controlComponent.getPreferredSize().height;
                
                // Set the new panel bounds and redraw
                repositionPanel(nooWidth, nooHeight);
            }
        }
        validate();
    }
    //-----------------------------------------------------------------------------------------------

    


    
    // start player on applet start
    //-----------------------------------------------------------------------------------------------
    public void start(){
    	
    	
        if (videoplayer != null){
            videoplayer.start();
        }
        
    }
    //-----------------------------------------------------------------------------------------------
    
    // applet has been stopped, stop and deallocate all the RTP players.

    //-----------------------------------------------------------------------------------------------
    public void stop(){
        if (videoplayer != null){
            videoplayer.close();
        }
       
    }
    //-----------------------------------------------------------------------------------------------

    // applet has been destroyed by the browser. Close the Session
    // Manager. 
    public void destroy(){
        // close the video and audio RTP SessionManagers
         
        if(rtpManager != null){
        
        	rtpManager.dispose();
        }
        super.destroy();
    }
    //-----------------------------------------------------------------------------------------------
            
   
    //-----------------------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent event){
        Button button = (Button)event.getSource();
       
    }
    //-----------------------------------------------------------------------------------------------
    
    
    /**
     * SessionListener.
     */
    //-----------------------------------------------------------------------------------------------
    public synchronized void update(SessionEvent evt) {
    	if (evt instanceof NewParticipantEvent) {
    		Participant p = ((NewParticipantEvent)evt).getParticipant();
    		System.err.println("  - A new participant had just joined: " + p.getCNAME());
    	}
    }
    //-----------------------------------------------------------------------------------------------
    
    

    /**
     * ReceiveStreamListener
     */
    //-----------------------------------------------------------------------------------------------
    public synchronized void update( ReceiveStreamEvent evt) {
    	
    	RTPManager mgr = (RTPManager)evt.getSource();
    	Participant participant = evt.getParticipant();	// could be null.
    	ReceiveStream stream = evt.getReceiveStream();  // could be null.

    	if (evt instanceof RemotePayloadChangeEvent) {
     
    		System.err.println("  - Received an RTP PayloadChangeEvent.");
    		System.err.println("Sorry, cannot handle payload change.");
    		System.exit(0);

    	}
    
    	else if (evt instanceof NewReceiveStreamEvent) {

		    try {
		    	stream = ((NewReceiveStreamEvent)evt).getReceiveStream();
		    	DataSource ds = stream.getDataSource();
			
		    	videoplayer = javax.media.Manager.createPlayer(ds);
		    	if (videoplayer == null)
		    		return;

		    	videoplayer.addControllerListener(this);
		    	videoplayer.realize();

	
		    	// Find out the formats.
		    	RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
		    	if (ctl != null){
		    		System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
		    	} else
		    		System.err.println("  - Recevied new RTP stream");
	
		    	if (participant == null)
		    		System.err.println("      The sender of this stream had yet to be identified.");
		    	
		    	else {
		    		System.err.println("      The stream comes from: " + participant.getCNAME()); 
		    	}

		
		    	// Notify intialize() that a new stream had arrived.
		    	synchronized (dataSync) {
		    		dataReceived = true;
		    		dataSync.notifyAll();
		    	}

		    } catch (Exception e) {
		    	System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
		    	return;
		    }
        
    	}
        

	else if (evt instanceof StreamMappedEvent) {

	    if (stream != null && stream.getDataSource() != null) {
	    	DataSource ds = stream.getDataSource();
	    	// Find out the formats.
	    	RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
	    	System.err.println("  - The previously unidentified stream ");
	    	if (ctl != null)
	    		System.err.println("      " + ctl.getFormat());
	    		System.err.println("      had now been identified as sent by: " + participant.getCNAME());
	     	}
		}

		else if (evt instanceof ByeEvent) {
			System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
	    
	     
		}
    	
    }
    //--------------------------------------------------------------------------------------------------
    
    /**
     * 
     * @param width
     * @param height
     */
    //--------------------------------------------------------------------------------------------------
    void repositionPanel(int width, int height) {
        panel.setBounds(0,
                        0,
                        width,
                        height);
        panel.validate();
    }
    //--------------------------------------------------------------------------------------------------
    
   
}// end of class

