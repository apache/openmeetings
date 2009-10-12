package org.openmeetings.client.gui;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientVirtualScreenBean;
import org.openmeetings.client.screen.ClientBlankArea;

public class ClientVirtualScreen {
	
	private static Logger log = Logger.getLogger(ClientStartScreen.class);
	
	public static ClientVirtualScreen instance = null;
	
	public boolean showWarning = true;
	
	public boolean doUpdateBounds = true;

	public ClientVirtualScreen() throws Exception{
		instance = this;
		
		ClientStartScreen.instance.tFieldScreenZoom = new JLabel();
		ClientStartScreen.instance.tFieldScreenZoom.setBounds(30, 120, 200, 20);
		ClientStartScreen.instance.tFieldScreenZoom.setText(ClientStartScreen.instance.label734);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.tFieldScreenZoom);
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		ClientVirtualScreenBean.screenratio = screenSize.getHeight()/screenSize.getWidth();
		ClientVirtualScreenBean.screenWidthMax = Double.valueOf(screenSize.getWidth()).intValue();
		ClientVirtualScreenBean.screenHeightMax = Double.valueOf(screenSize.getHeight()).intValue();

		ClientVirtualScreenBean.vScreenHeight = Long.valueOf(Math.round( ClientVirtualScreenBean.vScreenWidth*ClientVirtualScreenBean.screenratio )).intValue();
		
		int width = ClientVirtualScreenBean.vScreenWidth;
		int height = Long.valueOf(Math.round(width*ClientVirtualScreenBean.screenratio )).intValue();
		
		ClientStartScreen.instance.vScreenIconLeft = new JLabel();		
		ClientStartScreen.instance.vScreenIconLeft.setBounds(14, 162+(height/2), 32, 16);
		
		ClientStartScreen.instance.vScreenIconRight = new JLabel();
		ClientStartScreen.instance.vScreenIconRight.setBounds(30+width-16, 162+(height/2), 32, 16);
		
		ClientStartScreen.instance.vScreenIconUp = new JLabel();
		ClientStartScreen.instance.vScreenIconUp.setBounds(30+(width/2)-8, 162-8, 16, 32);

		ClientStartScreen.instance.vScreenIconDown = new JLabel();
		ClientStartScreen.instance.vScreenIconDown.setBounds(30+(width/2)-8, 162+height-8, 16, 32);	
		
		Image im_left = ImageIO.read(ClientStartScreen.class.getResource("/1leftarrow.png"));	
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(ClientStartScreen.class.getResource("/1rightarrow.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(ClientStartScreen.class.getResource("/1uparrow.png"));	
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(ClientStartScreen.class.getResource("/1downarrow.png"));		
		ImageIcon iIcon4 = new ImageIcon(im_down);
		
		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);		
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);	
		ClientStartScreen.instance.vScreenIconLeft.add(jLab1);	
		ClientStartScreen.instance.vScreenIconLeft.add(jLab2);
		ClientStartScreen.instance.vScreenIconLeft.setToolTipText(ClientStartScreen.instance.label735);
		ClientVirtualScreenXMouseListener xLeftMouseListener = new ClientVirtualScreenXMouseListener();
		ClientStartScreen.instance.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		ClientStartScreen.instance.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vScreenIconLeft);
		
		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);		
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);			
		ClientStartScreen.instance.vScreenIconRight.add(jLab3);
		ClientStartScreen.instance.vScreenIconRight.add(jLab4);
		ClientStartScreen.instance.vScreenIconRight.setToolTipText(ClientStartScreen.instance.label735);
		ClientVirtualScreenWidthMouseListener widthMouseListener = new ClientVirtualScreenWidthMouseListener();
		ClientStartScreen.instance.vScreenIconRight.addMouseListener(widthMouseListener);
		ClientStartScreen.instance.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vScreenIconRight);
		
		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);		
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);			
		ClientStartScreen.instance.vScreenIconUp.add(jLab5);
		ClientStartScreen.instance.vScreenIconUp.add(jLab6);
		ClientStartScreen.instance.vScreenIconUp.setToolTipText(ClientStartScreen.instance.label737);
		ClientVirtualScreenYMouseListener yMouseListener = new ClientVirtualScreenYMouseListener();
		ClientStartScreen.instance.vScreenIconUp.addMouseListener(yMouseListener);
		ClientStartScreen.instance.vScreenIconUp.addMouseMotionListener(yMouseListener);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vScreenIconUp);
		
		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);		
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);			
		ClientStartScreen.instance.vScreenIconDown.add(jLab7);
		ClientStartScreen.instance.vScreenIconDown.add(jLab8);
		ClientStartScreen.instance.vScreenIconDown.setToolTipText(ClientStartScreen.instance.label737);
		ClientVirtualScreenHeightMouseListener heightMouseListener = new ClientVirtualScreenHeightMouseListener();
		ClientStartScreen.instance.vScreenIconDown.addMouseListener(heightMouseListener);
		ClientStartScreen.instance.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vScreenIconDown);
		

		ClientStartScreen.instance.virtualScreen = new ClientBlankArea(new Color(255,255,255,100));
		ClientStartScreen.instance.virtualScreen.setOpaque(true);
		ClientStartScreen.instance.virtualScreen.setHorizontalAlignment(SwingConstants.LEFT);
		ClientStartScreen.instance.virtualScreen.setVerticalAlignment(SwingConstants.TOP);
		ClientStartScreen.instance.virtualScreen.setText(ClientVirtualScreenBean.screenWidthMax+":"+ClientVirtualScreenBean.screenHeightMax);
		ClientStartScreen.instance.virtualScreen.setBounds(30, 170, ClientVirtualScreenBean.vScreenWidth, ClientVirtualScreenBean.vScreenHeight);
		ClientVirtualScreenMouseListener vListener = new ClientVirtualScreenMouseListener();
		ClientStartScreen.instance.virtualScreen.addMouseListener(vListener);
		ClientStartScreen.instance.virtualScreen.addMouseMotionListener(vListener);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.virtualScreen);			
		
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);		
		
		Image img = imageScreen.getScaledInstance(width, height ,Image.SCALE_SMOOTH);
		//imageScreen.
		log.debug("img"+img);
		ImageIcon image = new ImageIcon(img);
		
		ClientStartScreen.instance.blankArea = new JLabel(image);
		ClientStartScreen.instance.blankArea.setBounds(30, 170, width, height);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.blankArea);
		
		//Spinner X
		ClientVirtualScreenBean.vScreenSpinnerX = 0;
		ClientStartScreen.instance.vscreenXLabel = new JLabel();
		ClientStartScreen.instance.vscreenXLabel.setText(ClientStartScreen.instance.label738);
		ClientStartScreen.instance.vscreenXLabel.setBounds(250, 170, 150, 24);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vscreenXLabel);
		
		ClientStartScreen.instance.jVScreenXSpin = new JSpinner(
					new SpinnerNumberModel(ClientVirtualScreenBean.vScreenSpinnerX, 0, ClientVirtualScreenBean.screenWidthMax, 1)
				);
		ClientStartScreen.instance.jVScreenXSpin.setBounds(400, 170, 60, 24);
		ClientStartScreen.instance.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueXSpin();
			}	
		});	
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.jVScreenXSpin);
		
		//Spinner Y
		ClientVirtualScreenBean.vScreenSpinnerY = 0;
		ClientStartScreen.instance.vscreenYLabel = new JLabel();
		ClientStartScreen.instance.vscreenYLabel.setText(ClientStartScreen.instance.label739);
		ClientStartScreen.instance.vscreenYLabel.setBounds(250, 200, 150, 24);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vscreenYLabel);
		
		ClientStartScreen.instance.jVScreenYSpin = new JSpinner(
					new SpinnerNumberModel(ClientVirtualScreenBean.vScreenSpinnerY, 0, ClientVirtualScreenBean.screenHeightMax, 1)
				);
		ClientStartScreen.instance.jVScreenYSpin.setBounds(400, 200, 60, 24);
		ClientStartScreen.instance.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueYSpin();
			}	
		});	
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.jVScreenYSpin);
		
		//Spinner Width
		ClientVirtualScreenBean.vScreenSpinnerWidth = ClientVirtualScreenBean.screenWidthMax;
		ClientStartScreen.instance.vscreenWidthLabel = new JLabel();
		ClientStartScreen.instance.vscreenWidthLabel.setText(ClientStartScreen.instance.label740);
		ClientStartScreen.instance.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vscreenWidthLabel);
		
		ClientStartScreen.instance.jVScreenWidthSpin = new JSpinner(
					new SpinnerNumberModel(ClientVirtualScreenBean.vScreenSpinnerWidth, 0, ClientVirtualScreenBean.screenWidthMax, 1)
				);
		ClientStartScreen.instance.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		ClientStartScreen.instance.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueWidthSpin();
			}	
		});	
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.jVScreenWidthSpin);		
		
		//Spinner Height
		ClientVirtualScreenBean.vScreenSpinnerHeight = ClientVirtualScreenBean.screenHeightMax;
		ClientStartScreen.instance.vscreenHeightLabel = new JLabel();
		ClientStartScreen.instance.vscreenHeightLabel.setText(ClientStartScreen.instance.label741);
		ClientStartScreen.instance.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.vscreenHeightLabel);
		
		ClientStartScreen.instance.jVScreenHeightSpin = new JSpinner(
					new SpinnerNumberModel(ClientVirtualScreenBean.vScreenSpinnerHeight, 0, ClientVirtualScreenBean.screenHeightMax, 1)
				);
		ClientStartScreen.instance.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		ClientStartScreen.instance.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueHeightSpin();
			}	
		});	
		ClientStartScreen.instance.t.add(ClientStartScreen.instance.jVScreenHeightSpin);	
		
	}	
	
	

	
	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = Integer.valueOf(ClientStartScreen.instance.jVScreenXSpin.getValue().toString()).intValue();
			if(ClientVirtualScreenBean.vScreenSpinnerWidth+newX > ClientVirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING X "+ClientVirtualScreenBean.vScreenSpinnerWidth+" "+newX);
				newX=ClientVirtualScreenBean.screenWidthMax-ClientVirtualScreenBean.vScreenSpinnerWidth;
				ClientStartScreen.instance.jVScreenXSpin.setValue(newX);
				if (this.showWarning) ClientShowStatusMessage.showStatusMessage("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				ClientVirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			ClientVirtualScreenBean.vScreenSpinnerX = Integer.valueOf(ClientStartScreen.instance.jVScreenXSpin.getValue().toString()).intValue();
		}
	}	
	
	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = Integer.valueOf(ClientStartScreen.instance.jVScreenYSpin.getValue().toString()).intValue();
			if(ClientVirtualScreenBean.vScreenSpinnerHeight+newY > ClientVirtualScreenBean.screenHeightMax){
				newY=ClientVirtualScreenBean.screenHeightMax-ClientVirtualScreenBean.vScreenSpinnerHeight;
				ClientStartScreen.instance.jVScreenYSpin.setValue(newY);
				if (this.showWarning) ClientShowStatusMessage.showStatusMessage("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				ClientVirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			ClientVirtualScreenBean.vScreenSpinnerY = Integer.valueOf(ClientStartScreen.instance.jVScreenYSpin.getValue().toString()).intValue();
		}
	}	
	
	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = Integer.valueOf(ClientStartScreen.instance.jVScreenWidthSpin.getValue().toString()).intValue();
			if(ClientVirtualScreenBean.vScreenSpinnerX+newWidth > ClientVirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING WIDTH");
				newWidth=ClientVirtualScreenBean.screenWidthMax-ClientVirtualScreenBean.vScreenSpinnerX;
				ClientStartScreen.instance.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)ClientShowStatusMessage.showStatusMessage("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				ClientVirtualScreenBean.vScreenSpinnerWidth = newWidth;
				updateVScreenBounds();	
			}
		} else {
			ClientVirtualScreenBean.vScreenSpinnerWidth = Integer.valueOf(ClientStartScreen.instance.jVScreenWidthSpin.getValue().toString()).intValue();
		}
		//System.out.println("calcNewValueWidthSpin "+ClientVirtualScreenBean.vScreenSpinnerWidth);
	}	
	
	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = Integer.valueOf(ClientStartScreen.instance.jVScreenHeightSpin.getValue().toString()).intValue();
			if(ClientVirtualScreenBean.vScreenSpinnerY+newHeight > ClientVirtualScreenBean.screenHeightMax){
				newHeight=ClientVirtualScreenBean.screenHeightMax-ClientVirtualScreenBean.vScreenSpinnerY;
				ClientStartScreen.instance.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)ClientShowStatusMessage.showStatusMessage("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				ClientVirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();	
			}
		} else {
			ClientVirtualScreenBean.vScreenSpinnerHeight = Integer.valueOf(ClientStartScreen.instance.jVScreenHeightSpin.getValue().toString()).intValue();
		}
		//System.out.println("calcNewValueHeightSpin "+ClientVirtualScreenBean.vScreenSpinnerHeight);
	}	
	
	/**
	 * update the bounds of the vScreen
	 * by useing the vars from the Spinners
	 *
	 */
	void updateVScreenBounds(){

			double newvScreenWidth = ClientVirtualScreenBean.vScreenSpinnerWidth * (new Double(ClientVirtualScreenBean.vScreenWidth) / new Double(ClientVirtualScreenBean.screenWidthMax));
			double newvScreenX = ClientVirtualScreenBean.vScreenSpinnerX * (new Double(ClientVirtualScreenBean.vScreenWidth) / new Double(ClientVirtualScreenBean.screenWidthMax));
		
			double newvScreenHeight = ClientVirtualScreenBean.vScreenSpinnerHeight * (new Double(ClientVirtualScreenBean.vScreenHeight) / new Double(ClientVirtualScreenBean.screenHeightMax));
			double newvScreenY = ClientVirtualScreenBean.vScreenSpinnerY * (new Double(ClientVirtualScreenBean.vScreenHeight) / new Double(ClientVirtualScreenBean.screenHeightMax));
			
			//System.out.println("updateVScreenBounds "+newvScreenX+"||"+newvScreenWidth);
			
			ClientStartScreen.instance.vScreenIconLeft.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30-16, 
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+(Long.valueOf(Math.round(newvScreenHeight)).intValue()/2));
			ClientStartScreen.instance.vScreenIconRight.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+Long.valueOf(Math.round(newvScreenWidth)).intValue()-16, 
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+((Long.valueOf(Math.round(newvScreenHeight)).intValue())/2));
			
			ClientStartScreen.instance.vScreenIconUp.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8);
	
			ClientStartScreen.instance.vScreenIconDown.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8+(Long.valueOf(Math.round(newvScreenHeight)).intValue()));
	
			
			ClientStartScreen.instance.virtualScreen.setBounds(
					30+Long.valueOf(Math.round(newvScreenX)).intValue(), 
							170+Long.valueOf(Math.round(newvScreenY)).intValue() ,  
									Long.valueOf(Math.round(newvScreenWidth)).intValue(), 
											Long.valueOf(Math.round(newvScreenHeight)).intValue() );
			
			ClientStartScreen.instance.virtualScreen.setText(ClientVirtualScreenBean.vScreenSpinnerWidth+":"+ClientVirtualScreenBean.vScreenSpinnerHeight);
			
			//System.out.println(30+Long.valueOf(Math.round(newvScreenX)).intValue()+"|||"+  Long.valueOf(Math.round(newvScreenWidth)).intValue());
			
			//System.out.println(170+Long.valueOf(Math.round(newvScreenY)).intValue() +","+ Long.valueOf(Math.round(newvScreenHeight)).intValue() );

	}
	
}
