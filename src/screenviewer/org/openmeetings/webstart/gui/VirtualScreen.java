package org.openmeetings.webstart.gui;

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

import org.openmeetings.webstart.beans.VirtualScreenBean;
import org.openmeetings.webstart.screen.BlankArea;

public class VirtualScreen {
	
	public static VirtualScreen instance = null;
	
	public boolean showWarning = true;
	
	public boolean doUpdateBounds = true;

	public VirtualScreen() throws Exception{
		instance = this;
		
		StartScreen.instance.tFieldScreenZoom = new JLabel();
		StartScreen.instance.tFieldScreenZoom.setBounds(10, 120, 200, 20);
		StartScreen.instance.tFieldScreenZoom.setText("Select your screen Area:");
		StartScreen.instance.t.add(StartScreen.instance.tFieldScreenZoom);
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		VirtualScreenBean.screenratio = screenSize.getHeight()/screenSize.getWidth();
		VirtualScreenBean.screenWidthMax = Double.valueOf(screenSize.getWidth()).intValue();
		VirtualScreenBean.screenHeightMax = Double.valueOf(screenSize.getHeight()).intValue();

		VirtualScreenBean.vScreenHeight = Long.valueOf(Math.round( VirtualScreenBean.vScreenWidth*VirtualScreenBean.screenratio )).intValue();
		
		int width = VirtualScreenBean.vScreenWidth;
		int height = Long.valueOf(Math.round(width*VirtualScreenBean.screenratio )).intValue();
		
		StartScreen.instance.vScreenIconLeft = new JLabel();		
		StartScreen.instance.vScreenIconLeft.setBounds(14, 162+(height/2), 32, 16);
		
		StartScreen.instance.vScreenIconRight = new JLabel();
		StartScreen.instance.vScreenIconRight.setBounds(30+width-16, 162+(height/2), 32, 16);
		
		StartScreen.instance.vScreenIconUp = new JLabel();
		StartScreen.instance.vScreenIconUp.setBounds(30+(width/2)-8, 162-8, 16, 32);

		StartScreen.instance.vScreenIconDown = new JLabel();
		StartScreen.instance.vScreenIconDown.setBounds(30+(width/2)-8, 162+height-8, 16, 32);	
		
		Image im_left = ImageIO.read(StartScreen.class.getResource("/1leftarrow.png"));	
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(StartScreen.class.getResource("/1rightarrow.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(StartScreen.class.getResource("/1uparrow.png"));	
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(StartScreen.class.getResource("/1downarrow.png"));		
		ImageIcon iIcon4 = new ImageIcon(im_down);
		
		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);		
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);	
		StartScreen.instance.vScreenIconLeft.add(jLab1);	
		StartScreen.instance.vScreenIconLeft.add(jLab2);
		StartScreen.instance.vScreenIconLeft.setToolTipText("Change width");
		VirtualScreenXMouseListener xLeftMouseListener = new VirtualScreenXMouseListener();
		StartScreen.instance.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		StartScreen.instance.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		StartScreen.instance.t.add(StartScreen.instance.vScreenIconLeft);
		
		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);		
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);			
		StartScreen.instance.vScreenIconRight.add(jLab3);
		StartScreen.instance.vScreenIconRight.add(jLab4);
		StartScreen.instance.vScreenIconRight.setToolTipText("Change width");
		VirtualScreenWidthMouseListener widthMouseListener = new VirtualScreenWidthMouseListener();
		StartScreen.instance.vScreenIconRight.addMouseListener(widthMouseListener);
		StartScreen.instance.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		StartScreen.instance.t.add(StartScreen.instance.vScreenIconRight);
		
		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);		
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);			
		StartScreen.instance.vScreenIconUp.add(jLab5);
		StartScreen.instance.vScreenIconUp.add(jLab6);
		StartScreen.instance.vScreenIconUp.setToolTipText("Change height");
		VirtualScreenYMouseListener yMouseListener = new VirtualScreenYMouseListener();
		StartScreen.instance.vScreenIconUp.addMouseListener(yMouseListener);
		StartScreen.instance.vScreenIconUp.addMouseMotionListener(yMouseListener);
		StartScreen.instance.t.add(StartScreen.instance.vScreenIconUp);
		
		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);		
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);			
		StartScreen.instance.vScreenIconDown.add(jLab7);
		StartScreen.instance.vScreenIconDown.add(jLab8);
		StartScreen.instance.vScreenIconDown.setToolTipText("Change height");
		VirtualScreenHeightMouseListener heightMouseListener = new VirtualScreenHeightMouseListener();
		StartScreen.instance.vScreenIconDown.addMouseListener(heightMouseListener);
		StartScreen.instance.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		StartScreen.instance.t.add(StartScreen.instance.vScreenIconDown);
		

		StartScreen.instance.virtualScreen = new BlankArea(new Color(255,255,255,100));
		StartScreen.instance.virtualScreen.setOpaque(true);
		StartScreen.instance.virtualScreen.setHorizontalAlignment(SwingConstants.LEFT);
		StartScreen.instance.virtualScreen.setVerticalAlignment(SwingConstants.TOP);
		StartScreen.instance.virtualScreen.setText(VirtualScreenBean.screenWidthMax+":"+VirtualScreenBean.screenHeightMax);
		StartScreen.instance.virtualScreen.setBounds(30, 170, VirtualScreenBean.vScreenWidth, VirtualScreenBean.vScreenHeight);
		VirtualScreenMouseListener vListener = new VirtualScreenMouseListener();
		StartScreen.instance.virtualScreen.addMouseListener(vListener);
		StartScreen.instance.virtualScreen.addMouseMotionListener(vListener);
		StartScreen.instance.t.add(StartScreen.instance.virtualScreen);			
		
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);		
		
		Image img = imageScreen.getScaledInstance(width, height ,Image.SCALE_SMOOTH);
		//imageScreen.
		System.out.println("img"+img);
		ImageIcon image = new ImageIcon(img);
		
		StartScreen.instance.blankArea = new JLabel(image);
		StartScreen.instance.blankArea.setBounds(30, 170, width, height);
		StartScreen.instance.t.add(StartScreen.instance.blankArea);
		
		//Spinner X
		VirtualScreenBean.vScreenSpinnerX = 0;
		StartScreen.instance.vscreenXLabel = new JLabel();
		StartScreen.instance.vscreenXLabel.setText("SharingScreen X:");
		StartScreen.instance.vscreenXLabel.setBounds(250, 170, 150, 24);
		StartScreen.instance.t.add(StartScreen.instance.vscreenXLabel);
		
		StartScreen.instance.jVScreenXSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerX, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		StartScreen.instance.jVScreenXSpin.setBounds(400, 170, 60, 24);
		StartScreen.instance.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueXSpin();
			}	
		});	
		StartScreen.instance.t.add(StartScreen.instance.jVScreenXSpin);
		
		//Spinner Y
		VirtualScreenBean.vScreenSpinnerY = 0;
		StartScreen.instance.vscreenYLabel = new JLabel();
		StartScreen.instance.vscreenYLabel.setText("SharingScreen Y:");
		StartScreen.instance.vscreenYLabel.setBounds(250, 200, 150, 24);
		StartScreen.instance.t.add(StartScreen.instance.vscreenYLabel);
		
		StartScreen.instance.jVScreenYSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerY, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		StartScreen.instance.jVScreenYSpin.setBounds(400, 200, 60, 24);
		StartScreen.instance.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueYSpin();
			}	
		});	
		StartScreen.instance.t.add(StartScreen.instance.jVScreenYSpin);
		
		//Spinner Width
		VirtualScreenBean.vScreenSpinnerWidth = VirtualScreenBean.screenWidthMax;
		StartScreen.instance.vscreenWidthLabel = new JLabel();
		StartScreen.instance.vscreenWidthLabel.setText("SharingScreen Width:");
		StartScreen.instance.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		StartScreen.instance.t.add(StartScreen.instance.vscreenWidthLabel);
		
		StartScreen.instance.jVScreenWidthSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerWidth, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		StartScreen.instance.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		StartScreen.instance.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueWidthSpin();
			}	
		});	
		StartScreen.instance.t.add(StartScreen.instance.jVScreenWidthSpin);		
		
		//Spinner Height
		VirtualScreenBean.vScreenSpinnerHeight = VirtualScreenBean.screenHeightMax;
		StartScreen.instance.vscreenHeightLabel = new JLabel();
		StartScreen.instance.vscreenHeightLabel.setText("SharingScreen Height:");
		StartScreen.instance.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		StartScreen.instance.t.add(StartScreen.instance.vscreenHeightLabel);
		
		StartScreen.instance.jVScreenHeightSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerHeight, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		StartScreen.instance.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		StartScreen.instance.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueHeightSpin();
			}	
		});	
		StartScreen.instance.t.add(StartScreen.instance.jVScreenHeightSpin);	
		
	}	
	
	

	
	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = Integer.valueOf(StartScreen.instance.jVScreenXSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerWidth+newX > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING X "+VirtualScreenBean.vScreenSpinnerWidth+" "+newX);
				newX=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerWidth;
				StartScreen.instance.jVScreenXSpin.setValue(newX);
				if (this.showWarning) StartScreen.instance.showBandwidthWarning("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				VirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerX = Integer.valueOf(StartScreen.instance.jVScreenXSpin.getValue().toString()).intValue();
		}
	}	
	
	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = Integer.valueOf(StartScreen.instance.jVScreenYSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerHeight+newY > VirtualScreenBean.screenHeightMax){
				newY=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerHeight;
				StartScreen.instance.jVScreenYSpin.setValue(newY);
				if (this.showWarning) StartScreen.instance.showBandwidthWarning("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				VirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerY = Integer.valueOf(StartScreen.instance.jVScreenYSpin.getValue().toString()).intValue();
		}
	}	
	
	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = Integer.valueOf(StartScreen.instance.jVScreenWidthSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerX+newWidth > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING WIDTH");
				newWidth=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerX;
				StartScreen.instance.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)StartScreen.instance.showBandwidthWarning("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				VirtualScreenBean.vScreenSpinnerWidth = newWidth;
				updateVScreenBounds();	
			}
		} else {
			VirtualScreenBean.vScreenSpinnerWidth = Integer.valueOf(StartScreen.instance.jVScreenWidthSpin.getValue().toString()).intValue();
		}
		//System.out.println("calcNewValueWidthSpin "+VirtualScreenBean.vScreenSpinnerWidth);
	}	
	
	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = Integer.valueOf(StartScreen.instance.jVScreenHeightSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerY+newHeight > VirtualScreenBean.screenHeightMax){
				newHeight=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerY;
				StartScreen.instance.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)StartScreen.instance.showBandwidthWarning("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				VirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();	
			}
		} else {
			VirtualScreenBean.vScreenSpinnerHeight = Integer.valueOf(StartScreen.instance.jVScreenHeightSpin.getValue().toString()).intValue();
		}
		//System.out.println("calcNewValueHeightSpin "+VirtualScreenBean.vScreenSpinnerHeight);
	}	
	
	/**
	 * update the bounds of the vScreen
	 * by useing the vars from the Spinners
	 *
	 */
	void updateVScreenBounds(){

			double newvScreenWidth = VirtualScreenBean.vScreenSpinnerWidth * (new Double(VirtualScreenBean.vScreenWidth) / new Double(VirtualScreenBean.screenWidthMax));
			double newvScreenX = VirtualScreenBean.vScreenSpinnerX * (new Double(VirtualScreenBean.vScreenWidth) / new Double(VirtualScreenBean.screenWidthMax));
		
			double newvScreenHeight = VirtualScreenBean.vScreenSpinnerHeight * (new Double(VirtualScreenBean.vScreenHeight) / new Double(VirtualScreenBean.screenHeightMax));
			double newvScreenY = VirtualScreenBean.vScreenSpinnerY * (new Double(VirtualScreenBean.vScreenHeight) / new Double(VirtualScreenBean.screenHeightMax));
			
			//System.out.println("updateVScreenBounds "+newvScreenX+"||"+newvScreenWidth);
			
			StartScreen.instance.vScreenIconLeft.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30-16, 
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+(Long.valueOf(Math.round(newvScreenHeight)).intValue()/2));
			StartScreen.instance.vScreenIconRight.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+Long.valueOf(Math.round(newvScreenWidth)).intValue()-16, 
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+((Long.valueOf(Math.round(newvScreenHeight)).intValue())/2));
			
			StartScreen.instance.vScreenIconUp.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8);
	
			StartScreen.instance.vScreenIconDown.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8+(Long.valueOf(Math.round(newvScreenHeight)).intValue()));
	
			
			StartScreen.instance.virtualScreen.setBounds(
					30+Long.valueOf(Math.round(newvScreenX)).intValue(), 
							170+Long.valueOf(Math.round(newvScreenY)).intValue() ,  
									Long.valueOf(Math.round(newvScreenWidth)).intValue(), 
											Long.valueOf(Math.round(newvScreenHeight)).intValue() );
			
			StartScreen.instance.virtualScreen.setText(VirtualScreenBean.vScreenSpinnerWidth+":"+VirtualScreenBean.vScreenSpinnerHeight);
			
			//System.out.println(30+Long.valueOf(Math.round(newvScreenX)).intValue()+"|||"+  Long.valueOf(Math.round(newvScreenWidth)).intValue());
			
			//System.out.println(170+Long.valueOf(Math.round(newvScreenY)).intValue() +","+ Long.valueOf(Math.round(newvScreenHeight)).intValue() );

	}
	
}
