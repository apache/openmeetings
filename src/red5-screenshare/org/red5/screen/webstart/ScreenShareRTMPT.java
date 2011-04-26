package org.red5.screen.webstart;

import java.io.*;
import java.util.*;

import org.apache.mina.common.ByteBuffer;
import org.red5.io.IStreamableFile;
import org.red5.io.ITag;
import org.red5.io.ITagWriter;
import org.red5.io.ITagReader;
import org.red5.io.flv.impl.FLVService;
import org.red5.io.flv.impl.FLV;
import org.red5.io.flv.impl.FLVReader;
import org.red5.io.flv.impl.Tag;
import org.red5.io.IoConstants;
import org.red5.io.utils.ObjectMap;
import org.red5.screen.webstart.tgui.VirtualScreen;
import org.red5.screen.webstart.tgui.VirtualScreenBean;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.event.IEventDispatcher;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.net.rtmp.Channel;
import org.red5.server.net.rtmpt.RTMPTClient;
import org.red5.server.net.rtmp.INetStreamEventHandler;
import org.red5.server.net.rtmp.RTMPConnection;
import org.red5.server.net.rtmp.ClientExceptionHandler;
import org.red5.server.net.rtmp.codec.RTMP;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.message.Header;
import org.red5.server.net.rtmp.status.StatusCodes;
import org.red5.server.net.rtmp.event.SerializeUtils;
import org.red5.server.stream.AbstractClientStream;
import org.red5.server.stream.IStreamData;
import org.red5.server.stream.message.RTMPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.Date;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.JComboBox;

public class ScreenShareRTMPT extends RTMPTClient implements INetStreamEventHandler, ClientExceptionHandler, IPendingServiceCallback {

    private static final Logger logger = LoggerFactory.getLogger( ScreenShareRTMPT.class );
    public static ScreenShareRTMPT instance = null;

    public boolean startPublish = false;
    public Integer playStreamId;
    public Integer publishStreamId;
    public String publishName;
    public RTMPConnection conn;
    public ITagWriter writer;
    public ITagReader reader;
    public int videoTs = 0;
    public int audioTs = 0;
    public int kt = 0;
    public int kt2 = 0;
    public ByteBuffer buffer;
	public CaptureScreen capture = null;
	public Thread thread = null;

	public java.awt.Container contentPane;
	public JFrame t;
	public JLabel textArea;
	public JLabel textWarningArea;
	public JLabel textAreaQualy;
	public JButton startButton;
	public JButton stopButton;
	public JButton exitButton;
	public JSpinner jSpin;
	public JLabel tFieldScreenZoom;
	public JLabel blankArea;
	public BlankArea virtualScreen;
	public JLabel vscreenXLabel;
	public JLabel vscreenYLabel;
	public JSpinner jVScreenXSpin;
	public JSpinner jVScreenYSpin;
	public JLabel vscreenWidthLabel;
	public JLabel vscreenHeightLabel;
	public JSpinner jVScreenWidthSpin;
	public JSpinner jVScreenHeightSpin;
	
	 //---
    
    public JComboBox jVScreenResizeMode;
    public JLabel vscreenResizeLabel;
    //public JLabel vscreenResizeHeightLabel;
    //public JSpinner jVScreenResizeWidthSpin;
    //public JSpinner jVScreenResizeHeightSpin;
    //---
    
	public JLabel textAreaHeaderRecording;
	public JLabel textAreaHeaderRecordingDescr;
	public JButton startButtonRecording;
	public JButton stopButtonRecording;

	public JLabel vScreenIconLeft;
	public JLabel vScreenIconRight;
	public JLabel vScreenIconUp;
	public JLabel vScreenIconDown;
	public JLabel myBandWidhtTestLabel;

	public String host = "btg199251";
	public String app = "oflaDemo";
	public int port = 1935;
	public int defaultQualityScreensharing = 0;
	
	public Long organization_id = 0L;
	public Long user_id = null;
	public Boolean allowRecording = true;
	
	public boolean startRecording = false;
	public boolean stopRecording = false;
	
	public boolean startStreaming = false;
	public boolean stopStreaming = false;
	
	public String label730 = "Desktop Publisher";
	public String label731 = "This application will publish your screen";
	public String label732 = "Start Sharing";
	public String label733 = "Stop Sharing";
	public String label734 = "Select your screen Area:";
	public String label735 = "Change width";
	public String label737 = "Change height";
	public String label738 = "SharingScreen X:";
	public String label739 = "SharingScreen Y:";
	public String label740 = "SharingScreen Width:";
	public String label741 = "SharingScreen Height:";
	public String label742 = "Connection was closed by Server";
	public String label844 = "Show Mouse Position at viewers";
	
	public String label869 = "Recording";
	public String label870 = "<HTML>You may record and share your screen at the same time." +
			"To enable others to see your screen just hit the start button on the top." +
			"To only record the Session it is sufficient to click start recording.</HTML>";
	public String label871 = "Start Recording";
	public String label872 = "Stop Recording";
	public String label878 = "Stop Sharing";
    
    public String label1089 = "Quality of the ScreenShare: -";
    public String label1090 = "Very high Quality -";
    public String label1091 = "High Quality -";
    public String label1092 = "Medium Quality -";
    public String label1093 = "Low Quality -";	

	public Float imgQuality = new Float(0.40);
	
	//public Float scaleFactor = 1F;
	public float Ampl_factor = 1.3f;
	
	public boolean isConnected = false;
	
	public Map<Integer,Boolean> currentPressedKeys = new HashMap<Integer,Boolean>();

    // ------------------------------------------------------------------------
    //
    // Main
    //
    // ------------------------------------------------------------------------

	private ScreenShareRTMPT() {};
	
	public static void main(String[] args)
	{
		try {
			instance = new ScreenShareRTMPT();
	
			if (args.length == 9) {
				
				
				instance.host = args[0];
				instance.app = args[1];
				instance.port = Integer.parseInt(args[2]);
				instance.publishName = args[3];
				
				String labelTexts = args[4];
				
				instance.organization_id = Long.parseLong(args[5]);
				 

                instance.defaultQualityScreensharing = Integer.parseInt(args[6]);
                instance.user_id =  Long.parseLong(args[7]);
                instance.allowRecording =  Boolean.parseBoolean(args[8]);
				
				if (labelTexts.length() > 0) {
					String[] textArray = labelTexts.split(";");
					
					logger.debug("labelTexts :: "+labelTexts);
					
					logger.debug("textArray Length "+textArray.length);
					
					for (int i=0;i<textArray.length;i++) {
						logger.debug(i + " :: " + textArray[i]);
					}
					
					instance.label730 = textArray[0];
					instance.label731 = textArray[1];
					instance.label732 = textArray[2];
					instance.label733 = textArray[3];
					instance.label734 = textArray[4];
					instance.label735 = textArray[5];
					instance.label737 = textArray[6];
					instance.label738 = textArray[7];
					instance.label739 = textArray[8];
					instance.label740 = textArray[9];
					instance.label741 = textArray[10];
					instance.label742 = textArray[11];
					instance.label844 = textArray[12];
					
					instance.label869 = textArray[13];
					instance.label870 = "<html>"+textArray[14]+"</html>";
					instance.label871 = textArray[15];
					instance.label872 = textArray[16];
					instance.label878 = textArray[17];
					
					instance.label1089 = textArray[18];
                    instance.label1090 = textArray[19];
                    instance.label1091 = textArray[20];
                    instance.label1092 = textArray[21];
                    instance.label1093 = textArray[22];
					
				}
	
			} else {
				instance = null;
				System.out.println("\nRed5 SceenShare: use as java ScreenShareRTMPT <host> <app name> <port> <stream name>\n Example: SceenShare localhost oflaDemo 1935 screen_stream");
				System.exit(0);
			}
	
			logger.debug("RTMPT host: " + instance.host + ", app: " + instance.app + ", port: " + instance.port + ", publish: " + instance.publishName);
	
			instance.createWindow();
			
		} catch (Exception err) {
			logger.error("",err);
		}
	}
	
    // ------------------------------------------------------------------------
    //
    // Wrapper - Constructor for testing
    //
    // ------------------------------------------------------------------------
	public ScreenShareRTMPT(String host, String app, Integer port, String publishName, Long organization_id)
	{
		instance = new ScreenShareRTMPT();

		instance.host = host;
		instance.app = app;
		instance.port = port;
		instance.publishName = publishName;
		instance.organization_id = organization_id;

		logger.debug("host: " + instance.host + ", app: " + instance.app + ", port: " + instance.port + ", publish: " + instance.publishName);

		instance.createWindow();
	}
	

    // ------------------------------------------------------------------------
    //
    // GUI
    //
    // ------------------------------------------------------------------------

	public void createWindow()
	{
		try {
			
			ImageIcon start_btn = createImageIcon("/webstart_play.png");
			ImageIcon stop_btn = createImageIcon("/webstart_stop.png");

			UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
			UIManager.getLookAndFeelDefaults().put( "ClassLoader", getClass().getClassLoader()  );

			t = new JFrame(this.label730);
			contentPane = t.getContentPane();
			contentPane.setBackground(Color.WHITE);
			textArea = new JLabel();
			textArea.setBackground(Color.WHITE);
			contentPane.setLayout(null);
			contentPane.add(textArea);
			
			//*****
			//Header Overall
			textArea.setText(this.label731);
			textArea.setBounds(10, 0, 400,24);

			//*****
			//Start Button Screen Sharing
			startButton = new JButton( this.label732, start_btn );
			startButton.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					startRecording = false;
					startStreaming = true;
					captureScreenStart();
				}
			});
			startButton.setBounds(30, 34, 200, 32);
			t.add(startButton);

			//*****
			//Stop Button Screen Sharing
			stopButton = new JButton( this.label733, stop_btn );
			stopButton.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					stopRecording = false;
					stopStreaming = true;
					captureScreenStop();
				}
			});
			stopButton.setBounds(290, 34, 200, 32);
			stopButton.setEnabled(false);
			t.add(stopButton);

			//add the small screen thumb to the JFrame
			new VirtualScreen();
			
			//*****
			//Text Recording
			textAreaHeaderRecording = new JLabel(); 
			
			//FIXME: Set Font to bold
			//textAreaHeaderRecording.setB
			//Font f = textAreaHeaderRecording.getFont();
			//textAreaHeaderRecording.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

			textAreaHeaderRecording.setText(this.label869);
			contentPane.add(textAreaHeaderRecording);
			textAreaHeaderRecording.setBounds(10, 340, 480, 24);
			
			textAreaHeaderRecordingDescr = new JLabel(); 
			textAreaHeaderRecordingDescr.setText(this.label870);
			contentPane.add(textAreaHeaderRecordingDescr);
			textAreaHeaderRecordingDescr.setBounds(10, 360, 480, 54);
			
			if (instance.allowRecording) {
			
				//*****
				//Start Button Recording
				startButtonRecording = new JButton( this.label871, start_btn );
				startButtonRecording.addActionListener( new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						startRecording = true;
						startStreaming = false;
						captureScreenStart();
					}
				});
				startButtonRecording.setBounds(30, 420, 200, 32);
				t.add(startButtonRecording);
				
				//*****
				//Stop Button Recording
				stopButtonRecording = new JButton( this.label872, stop_btn );
				stopButtonRecording.addActionListener( new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						stopRecording = true;
						stopStreaming = false;
						captureScreenStop();
					}
				});
				stopButtonRecording.setBounds(290, 420, 200, 32);
				stopButtonRecording.setEnabled(false);
				t.add(stopButtonRecording);
				
			}
				
			//*****
			//Text Warning
			textWarningArea = new JLabel();
			contentPane.add(textWarningArea);
			textWarningArea.setBounds(10, 450, 420,54);
			//textWarningArea.setBackground(Color.WHITE);
			
			
			//*****
			//Exit Button
			exitButton = new JButton( this.label878 );
			exitButton.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					t.setVisible(false);
					System.exit(0);
				}
			});
			exitButton.setBounds(290, 460, 200, 32);
			t.add(exitButton);

			//*****
			//Background Image
			Image im_left = ImageIO.read(ScreenShareRTMPT.class.getResource("/background.png"));
			ImageIcon iIconBack = new ImageIcon(im_left);

			JLabel jLab = new JLabel(iIconBack);
			jLab.setBounds(0, 0, 500, 440);
			t.add(jLab);

			t.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					t.setVisible(false);
					System.exit(0);
				}

			});
			t.pack();
			t.setLocation(30, 30);
			t.setSize(500, 530);
			t.setVisible(true);
			t.setResizable(false);


			logger.debug("initialized");

		} catch (Exception err)
		{
			logger.error("createWindow Exception: ",err);
			err.printStackTrace();
		}
	}
	
	protected static ImageIcon createImageIcon(String path) throws Exception {
	    java.net.URL imgURL = ScreenShareRTMPT.class.getResource(path);
	    return new ImageIcon(imgURL);
	}

	public void showBandwidthWarning(String warning)
	{
		textWarningArea.setText(warning);
	}
	
	synchronized public void sendCursorStatus() {
		try {
			
			PointerInfo a = MouseInfo.getPointerInfo();
			Point mouseP = a.getLocation();
			
			//Integer x = Long.valueOf(Math.round(mouseP.getX())).intValue();
			//Integer y = Long.valueOf(Math.round(mouseP.getY())).intValue();
			
			//Real size: Real mouse position = Resize : X
            Integer x = Long.valueOf (Math.round( ( ((mouseP.getX()*VirtualScreenBean.vScreenResizeX )/VirtualScreenBean.vScreenSpinnerWidth)-VirtualScreenBean.vScreenSpinnerX) *Ampl_factor)).intValue();
            Integer y = Long.valueOf (Math.round( ( ((mouseP.getY()*VirtualScreenBean.vScreenResizeY )/VirtualScreenBean.vScreenSpinnerHeight)-VirtualScreenBean.vScreenSpinnerY) *Ampl_factor)).intValue();
            
	
			HashMap cursorPosition = new HashMap();
			cursorPosition.put("publicSID",this.publishName);
			cursorPosition.put("cursor_x",x);
			cursorPosition.put("cursor_y",y);
						
			invoke("setNewCursorPosition",new Object[] { cursorPosition }, this);
			
		} catch (Exception err) {
			System.out.println("captureScreenStart Exception: ");
			System.err.println(err);
			textArea.setText("Exception: "+err);
			logger.error("[sendCursorStatus]",err);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	synchronized public void setConnectionAsSharingClient() {
		try {
			
			//logger.debug("setConnectionAsSharingClient" );
			
			HashMap map = new HashMap();
			map.put("screenX",VirtualScreenBean.vScreenSpinnerX);
			map.put("screenY",VirtualScreenBean.vScreenSpinnerY);
			
//			int scaledWidth = Float.valueOf(Math.round(VirtualScreenBean.vScreenSpinnerWidth*scaleFactor)).intValue();
//			int scaledHeight = Float.valueOf(Math.round(VirtualScreenBean.vScreenSpinnerHeight*scaleFactor)).intValue();
			
			int scaledWidth = Float.valueOf(Math.round(VirtualScreenBean.vScreenResizeX*Ampl_factor)).intValue();
            int scaledHeight = Float.valueOf(Math.round(VirtualScreenBean.vScreenResizeY*Ampl_factor)).intValue();
            
            map.put("screenWidth",scaledWidth);
            map.put("screenHeight",scaledHeight);
			
			map.put("publishName", this.publishName);
			map.put("startRecording", this.startRecording);
			map.put("startStreaming", this.startStreaming);
			
			map.put("organization_id", this.organization_id);
			map.put("user_id", this.user_id);
			
			invoke("setConnectionAsSharingClient",new Object[] { map }, this);
			
		} catch (Exception err) {
			logger.error("setConnectionAsSharingClient Exception: ",err);
			textArea.setText("Error: "+err.getLocalizedMessage());
			logger.error("[setConnectionAsSharingClient]",err);
		}
	}

	private void captureScreenStart()
	{
		try {

			logger.debug("captureScreenStart");

			startStream(host, app, port, publishName);

		} catch (Exception err) {
			logger.error("captureScreenStart Exception: ",err);
			textArea.setText("Exception: "+err);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void captureScreenStop()
	{
		try {
			
			//logger.debug("INVOKE ScreenShareRTMPTrAction" );
			
			HashMap map = new HashMap();
			map.put("stopStreaming", this.stopStreaming);
			map.put("stopRecording", this.stopRecording);
			
			invoke("screenSharerAction",new Object[] { map }, this);
			
			if (this.stopStreaming) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			} else {
				startButtonRecording.setEnabled(true);
				stopButtonRecording.setEnabled(false);
			}
			
		} catch (Exception err) {
			logger.error("captureScreenStop Exception: ",err);
			textArea.setText("Exception: "+err);
		}
	}

    // ------------------------------------------------------------------------
    //
    // Public
    //
    // ------------------------------------------------------------------------


    public void startStream( String host, String app, int port, String publishName) {
    	
    	logger.debug( "ScreenShare startStream" );
        this.publishName = publishName;

        videoTs = 0;
        audioTs = 0;
        kt = 0;
        kt2 = 0;

        try {
        	
        	if (!isConnected) {
        		connect( host, port, app, this );
        	} else {
        		setConnectionAsSharingClient();
        	}
        	
        } catch ( Exception e ) {
            logger.error( "ScreenShare startStream exception " + e );
        }

    }
    
	@Override
	protected void onInvoke(RTMPConnection conn, Channel channel, Header source,
			Notify invoke, RTMP rtmp) {
		// TODO Auto-generated method stub
		super.onInvoke(conn, channel, source, invoke, rtmp);

		if (invoke.getType() == IEvent.Type.STREAM_DATA) {
			//logger.debug("Ignoring stream data notify with header: {}", source);
			return;
		}
		//logger.debug("onInvoke: {}, invokeId: {}", invoke, invoke
		//				.getInvokeId());

		//logger.debug("ServiceMethodName :: "+ invoke.getCall().getServiceMethodName());
		//logger.debug("Arguments :: "+ invoke.getCall().getArguments());
		
		if (invoke.getCall().getServiceMethodName().equals("sendRemoteCursorEvent")) {
			
			sendRemoteCursorEvent(invoke.getCall().getArguments()[0]);
			
		}
		
	}    


    public void stopStream() {
    	try {
    		
    		logger.debug( "ScreenShare stopStream" );
        	
        	isConnected = false;
        	
            disconnect();
            capture.stop();
            capture.release();
            thread = null;
            
        }
        catch ( Exception e ) {
            logger.error( "ScreenShare stopStream exception " + e );
        }

    }


    // ------------------------------------------------------------------------
    //
    // Implementations
    //
    // ------------------------------------------------------------------------

	public void handleException(Throwable throwable)
	{
			logger.error("{}",new Object[]{throwable.getCause()});
			System.out.println( throwable.getCause() );
	}


    public void onStreamEvent( Notify notify ) {

        //logger.debug( "onStreamEvent " + notify );

        ObjectMap map = (ObjectMap) notify.getCall().getArguments()[ 0 ];
        String code = (String) map.get( "code" );

        if ( StatusCodes.NS_PUBLISH_START.equals( code ) ) {
            //logger.debug( "onStreamEvent Publish start" );
            startPublish = true;
        }
    }


    public void sendRemoteCursorEvent(Object obj) {
    	try {
    		
    		//logger.debug("#### sendRemoteCursorEvent ");

            //logger.debug("Result Map Type "+obj.getClass().getName());
            
            Map returnMap = (Map) obj;
            
            //logger.debug("result "+returnMap.get("result"));
            
            String action = returnMap.get("action").toString();
            
            if (action.equals("onmouseup")) {
           	 
	           	Robot robot = new Robot();
	           	 
	           	//VirtualScreenBean
	           	 
	//          Integer x = Math.round ( ( ( Float.valueOf(returnMap.get("x").toString()).floatValue() *VirtualScreenBean.vScreenResizeX )/VirtualScreenBean.vScreenSpinnerWidth) / Ampl_factor) ;
	//          Integer y = Math.round ( ( ( Float.valueOf(returnMap.get("y").toString()).floatValue() *VirtualScreenBean.vScreenResizeY )/VirtualScreenBean.vScreenSpinnerHeight)/ Ampl_factor) ;
	//            
	           	
	           	//logger.debug("x 1 "+returnMap.get("x"));
	           	
	           	Float scaleFactor = Float.valueOf(VirtualScreenBean.vScreenSpinnerWidth) / Float.valueOf(VirtualScreenBean.vScreenResizeX);
	           	
	           	//logger.debug("x 1 scaleFactor "+scaleFactor);
	           	
	           	Float part_x1 = ( ( Float.valueOf(returnMap.get("x").toString()).floatValue() * scaleFactor  ) / Float.valueOf(Ampl_factor) );
	           	
	           	//logger.debug("x 1 part_x1 "+part_x1);
	           	
	           	Integer x = Math.round ( part_x1 + VirtualScreenBean.vScreenSpinnerX  );

	           	
	           	Integer y = Math.round ( ( ( Float.valueOf(returnMap.get("y").toString()).floatValue() * VirtualScreenBean.vScreenSpinnerHeight / VirtualScreenBean.vScreenResizeY) / Ampl_factor) + VirtualScreenBean.vScreenSpinnerY)  ;
            
           	
	           	//logger.debug("x|y "+x+" || "+y);
           	 
                robot.mouseMove(x, y);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
           	 
            } else if (action.equals("onmousedown")) {
           	 
	           	Robot robot = new Robot();
	           	
	           	Float scaleFactor = Float.valueOf(VirtualScreenBean.vScreenSpinnerWidth) / Float.valueOf(VirtualScreenBean.vScreenResizeX);
	           	Float part_x1 = ( ( Float.valueOf(returnMap.get("x").toString()).floatValue() * scaleFactor  ) / Float.valueOf(Ampl_factor) );
	           	Integer x = Math.round ( part_x1 + VirtualScreenBean.vScreenSpinnerX  );
	           	Integer y = Math.round ( ( ( Float.valueOf(returnMap.get("y").toString()).floatValue() * VirtualScreenBean.vScreenSpinnerHeight / VirtualScreenBean.vScreenResizeY) / Ampl_factor) + VirtualScreenBean.vScreenSpinnerY)  ;
           	 
                robot.mouseMove(x, y);
                robot.mousePress(InputEvent.BUTTON1_MASK);
           	 
            } else if (action.equals("mousePos")) {
            	
            	Robot robot = new Robot();
	           	 
	           	Float scaleFactor = Float.valueOf(VirtualScreenBean.vScreenSpinnerWidth) / Float.valueOf(VirtualScreenBean.vScreenResizeX);
	           	
	           	Float part_x1 = ( ( Float.valueOf(returnMap.get("x").toString()).floatValue() * scaleFactor  ) / Float.valueOf(Ampl_factor) );
	           	
	           	Integer x = Math.round ( part_x1 + VirtualScreenBean.vScreenSpinnerX  );
	           	
	           	Integer y = Math.round ( ( ( Float.valueOf(returnMap.get("y").toString()).floatValue() * VirtualScreenBean.vScreenSpinnerHeight / VirtualScreenBean.vScreenResizeY) / Ampl_factor) + VirtualScreenBean.vScreenSpinnerY)  ;
            
                robot.mouseMove(x, y);
            	
            } else if (action.equals("onkeydown")) {
            	
            	Robot robot = new Robot();
            	
            	Integer key = Integer.valueOf(returnMap.get("k").toString()).intValue();
            
            	//logger.debug("key onkeydown -1 "+key);
            	boolean doAction = true;
            	
            	if (key == 221) {
            		key = 61;
            	} else if (key == -1) {
            		
            		String charValue = returnMap.get("c").toString();
            		
            		//key = KeyEvent.VK_ADD;
            		doAction = false;
            		
            		for (Iterator<Integer> iter = this.currentPressedKeys.keySet().iterator();iter.hasNext();) {
            			Integer storedKey = iter.next();
            			
            			robot.keyRelease(storedKey);
            			
            		}
            		
            		this.currentPressedKeys = new HashMap<Integer,Boolean>();
            		
            		this.pressSpecialSign(charValue, robot);
            	} else if (key == 188) {
            		key = 44;
            	} else if (key == 189) {
            		key = 109;
            	} else if (key == 190) {
            		key = 46;
            	} else if (key == 191) {
            		key = 47;
            	}
            	
            	//logger.debug("key onkeydown -2 "+key);
            	
            	if (doAction) {
            		
            		this.currentPressedKeys.put(key, true);
            		
            		robot.keyPress(key);
            	}
            	
            } else if (action.equals("onkeyup")) {
            	
            	Robot robot = new Robot();
            	
            	Integer key = Integer.valueOf(returnMap.get("k").toString()).intValue();
            
            	//logger.debug("key onkeyup 1- "+key);
            	
            	boolean doAction = true;
            	
            	if (key == 221) {
            		key = 61;
            	} else if (key == -1) {
            		doAction = false;
            	} else if (key == 188) {
            		key = 44;
            	} else if (key == 189) {
            		key = 109;
            	} else if (key == 190) {
            		key = 46;
            	} else if (key == 191) {
            		key = 47;
            	}
            	
            	//logger.debug("key onkeyup 2- "+key);
            	
            	if (doAction) {
            		
            		if (this.currentPressedKeys.containsKey(key)) {
            			this.currentPressedKeys.remove(key);
            			
            			robot.keyRelease(key);
            			
            		}
            		
            	}
            	
            } else if (action.equals("paste")) {
            	
            	Robot robot = new Robot();
            	
            	String paste = returnMap.get("paste").toString();
            
            	this.pressSpecialSign(paste, robot);
            	
            } else if (action.equals("copy")) {
            	
            	Robot robot = new Robot();
            	
            	String paste = this.getHighlightedText(robot);
            	
            	HashMap<Integer,String> map = new HashMap<Integer,String>();
            	map.put(0,"copiedText");
            	map.put(1,paste);
            	
            	String clientId = returnMap.get("clientId").toString();
            
            	//public synchronized int sendMessageWithClientById(Object newMessage, String clientId)
            	
            	invoke("sendMessageWithClientById",new Object[] { map, clientId }, this);
            	
            } else if (action.equals("show")) {
            	
            	Robot robot = new Robot();
            	
            	String paste = this.getClipboardText();
            	
            	HashMap<Integer,String> map = new HashMap<Integer,String>();
            	map.put(0,"copiedText");
            	map.put(1,paste);
            	
            	String clientId = returnMap.get("clientId").toString();
            
            	//public synchronized int sendMessageWithClientById(Object newMessage, String clientId)
            	
            	invoke("sendMessageWithClientById",new Object[] { map, clientId }, this);
            	
            }
            
            //KeyEvent.VK
            //KeyEvent.
    		
    	} catch (Exception err) {
    		logger.error( "[sendRemoteCursorEvent]", err );
    	}
    }
    
    public String getClipboardText() {
		try {
			// get the system clipboard
			Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			// get the contents on the clipboard in a
			// transferable object
			Transferable clipboardContents = systemClipboard.getContents(null);
			
			// check if clipboard is empty
			if (clipboardContents == null) {
				
				//Clipboard is empty!!!
				return ("");
			
				// see if DataFlavor of
				// DataFlavor.stringFlavor is supported
			} else if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			
				// return text content
				String returnText = (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);
				
				return returnText;
			}
		
			return "";
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return "";
	}
    
    private String getHighlightedText(Robot instance)
    {
      
      try{

         //clippy.setContents( selection,selection  );
        
         //logger.debug("os.name :: "+System.getProperty("os.name"));
    	 
         if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
	    	 
        	 //logger.debug("IS WINDOWS");
        	 
        	 //dr�ckt STRG+C == copy
        	 instance.keyPress( KeyEvent.VK_CONTROL );
        	 Thread.sleep(200);
             instance.keyPress(KeyEvent.VK_C);
             Thread.sleep(200);
             instance.keyRelease(KeyEvent.VK_C);    
             Thread.sleep(200);
             instance.keyRelease( KeyEvent.VK_CONTROL );
             Thread.sleep(200);
        	 
         } else {
        	 
        	 //logger.debug("IS MAC");
        	 
        	 //Macintosh simulate Copy
        	 instance.keyPress( 157 );
        	 Thread.sleep(200);
	         instance.keyPress( 67 );
	         Thread.sleep(200);
	         instance.keyRelease( 67 );   
	         Thread.sleep(200);
	         instance.keyRelease( 157 );
	         Thread.sleep(200);
	         
         }
         
		 String charValue = this.getClipboardText();
		 
		 //JOptionPane.showMessageDialog(ScreenShare.instance.contentPane, charValue);
		 
		 return charValue;
          
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
      return "";
      //clippy.setContents( clippysContent ,null); //zur�cksetzen vom alten Kontext
    }
    
    private void pressSpecialSign(String charValue, Robot instance)
    {
      Clipboard clippy = Toolkit.getDefaultToolkit().getSystemClipboard();
      //Transferable clippysContent = clippy.getContents( null );
      try{

		 Transferable transferableText = new StringSelection(charValue);
         clippy.setContents( transferableText, null  );
        
         //logger.debug("os.name :: "+System.getProperty("os.name"));
    	 
         if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
	    	 
        	 //logger.debug("IS WINDOWS");
        	 
        	 //dr�ckt STRG+V == einf�gen
        	 instance.keyPress( KeyEvent.VK_CONTROL );
        	 Thread.sleep(100);
             instance.keyPress(KeyEvent.VK_V);
             Thread.sleep(100);
             instance.keyRelease(KeyEvent.VK_V);   
             Thread.sleep(100);
             instance.keyRelease( KeyEvent.VK_CONTROL );
             Thread.sleep(100);
        	 
         } else {
        	 
        	 //logger.debug("IS MAC");
        	 
        	 //Macintosh simulate Insert
        	 instance.keyPress( 157 );
        	 Thread.sleep(100);
	         instance.keyPress( 86 );
	         Thread.sleep(100);
	         instance.keyRelease( 86 );    
	         Thread.sleep(100);
	         instance.keyRelease( 157 );
	         Thread.sleep(100);
	         
         }
        
          //oder wenn das keine Exception wirft
    	 /*instance.keyPress( KeyEvent.VK_PASTE );
    	 instance.keyRelease( KeyEvent.VK_PASTE );*/
         
         //JOptionPane.showMessageDialog(ScreenShare.instance.contentPane, charValue);
         
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
      //clippy.setContents( clippysContent ,null); //zur�cksetzen vom alten Kontext
    }

    public void resultReceived( IPendingServiceCall call ) {
    	try {
    		
    		//logger.debug( "service call result: " + call );

	        if ( call.getServiceMethodName().equals("connect") ) {
	        	
	        	isConnected = true;
	        	setConnectionAsSharingClient();
	
	        } else if (call.getServiceMethodName().equals("setConnectionAsSharingClient")) {
				
				//logger.debug("call get Method Name "+call.getServiceMethodName());
				
				Object o = call.getResult();
				
				//logger.debug("Result Map Type "+o.getClass().getName());
				
				Map returnMap = (Map) o;
				
				//logger.debug("result "+returnMap.get("result"));
				
				//for (Iterator iter = returnMap.keySet().iterator();iter.hasNext();) {
				//	logger.debug("key "+iter.next());
				//}
				
				if (!Boolean.valueOf(returnMap.get("alreadyPublished").toString()).booleanValue()) {
					
					logger.debug("Stream not yet started - do it ");
					
					createStream( this );
					
				} else {

					if (this.capture != null) {
						this.capture.resetBuffer();
					}
					
					logger.debug("The Stream was already started ");
				}
				
				if (returnMap.get("modus") != null) {
					if (returnMap.get("modus").toString().equals("startStreaming")) {
						this.startButton.setEnabled(false);
						this.stopButton.setEnabled(true);
					} else if (returnMap.get("modus").toString().equals("startRecording")) {
						this.startButtonRecording.setEnabled(false);
						this.stopButtonRecording.setEnabled(true);
					}
				} else {
					throw new Exception("Could not aquire modus for event setConnectionAsSharingClient");
				}
				
			} else if (call.getServiceMethodName().equals("createStream")) {
					
				publishStreamId = (Integer) call.getResult();
				logger.debug( "createPublishStream result stream id: " + publishStreamId );
				logger.debug( "publishing video by name: " + publishName );
				publish( publishStreamId, publishName, "live", this );
	
				logger.debug( "setup capture thread");
				
				logger.debug( "setup capture thread getCanonicalName "+VirtualScreenBean.class.getCanonicalName());
				logger.debug( "setup capture thread getName "+VirtualScreenBean.class.getName());
				logger.debug( "setup capture thread vScreenSpinnerWidth "+VirtualScreenBean.vScreenSpinnerWidth);
				logger.debug( "setup capture thread vScreenSpinnerHeight "+VirtualScreenBean.vScreenSpinnerHeight);
	
                capture = new CaptureScreen(VirtualScreenBean.vScreenSpinnerX,
				                        VirtualScreenBean.vScreenSpinnerY,
				                        VirtualScreenBean.vScreenSpinnerWidth,
				                        VirtualScreenBean.vScreenSpinnerHeight,
				                        VirtualScreenBean.vScreenResizeX,
				                        VirtualScreenBean.vScreenResizeY
									);
	
				if (thread == null)
				{
					thread = new Thread(capture);
					thread.start();
				}
				capture.start();
	
			} else if (call.getServiceMethodName().equals("screenSharerAction")) {
				
				logger.debug("call ### get Method Name "+call.getServiceMethodName());
				
				Object o = call.getResult();
				
				logger.debug("Result Map Type "+o.getClass().getName());
				
				Map returnMap = (Map) o;
				
				//logger.debug("result "+returnMap.get("result"));
				
				//for (Iterator iter = returnMap.keySet().iterator();iter.hasNext();) {
				//	logger.debug("key "+iter.next());
				//}
				
				if (returnMap.get("result").equals("stopAll")) {
				
					logger.debug("Stopping to stream, there is neither a Desktop Sharing nor Recording anymore");
					
					stopStream();
				
				}
				
				//logger.debug("Stop No Doubt!");
				//stopStream();
				
			} else if (call.getServiceMethodName().equals("setNewCursorPosition")) {
				
				//Do not do anything
				
			} else {
				
				logger.debug("Unkown method "+call.getServiceMethodName());
				
			}
	        
    	} catch (Exception err) {
    		logger.error("[resultReceived]",err);
    	}
    }


    public void pushVideo( int len, byte[] video, long ts) throws IOException {

		if (!startPublish) return;

        if ( buffer == null ) {
            buffer = ByteBuffer.allocate( 1024 );
            buffer.setAutoExpand( true );
        }

        buffer.clear();
        buffer.put( video );
        buffer.flip();

        VideoData videoData = new VideoData( buffer );
        videoData.setTimestamp( (int) ts );

        kt++;

//        if ( kt < 10 ) {
//            logger.debug( "+++ " + videoData );
//            System.out.println( "+++ " + videoData);
//        }

        RTMPMessage rtmpMsg = new RTMPMessage();
        rtmpMsg.setBody( videoData );
        publishStreamData( publishStreamId, rtmpMsg );
    }

	// ------------------------------------------------------------------------
	//
	// CaptureScreen
	//
	// ------------------------------------------------------------------------


	private final class CaptureScreen extends Object implements Runnable
	{
        private volatile int x = 0;
        private volatile int y = 0;
        private volatile int resizeX;
        private volatile int resizeY;
        
        private volatile int width = resizeX; 	//320
        private volatile int height = resizeY; 	//240
        
        private int timeBetweenFrames = 1000; //frameRate
        
        private volatile long timestamp = 0;

        private volatile boolean active = true;
        private volatile boolean stopped = false;
        private byte[] previousItems = null;

		// ------------------------------------------------------------------------
		//
		// Constructor
		//
		// ------------------------------------------------------------------------


		public CaptureScreen(final int x, final int y, final int width, final int height,int resizeX,int resizeY)
        {
			
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.resizeX = resizeX;
            this.resizeY = resizeY;			
            
            if(VirtualScreenBean.vScreenScaleFactor.equals(ScreenShareRTMPT.instance.label1090)) {
            	timeBetweenFrames = 100;
            } else {
            	timeBetweenFrames = 1000;
            }
            	

            logger.debug( "CaptureScreen: x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + ",resizeX="+ resizeX + " resizeY= " +resizeY );

		}


		// ------------------------------------------------------------------------
		//
		// Public
		//
		// ------------------------------------------------------------------------

		public void setOrigin(final int x, final int y)
		{
			this.x = x;
			this.y = y;
		}


		public void start()
		{
			stopped = false;
		}


		public void stop()
		{
			stopped = true;
		}

		public void release()
		{
			active = false;
		}

		public void resetBuffer() {
			this.previousItems = null;
		}

		// ------------------------------------------------------------------------
		//
		// Thread loop
		//
		// ------------------------------------------------------------------------

		public void run()
        {
                final int blockWidth = 32;
                final int blockHeight = 32;

                int frameCounter = 0;

                int orig_width = width;
                int orig_height = height;
               
               
                try
                {
                        Robot robot = new Robot();

                        this.previousItems = null;
                        
                        while (active)
                        {
                                final long ctime = System.currentTimeMillis();
                                
                                
                                width = orig_width;
                                height = orig_height;
                                
                                BufferedImage image = robot.createScreenCapture(new Rectangle(x, y, width, height));
                                
                                int width_new = resizeX;
                            	int height_new = resizeY;
                                width = resizeX;
                                height = resizeY; 
                            	//Resize to 640*480
                            	// Create new (blank) image of required (scaled) size
                            	BufferedImage image_raw = new BufferedImage(width_new, height_new, BufferedImage.TYPE_INT_RGB);    
                            	
                            	
                            	Graphics2D graphics2D = image_raw.createGraphics();
                            	graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            	graphics2D.drawImage(image, 0, 0, width_new, height_new, null);
                            	graphics2D.dispose();
                            		
                            	//End resize
                                
                                int scaledWidth = width;
                                int scaledHeight = height;
                                
                                byte[] current = toBGR(image_raw);
//                                if (scaleFactor != 1F) {
//                                        
//                                        logger.debug("Calc new Scaled Instance ",scaleFactor);
//                                        
//                                        scaledWidth = Float.valueOf(Math.round(width*scaleFactor)).intValue();
//                                        scaledHeight = Float.valueOf(Math.round(height*scaleFactor)).intValue();
//                                        
//                                        Image img = image_raw.getScaledInstance(scaledWidth,
//                                                                                        scaledHeight,Image.SCALE_SMOOTH);
//                                        
//                                        BufferedImage image_scaled = new BufferedImage(scaledWidth, scaledHeight,BufferedImage.TYPE_3BYTE_BGR);
//                                        
//                                        Graphics2D biContext = image_scaled.createGraphics();
//                                        biContext.drawImage(img, 0, 0, null);
//                                        current = toBGR(image_scaled);
//                                } else {
//                                        current = toBGR(image_raw);
//                                }
                                
                                try
                                {
                                        //timestamp += (1000000 / timeBetweenFrames);
                                        timestamp += timeBetweenFrames;

                                        final byte[] screenBytes = encode(current, this.previousItems, blockWidth, blockHeight, scaledWidth, scaledHeight);
                                        pushVideo( screenBytes.length, screenBytes, timestamp);
                                        this.previousItems = current;

                                        if (++frameCounter % 100 == 0) this.previousItems = null;
                                }
                                catch (Exception e)
                                {
                                        e.printStackTrace();
                                }

                                final int spent = (int) (System.currentTimeMillis() - ctime);

                                sendCursorStatus();
                                
                                Thread.sleep(Math.max(0, timeBetweenFrames - spent));
                        }
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
        }


		// ------------------------------------------------------------------------
		//
		// Private
		//
		// ------------------------------------------------------------------------

		private byte[] toBGR(BufferedImage image)
		{
			final int width = image.getWidth();
			final int height = image.getHeight();

			byte[] buf = new byte[3 * width * height];

			final DataBuffer buffer = image.getData().getDataBuffer();

			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					final int rgb = buffer.getElem(y * width + x);
					final int offset = 3 * (y * width + x);

					buf[offset + 0] = (byte) (rgb & 0xFF);
					buf[offset + 1] = (byte) ((rgb >> 8) & 0xFF);
					buf[offset + 2] = (byte) ((rgb >> 16) & 0xFF);
				}
			}

			return buf;
		}


		private byte[] encode(final byte[] current, final byte[] previous, final int blockWidth, final int blockHeight, final int width, final int height) throws Exception
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);

			if (previous == null)
			{
				baos.write(getTag(0x01, 0x03));		// keyframe (all cells)
			}
			else
			{
				baos.write(getTag(0x02, 0x03));		// frame (changed cells)
			}

			// write header
			final int wh = width + ((blockWidth / 16 - 1) << 12);
			final int hh = height + ((blockHeight / 16 - 1) << 12);

			writeShort(baos, wh);
			writeShort(baos, hh);

			// write content
			int y0 = height;
			int x0 = 0;
			int bwidth = blockWidth;
			int bheight = blockHeight;

			while (y0 > 0)
			{
				bheight = Math.min(y0, blockHeight);
				y0 -= bheight;

				bwidth = blockWidth;
				x0 = 0;

				while (x0 < width)
				{
					bwidth = (x0 + blockWidth > width) ? width - x0 : blockWidth;

					final boolean changed = isChanged(current, previous, x0, y0, bwidth, bheight, width, height);

					if (changed)
					{
						ByteArrayOutputStream blaos = new ByteArrayOutputStream(4 * 1024);

						DeflaterOutputStream dos = new DeflaterOutputStream(blaos);

						for (int y = 0; y < bheight; y++)
						{
							dos.write(current, 3 * ((y0 + bheight - y - 1) * width + x0), 3 * bwidth);
						}

						dos.finish();

						final byte[] bbuf = blaos.toByteArray();
						final int written = bbuf.length;

						// write DataSize
						writeShort(baos, written);
						// write Data
						baos.write(bbuf, 0, written);
					}
					else
					{
						// write DataSize
						writeShort(baos, 0);
					}

					x0 += bwidth;
				}
			}

			return baos.toByteArray();
		}

		private void writeShort(OutputStream os, final int n) throws Exception
		{
			os.write((n >> 8) & 0xFF);
			os.write((n >> 0) & 0xFF);
		}

		public boolean isChanged(final byte[] current, final byte[] previous, final int x0, final int y0, final int blockWidth, final int blockHeight, final int width, final int height)
		{
			if (previous == null) return true;

			for (int y = y0, ny = y0 + blockHeight; y < ny; y++)
			{
				final int foff = 3 * (x0 + width * y);
				final int poff = 3 * (x0 + width * y);

				for (int i = 0, ni = 3 * blockWidth; i < ni; i++)
				{
					if (current[foff + i] != previous[poff + i]) return true;
				}
			}

			return false;
		}


		public int getTag(final int frame, final int codec)
		{
			return ((frame & 0x0F) << 4) + ((codec & 0x0F) << 0);
		}
	}

}

