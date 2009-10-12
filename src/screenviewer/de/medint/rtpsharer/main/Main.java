package de.medint.rtpsharer.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.net.InetAddress;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;

import org.apache.log4j.Logger;

import de.medint.rtpsharer.util.ConfigUtil;

public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
	/** StreamingClass */
	private static Streamer streamer = null;
	

	/** FrameRate */
	private static int frameRate = 20;
	
	/** TargetVideoheight */
	//private static int videoHeight = 768; //=> See ConfigUtil
	
	/** TargerVideoWidth*/
	//private static int videoWidth = 1024; //=> See ConfigUtil
	
	/** Quality */
	private static float quality = 1;
	
	/** Connected */
	private static boolean connection = false;
	
	/** Session ID OM*/
	private static String SID = null;
	
	/** Room id OM */
	private static String ROOM = null;
	
	/** Publilc SID */
	private static String PUBLICSID = null;
	
	/** Servlet URL for Function calls */
	private static String servletUrl = null;
	

	// Visual Components
	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="44,8"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem startMenuItem = null;
	private JMenuItem stopMenuItem = null;
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="364,11"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JLabel connected = null;
	
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(300, 200);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("OpenMeetings ScreenSharer");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			
			jContentPane.add(getConnectionLabel(), connected.getName());
			
		}
		return jContentPane;
	}
	
	/**
	 * Shows the ConnectionLabel
	 */
	//-----------------------------------------------------------------------------------------------------
	private JLabel getConnectionLabel(){
		connected = new JLabel();
		
		if(!ConfigUtil.connection){
				connected.setText("Not Streaming");
				connected.setForeground(Color.RED);
		}
		else{
				connected.setText("Streaming");
				connected.setForeground(Color.GREEN);
		}
		connected.setForeground(Color.RED);
		connected.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		connected.setLocation(0, 0);
		connected.setBounds(0, 0, 10, 10);
		
		return connected;
	}
	//-----------------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getStartMenuItem());
			fileMenu.add(getStopMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getStartMenuItem() {
		if (startMenuItem == null) {
			startMenuItem = new JMenuItem();
			startMenuItem.setText("Start");
			startMenuItem.addActionListener(new StartStreamingClass());
		}
		return startMenuItem;
	}
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getStopMenuItem() {
		if (stopMenuItem == null) {
			stopMenuItem = new JMenuItem();
			stopMenuItem.setText("Stop");
			stopMenuItem.addActionListener(new StopStreamingClass());
		}
		return stopMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setContentPane(getAboutContentPane());
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}
	
	
	/** Starting Streaming */
	public class StartStreamingClass implements ActionListener{

		//@Override
		public void actionPerformed(ActionEvent e) {

			log.debug("actionPerformed :: StartStreamingClass ");
			
			// Start Streaming
			if(streamer !=null){
				streamer.start();
				connected.setText("Streaming");
				connected.setForeground(Color.GREEN);
			}
			
			// Notify viewers
			try{
				ServletFunctions.sendStartSignal();

			}catch(Exception ex){
				log.error("[actionPerformed]",ex);
				System.out.println("Error on ServletCall : " + ex.getMessage());
			}
		}
		
	}
	
	/** Stopping Streaming*/
	public class StopStreamingClass implements ActionListener{

		//@Override
		public void actionPerformed(ActionEvent e) {
			try{
				
				log.debug("actionPerformed :: StopStreamingClass ");
				
				if(streamer !=null){
					streamer.stop();
					connected.setText("Not Streaming");
					connected.setForeground(Color.RED);
				}
				
				// Notify viewers
				ServletFunctions.sendStopSignal();
			}catch(Exception ex){
				System.out.println("Error on ServletCall : " + ex.getMessage());
			}
		}
		
	}

	

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		
		//Checking Params
		ConfigUtil.destinationAddress = args[0];
		ConfigUtil.destinationPort =Integer.parseInt(args[1]);
		
		ConfigUtil.SID = args[2];
		ConfigUtil.ROOM = args[3];
		PUBLICSID =args[7];
		
		ConfigUtil.rtmphostlocal = args[4];
		ConfigUtil.red5httpport = args[5];
		ConfigUtil.webAppRootKey = args[6];
		
		ConfigUtil.PUBLIC_SID = PUBLICSID;
		//ConfigUtil.RECORDER = args[8];
		
		System.out.println("Received input values : ");
		
		log.debug("### Received input values : ");
		
		System.out.println("destinationaddress : " + ConfigUtil.destinationAddress);
		System.out.println("destinationpoprt : " + ConfigUtil.destinationPort);
		//System.out.println("SID : " + ConfigUtil.SID);
		//System.out.println("ROOM : " + ConfigUtil.ROOM);
		System.out.println("PUBLICSID : " + ConfigUtil.PUBLIC_SID);
		
		// Streaming Object
		streamer = new Streamer();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main application = new Main();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
