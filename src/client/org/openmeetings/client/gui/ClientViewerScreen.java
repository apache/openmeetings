package org.openmeetings.client.gui;

import java.util.List;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.beans.ClientCursorStatus;
import org.openmeetings.client.beans.ClientFrameBean;
import org.openmeetings.client.beans.ClientViewerRegisterBean;
import org.openmeetings.client.transport.ClientTransportMinaPool;

/**
 * @author sebastianwagner
 *
 */
public class ClientViewerScreen {
	
	private static Logger log = Logger.getLogger(ClientViewerScreen.class);

	public static ClientViewerScreen instance = null;
	
	private java.awt.Container contentPane;
	private JFrame t;
	
	private JButton exitButton;
	private JLabel textWarningArea;
	private JScrollPane scrollPane;
	private JPanel scrollContent;
	private ImageIcon menupointer;
	private ImagePanel menupointerPanel;
	
	private String label728 = "Desktop Viewer";
	private String label729 = "exit";
	public String label736 = "End of Session";
	public String label742 = "Connection was closed by Server";
	
	private boolean mousePointerLoaded = false;
	
	private List<ImagePanel> imageScreens = new LinkedList<ImagePanel>();
	
	private int maxSizeX = 0;
	private int maxSizeY = 0;
	
	public boolean alreadyClosedWarning = false;
	
	public ClientViewerScreen(String host, String port, String SID, String room, String domain, String publicSID, String record, String labelTexts){
		log.debug("captureScreenStop START ");
		
		//JOptionPane.showMessageDialog(t, "publicSID: "+publicSID);
		
		ClientConnectionBean.isViewer = true;
		ClientConnectionBean.port = Integer.parseInt(port);
		ClientConnectionBean.host = host;
		ClientConnectionBean.SID = SID;
		ClientConnectionBean.room = room;
		ClientConnectionBean.domain = domain;	
		ClientConnectionBean.publicSID = publicSID;
		ClientConnectionBean.record = record;
		
		if (labelTexts.length() > 0) {
			String[] textArray = labelTexts.split(";");
			
			this.label728 = textArray[0];
			this.label729 = textArray[1];
			this.label736 = textArray[2];
			this.label742 = textArray[3];
			
		}
		
		if (ClientConnectionBean.record == "yes") {
			ClientConnectionBean.mode = 1;
		}
		
		instance=this;
		
		//instance.showBandwidthWarning("StartScreen: "+SID+" "+room+" "+domain+" "+url);
		this.initMainFrame();
	}
	
	private void initMainFrame() {
		try {
			// TODO Auto-generated method stub
			UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
	
	
	//		 make Web Start happy
	//		 see http://developer.java.sun.com/developer/bugParade/bugs/4155617.html
			UIManager.getLookAndFeelDefaults().put( "ClassLoader", getClass().getClassLoader()  );
			
			
			menupointer = createImageIcon("/menupointer.png");
			
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			
			t = new JFrame(this.label728);
			contentPane = t.getContentPane();
			contentPane.setBackground(Color.WHITE);
			contentPane.setLayout(null);
			
			textWarningArea = new JLabel();
			contentPane.add(textWarningArea);
			textWarningArea.setBounds(2, screenSize.height-250, 400, 30);
			
			exitButton = new JButton( this.label729 );
			exitButton.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					try {
						// TODO Auto-generated method stub
						ClientTransportMinaPool.closeSession();
						t.setVisible(false);
						System.exit(0);
					} catch (Exception e) {
						log.error("[actionPerformed1]",e);
					}
				}
			});
			exitButton.setBounds(screenSize.width-400, screenSize.height-250, 190, 24);
			contentPane.add(exitButton);
			
			
			
			scrollContent = new JPanel();
			
			scrollContent.setPreferredSize(new Dimension(1600,1200));
			//scrollContent.setBounds(0, 0, 300, 1400);
			
			//, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
	        //ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
			
			scrollPane = new JScrollPane(scrollContent);
			//scrollPane.setPreferredSize(new Dimension(300, 250));
			
			//Recalc more then 100 because windows does not calculate the width correctly
			scrollPane.setBounds(0, 0, screenSize.width-206, screenSize.height-254);
			
			scrollPane.setViewportView(scrollContent);
			
			scrollPane.setViewportBorder(
	                BorderFactory.createLineBorder(Color.black));

			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			//scrollPane.setColumnHeaderView(columnView);
			//scrollPane.setRowHeaderView(rowView);

//			scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,
//			                    new ClientCorner());
//			scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
//			                    new ClientCorner());


			//scrollPane.set
			
			contentPane.add(scrollPane);
			
			t.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					try {
						// TODO Auto-generated method stub
						ClientTransportMinaPool.closeSession();
						t.setVisible(false);
						System.exit(0);
					} catch (Exception e1) {
						log.error("[actionPerformed2]",e1);
					}
				}
	
			});
			
			t.pack();
			t.setLocation(100, 100);
			t.setSize(screenSize.width-200, screenSize.height-200);
			t.setVisible(true);
			t.setResizable(false);
			
			log.debug("initialized");
			
			
			
			
			ClientTransportMinaPool.startConnections();
			
		
		} catch (Exception err) {
			
			log.error("[initMainFrame]",err);
			
		}
		
	}
	
	protected static ImageIcon createImageIcon(String path) throws Exception {
	    java.net.URL imgURL = ClientStartScreen.class.getResource(path);
	    return new ImageIcon(imgURL);
	}
	
	public void showWarningPopUp(String warning){
		JOptionPane.showMessageDialog(t, warning);
	}
	
	public void showBandwidthWarning(String warning){
		textWarningArea.setText(warning);
		//JOptionPane.showMessageDialog(t, warning);
	}
	
	/**
	 * 
	 */
	public void doInitMessage() {
		try {
			
			ClientViewerRegisterBean clientViewerRegisterBean = new ClientViewerRegisterBean();
			clientViewerRegisterBean.setMode(5);
			clientViewerRegisterBean.setPublicSID(ClientConnectionBean.publicSID);
			
			ClientTransportMinaPool.sendMessage(clientViewerRegisterBean);
			
		} catch (Exception err) {
			
			log.error("[doInitMessage]",err);
			
		}
	}
	
	public void updateCursor(ClientCursorStatus clientCursorStatus) {
		try {
			
			this.showBandwidthWarning("Receive updateCursor");;
			
			//if (true) return;
			
			/*
			if (!this.mousePointerLoaded) {
				
				this.menupointerPanel = new ImagePanel(clientCursorStatus.getX(), clientCursorStatus.getY());
				this.menupointerPanel.setBounds(clientCursorStatus.getX(), clientCursorStatus.getY(), 22, 22);
				
				this.menupointerPanel.setImages(menupointer.getImage(), 22, 22);
				
			}
			
			this.menupointerPanel.setBounds(clientCursorStatus.getX(), clientCursorStatus.getY(), 22, 22);
			this.menupointerPanel.repaint();
			*/
			
			//this.menupointerPanel.setImages(menupointer.getImage(), 22, 22);
			
		} catch (Exception ex) {
			log.error("[add]",ex);
			this.showWarningPopUp(ex.getMessage());
		}
	}
	
	public void addClientFrameBean(ClientFrameBean clientFrameBean) {
		try {
			
//			String imagePath_1 = "pic_"+clientFrameBean.getSequenceNumber()+".gzip";
//			FileOutputStream fos_1 = new FileOutputStream(imagePath_1);
//			fos_1.write(clientFrameBean.getImageBytes());
//			fos_1.close();
			
			ByteArrayInputStream byteGzipIn = new ByteArrayInputStream(clientFrameBean.getImageBytes());
    		GZIPInputStream gZipIn = new GZIPInputStream(byteGzipIn);

    		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    		
    		byte[] buffer = new byte[1024];
    		int count = 0;
    		while ((count = gZipIn.read(buffer)) > 0 ){
    			bytesOut.write(buffer,0,count);
			}
			bytesOut.close();
			gZipIn.close();
			
//			BufferedImage bufferedImage = new BufferedImage(bytesOut.toByteArray());
//			BufferedImage image = new BufferedImage(clientFrameBean.getWidth(), clientFrameBean.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//			Graphics graphics = image.createGraphics();
//
//			graphics.
			
//			String imagePath_2 = "pic_"+clientFrameBean.getSequenceNumber()+".jpg";
//			FileOutputStream fos_2 = new FileOutputStream(imagePath_2);
//			fos_2.write(bytesOut.toByteArray());
//			fos_2.close();
			
//			Image im_left = Toolkit.getDefaultToolkit().createImage(bytesOut.toByteArray());
//			
			
			ByteArrayInputStream in = new ByteArrayInputStream(bytesOut.toByteArray());
			
			BufferedImage bufferedImage = ImageIO.read(in);
			
			log.debug("Image No: "+clientFrameBean.getSequenceNumber());
			log.debug("Type of New Image: "+bufferedImage.getType());
			
			ImagePanel iPanel = this.getImagePanel(clientFrameBean.getXValue(), clientFrameBean.getYValue(), clientFrameBean.getWidth(), clientFrameBean.getHeight());
			
			if (iPanel == null) {
				return;
			}
			
			iPanel.setSequenceNumber(clientFrameBean.getSequenceNumber());
			
			log.debug("PANEL SET IMAGE "+bytesOut.toByteArray().length);
			
			//int s_width = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * clientFrameBean.getWidth())).intValue();
			//int s_height = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * clientFrameBean.getHeight())).intValue();
			
			
			iPanel.setImages(bufferedImage,clientFrameBean.getWidth(),clientFrameBean.getHeight());
			
			
			//contentPane.repaint();
			//t.setVisible(true);
			
			
		} catch (Exception err) {
			log.error("[add]",err);
			//this.showWarningPopUp(err.getMessage());
		}
	}
	
	public ImagePanel getImagePanel(int x, int y, int width, int height){
		try {
			
			for (ImagePanel iPanel : imageScreens) {
				
				if (iPanel.getXPosition() == x && iPanel.getYPosition() == y) {
					//return null;
					return iPanel;
				}
				
			}
			
//			int s_x = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * x)).intValue();
//			int s_y = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * y)).intValue();
//			int s_width = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * width)).intValue();
//			int s_height = Long.valueOf(Math.round(ClientConnectionBean.imgQuality * height)).intValue();
			
			
			ImagePanel iPanel = new ImagePanel(x,y);
			iPanel.setBounds(x, y, width, height);
			log.debug("NEW PANEL "+iPanel.getXPosition()+" "+iPanel.getYPosition());
			
			//contentPane.add(iPanel);
			
			scrollContent.add(iPanel);
			scrollContent.setComponentZOrder(iPanel, 1);
			
			imageScreens.add(iPanel);
			
			if (y == 0) {
				if (maxSizeX < (x + width)){
					maxSizeX = (x + width);
				}
			}
			

			if (x == 0) {
				if (maxSizeY < (y + height)){
					maxSizeY = (y + height);
				}
			}
			
			log.debug("New Size "+maxSizeX+","+maxSizeY);
			
			//scrollContent.setPreferredSize(new Dimension(maxSizeX,maxSizeY));
			//scrollContent.revalidate();
			//scrollPane.repaint();
			
			//t.setSize(maxSizeX+30, maxSizeY+20);
			
			//scrollContent.repaint();
			//scrollPane.updateUI();
			//scrollPane.invalidate();
			//scrollPane.repaint();
			//t.repaint();
			
			return iPanel;
			
		} catch (Exception err) {
			log.error("[getImagePanel]",err);
		}
		return null;
	}

	public static void main(String[] args){
		String host = args[0];
		String port = args[1];
		String SID = args[2]; 
		String room = args[3];
		String domain = args[4];
		String publicSID = args[5];
		String record = args[6];
		String labelTexts = args[7];
		new ClientViewerScreen(host,port,SID,room,domain,publicSID,record,labelTexts);
	}

	

}
