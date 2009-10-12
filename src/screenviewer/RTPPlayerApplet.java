/*
 * @(#)RTPPlayerApplet.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
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


import java.applet.Applet;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;
import javax.media.rtp.event.*;
import com.sun.media.rtp.RTPSessionMgr;
import java.io.*;
import java.awt.*;
import java.util.Vector;
import java.net.*;
import java.awt.event.*;
import java.lang.String;
import javax.media.*;
import javax.media.protocol.*;
import com.sun.media.*;
import com.sun.media.ui.*;
import java.io.IOException;
import java.lang.SecurityException;


// This RTP applet will allow a user to playback streams for one audio
// session and one  video session. Video and Audio RTP monitors are
// also available for displaying RTCP statistics of this
// session.Methods
// StartSessionManager() will take care of starting the session and
// registering this applet as an RTP Session Listener.
// Method RTPSessionUpdate() will process all the RTPEvents sent by
// the SessionManager.
public class RTPPlayerApplet  extends Applet implements
			ControllerListener, ReceiveStreamListener, ActionListener{
  
    
    InetAddress destaddr;
    String address;
    String portstr;
    String media;
    Player videoplayer = null;
    SessionManager videomgr = null;
    SessionManager audiomgr = null;
    Component visualComponent = null;
    Component controlComponent = null;
    Panel panel = null;
    Button audiobutton = null;
    Button videobutton = null;
    GridBagLayout gridbag = null;
    GridBagConstraints c = null;
        int width = 320;
    int height =0;
    Vector playerlist = new Vector();
   
    
    public void init(){
        setLayout( new BorderLayout() );
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout( new FlowLayout() );
        add("North", buttonPanel);
        media = getParameter("video");
        if (media.equals("On")){
            address = getParameter("videosession");
            portstr = getParameter("videoport");
            StartSessionManager(address,
                                StrToInt(portstr),
                                "video");
            if (videomgr == null){
                System.err.println("null video manager ");
                return;
            }
            // this is the GUI for displaying the RTCP
            // statistics. This will not be displayed until the user
            // clicks on the RTP Monitor window
            //videogui = new ParticipantListWindow(videomgr);
            // add a button for the video RTP monitor
            videobutton = new Button("Video RTP Monitor");
            videobutton.addActionListener(this);
            buttonPanel.add(videobutton);
        }
        media = getParameter("audio");
        if (media.equals("On")){
            address = getParameter("audiosession");
            portstr = getParameter("audioport");
            StartSessionManager(address,
                                StrToInt(portstr),
                                "audio");
            if (audiomgr == null){
                System.err.println("null audio manager");
                return;
            }
            //audiogui = new ParticipantListWindow(audiomgr);
            // add a button for the audio RTP monitor
            audiobutton = new Button("Audio RTP Monitor");
            audiobutton.addActionListener(this);
            buttonPanel.add(audiobutton);
        }
    }// end of constructor

    public void start(){
        // The applet only controls the first video player by adding
        // its visual and control component to the applet canvas. Thus
        // only this player needs to be controlled when this applet is
        // swiched in browser pages etc.
        if (videoplayer != null){
            videoplayer.start();
        }
        if (playerlist == null)
            return;
        for (int i =0; i < playerlist.size(); i++){
            Player player = (Player)playerlist.elementAt(i);
            if (player != null)
                new PlayerWindow(player);
        }
    }
    // applet has been stopped, stop and deallocate all the RTP players.
    public void stop(){
        if (videoplayer != null){
            videoplayer.close();
        }
        if (playerlist == null)
            return;
        for (int i =0; i < playerlist.size(); i++){
            Player player = (Player)playerlist.elementAt(i);
            if (player != null){
                player.close();
            }
        }
    }

    // applet has been destroyed by the browser. Close the Session
    // Manager. 
    public void destroy(){
        // close the video and audio RTP SessionManagers
        String reason = "Shutdown RTP Player";
        
        if (videomgr != null){
            videomgr.closeSession(reason);
            videoplayer = null;
            videomgr = null;
        }
        
        if (audiomgr != null){
            audiomgr.closeSession(reason);
            audiomgr = null;
        }
        super.destroy();
    }
            
   
    public void actionPerformed(ActionEvent event){
        Button button = (Button)event.getSource();
       
            
    }
    
    public String getAddress(){
        return  address;
    }
    
    public int getPort(){
        // the port has to be returned as an integer
        return StrToInt(portstr);
    }
    
    public String getMedia(){
        return media;
    }
    
    private int StrToInt(String str){
        if (str == null)
            return -1;
        Integer retint = new Integer(str);
        return  retint.intValue();
    }

    public synchronized void controllerUpdate(ControllerEvent event) {
        Player player = null;
        Controller controller = (Controller)event.getSource();
        if (controller instanceof Player)
            player  =(Player)event.getSource();
        
        if (player == null)
            return;
        
        
        if (event instanceof RealizeCompleteEvent) {
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

        if (event instanceof SizeChangeEvent) {
            if (panel != null){
                SizeChangeEvent sce = (SizeChangeEvent) event;
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

    /**
     * The video/control component panel needs to be repositioned to sit
     * in the middle of the applet window.
     */
    void repositionPanel(int width, int height) {
        panel.setBounds(0,
                        0,
                        width,
                        height);
        panel.validate();
    }

    public void update( ReceiveStreamEvent event){
        SessionManager source =(SessionManager)event.getSource();
        Player newplayer = null;
        // create a new player if a new recvstream is detected
        if (event instanceof NewReceiveStreamEvent){
            try{
                ReceiveStream stream = ((NewReceiveStreamEvent)event).getReceiveStream();
                DataSource dsource = stream.getDataSource();
                newplayer = Manager.createPlayer(dsource);
            }catch (Exception e){
          System.err.println("RTPPlayerApplet Exception " + e.getMessage());
          e.printStackTrace();
            }
            if (newplayer == null){
                return;
            }
            // if this is the first video player, we need to listen to
            // its events. Add me as a ControllerListener before
            // starting the player
            if (source == videomgr){
                if (videoplayer == null){
                    videoplayer = newplayer;
                    newplayer.addControllerListener(this);
                    newplayer.start();
                }               
                else{// controller listener and start is taken care of
                    // in playerWindiow 
                    if (playerlist != null)
                        playerlist.addElement((Object)newplayer);
                    new PlayerWindow(newplayer);
                }
            }// if (source == videomgr)
            if (source == audiomgr){
                if (playerlist != null)
                        playerlist.addElement((Object)newplayer);
                new PlayerWindow(newplayer);
            }
        }// if (event instanceof NewReceiveStreamEvent)

        
        if (event instanceof RemotePayloadChangeEvent){
            // we received a payload change event. If a player was not
            // created for this ReceiveStream, create a player. If the
            // player already exists, RTPSM and JMF have taken care of
            // switching the payloads and we dont do anything.
            // If this is the first video player add me as the
            // controllerlistener before starting the player, else
            // just create a new player window.
        }
        
    }// end of RTPSessionUpdate
        
    private SessionManager StartSessionManager(String destaddrstr,
                                                  int port,
                                                  String media){
        // this method create a new RTPSessionMgr and adds this applet
        // as a SessionListener, before calling initSession() and startSession()
        SessionManager mymgr = new RTPSessionMgr();
        if (media.equals("video"))
            videomgr = mymgr;
        if (media.equals("audio"))
            audiomgr = mymgr;
        if (mymgr == null)
            return null;
        mymgr.addReceiveStreamListener(this);
        //if (media.equals("audio"))
        //  EncodingUtil.Init((SessionManager)mymgr);
        
        // for initSession() we must generate a CNAME and fill in the
        // RTP Session address and port
        String cname = mymgr.generateCNAME();
        String username = "jmf-user";

        SessionAddress localaddr = new SessionAddress();
        
        try{
            destaddr = InetAddress.getByName(destaddrstr);
        }catch (UnknownHostException e){
            System.err.println("inetaddress " + e.getMessage());
            e.printStackTrace();
        }    
        SessionAddress sessaddr = new SessionAddress(destaddr,
                                                           port,
                                                           destaddr,
                                                           port+1);
        
        SourceDescription[] userdesclist = new SourceDescription[4];
        int i;
        for(i=0; i< userdesclist.length;i++){
            if (i == 0){
                userdesclist[i] = new
                    SourceDescription(SourceDescription.SOURCE_DESC_EMAIL,
                                    "jmf-user@sun.com",
                                    1,
                                    false);
                continue;
            }

            if (i == 1){
                userdesclist[i] = new
              SourceDescription(SourceDescription.SOURCE_DESC_NAME,
                                    username,
                                    1,
                                    false);
                continue;
            }
            if ( i == 2){
                userdesclist[i] = new 
                    SourceDescription(SourceDescription.SOURCE_DESC_CNAME,
                                          cname,
                                      1,
                                      false);
                continue;
            }
            if (i == 3){ 
                userdesclist[i] = new
            SourceDescription(SourceDescription.SOURCE_DESC_TOOL,
                                  "JMF RTP Player v2.0",
                                  1,
                                  false);
                continue;
            }
        }// end of for
        
        // call initSession() and startSession() of the RTPsessionManager
        try{
            mymgr.initSession(localaddr,
                              mymgr.generateSSRC(),
                              userdesclist,
                              0.05,
                              0.25);
            mymgr.startSession(sessaddr,1,null);
        }catch (SessionManagerException e){
          System.err.println("RTPPlayerApplet: RTPSM Exception " + e.getMessage());
          e.printStackTrace();
          return null;
        }catch (IOException e){
           System.err.println("RTPPlayerApplet: IO Exception " + e.getMessage());
           e.printStackTrace();
           return null;
        }
        
        return mymgr;
    }       

}// end of class

