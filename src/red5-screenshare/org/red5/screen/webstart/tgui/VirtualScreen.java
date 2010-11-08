package org.red5.screen.webstart.tgui;

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
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

import org.red5.screen.webstart.BlankArea;
import org.red5.screen.webstart.ScreenShareRTMPT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualScreen {
	
	private static final Logger logger = LoggerFactory.getLogger( VirtualScreen.class );
	public static VirtualScreen instance = null;

	public boolean showWarning = true;

	public boolean doUpdateBounds = true;

	public VirtualScreen() throws Exception{
		instance = this;

		ScreenShareRTMPT.instance.tFieldScreenZoom = new JLabel();
		ScreenShareRTMPT.instance.tFieldScreenZoom.setBounds(10, 120, 200, 20);
		ScreenShareRTMPT.instance.tFieldScreenZoom.setText("Select your screen Area:");
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.tFieldScreenZoom);

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		VirtualScreenBean.screenratio = screenSize.getHeight()/screenSize.getWidth();
		VirtualScreenBean.screenWidthMax = Double.valueOf(screenSize.getWidth()).intValue();
		VirtualScreenBean.screenHeightMax = Double.valueOf(screenSize.getHeight()).intValue();

		VirtualScreenBean.vScreenHeight = Long.valueOf(Math.round( VirtualScreenBean.vScreenWidth*VirtualScreenBean.screenratio )).intValue();

		int width = VirtualScreenBean.vScreenWidth;
		int height = Long.valueOf(Math.round(width*VirtualScreenBean.screenratio )).intValue();

		ScreenShareRTMPT.instance.vScreenIconLeft = new JLabel();
		ScreenShareRTMPT.instance.vScreenIconLeft.setBounds(14, 162+(height/2), 32, 16);

		ScreenShareRTMPT.instance.vScreenIconRight = new JLabel();
		ScreenShareRTMPT.instance.vScreenIconRight.setBounds(30+width-16, 162+(height/2), 32, 16);

		ScreenShareRTMPT.instance.vScreenIconUp = new JLabel();
		ScreenShareRTMPT.instance.vScreenIconUp.setBounds(30+(width/2)-8, 162-8, 16, 32);

		ScreenShareRTMPT.instance.vScreenIconDown = new JLabel();
		ScreenShareRTMPT.instance.vScreenIconDown.setBounds(30+(width/2)-8, 162+height-8, 16, 32);

		Image im_left = ImageIO.read(ScreenShareRTMPT.class.getResource("/1leftarrow.png"));
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(ScreenShareRTMPT.class.getResource("/1rightarrow.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(ScreenShareRTMPT.class.getResource("/1uparrow.png"));
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(ScreenShareRTMPT.class.getResource("/1downarrow.png"));
		ImageIcon iIcon4 = new ImageIcon(im_down);

		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);
		ScreenShareRTMPT.instance.vScreenIconLeft.add(jLab1);
		ScreenShareRTMPT.instance.vScreenIconLeft.add(jLab2);
		ScreenShareRTMPT.instance.vScreenIconLeft.setToolTipText("Change width");
		VirtualScreenXMouseListener xLeftMouseListener = new VirtualScreenXMouseListener();
		ScreenShareRTMPT.instance.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		ScreenShareRTMPT.instance.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vScreenIconLeft);

		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);
		ScreenShareRTMPT.instance.vScreenIconRight.add(jLab3);
		ScreenShareRTMPT.instance.vScreenIconRight.add(jLab4);
		ScreenShareRTMPT.instance.vScreenIconRight.setToolTipText("Change width");
		VirtualScreenWidthMouseListener widthMouseListener = new VirtualScreenWidthMouseListener();
		ScreenShareRTMPT.instance.vScreenIconRight.addMouseListener(widthMouseListener);
		ScreenShareRTMPT.instance.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vScreenIconRight);

		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);
		ScreenShareRTMPT.instance.vScreenIconUp.add(jLab5);
		ScreenShareRTMPT.instance.vScreenIconUp.add(jLab6);
		ScreenShareRTMPT.instance.vScreenIconUp.setToolTipText("Change height");
		VirtualScreenYMouseListener yMouseListener = new VirtualScreenYMouseListener();
		ScreenShareRTMPT.instance.vScreenIconUp.addMouseListener(yMouseListener);
		ScreenShareRTMPT.instance.vScreenIconUp.addMouseMotionListener(yMouseListener);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vScreenIconUp);

		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);
		ScreenShareRTMPT.instance.vScreenIconDown.add(jLab7);
		ScreenShareRTMPT.instance.vScreenIconDown.add(jLab8);
		ScreenShareRTMPT.instance.vScreenIconDown.setToolTipText("Change height");
		VirtualScreenHeightMouseListener heightMouseListener = new VirtualScreenHeightMouseListener();
		ScreenShareRTMPT.instance.vScreenIconDown.addMouseListener(heightMouseListener);
		ScreenShareRTMPT.instance.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vScreenIconDown);


		ScreenShareRTMPT.instance.virtualScreen = new BlankArea(new Color(255,255,255,100));
		ScreenShareRTMPT.instance.virtualScreen.setOpaque(true);
		ScreenShareRTMPT.instance.virtualScreen.setHorizontalAlignment(SwingConstants.LEFT);
		ScreenShareRTMPT.instance.virtualScreen.setVerticalAlignment(SwingConstants.TOP);
		ScreenShareRTMPT.instance.virtualScreen.setText(VirtualScreenBean.screenWidthMax+":"+VirtualScreenBean.screenHeightMax);
		ScreenShareRTMPT.instance.virtualScreen.setBounds(30, 170, VirtualScreenBean.vScreenWidth, VirtualScreenBean.vScreenHeight);
		VirtualScreenMouseListener vListener = new VirtualScreenMouseListener();
		ScreenShareRTMPT.instance.virtualScreen.addMouseListener(vListener);
		ScreenShareRTMPT.instance.virtualScreen.addMouseMotionListener(vListener);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.virtualScreen);

		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);

		Image img = imageScreen.getScaledInstance(width, height ,Image.SCALE_SMOOTH);
		//imageScreen.
		logger.debug("img"+img);
		ImageIcon image = new ImageIcon(img);

		ScreenShareRTMPT.instance.blankArea = new JLabel(image);
		ScreenShareRTMPT.instance.blankArea.setBounds(30, 170, width, height);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.blankArea);

		//Spinner X
		VirtualScreenBean.vScreenSpinnerX = 0;
		ScreenShareRTMPT.instance.vscreenXLabel = new JLabel();
		ScreenShareRTMPT.instance.vscreenXLabel.setText("SharingScreen X:");
		ScreenShareRTMPT.instance.vscreenXLabel.setBounds(250, 170, 150, 24);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vscreenXLabel);

		ScreenShareRTMPT.instance.jVScreenXSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerX, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		ScreenShareRTMPT.instance.jVScreenXSpin.setBounds(400, 170, 60, 24);
		ScreenShareRTMPT.instance.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueXSpin();
			}
		});
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.jVScreenXSpin);

		//Spinner Y
		VirtualScreenBean.vScreenSpinnerY = 0;
		ScreenShareRTMPT.instance.vscreenYLabel = new JLabel();
		ScreenShareRTMPT.instance.vscreenYLabel.setText("SharingScreen Y:");
		ScreenShareRTMPT.instance.vscreenYLabel.setBounds(250, 200, 150, 24);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vscreenYLabel);

		ScreenShareRTMPT.instance.jVScreenYSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerY, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		ScreenShareRTMPT.instance.jVScreenYSpin.setBounds(400, 200, 60, 24);
		ScreenShareRTMPT.instance.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueYSpin();
			}
		});
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.jVScreenYSpin);

		//Spinner Width
		VirtualScreenBean.vScreenSpinnerWidth = VirtualScreenBean.screenWidthMax;
		logger.debug("Modify 111 "+VirtualScreenBean.vScreenSpinnerWidth);
		ScreenShareRTMPT.instance.vscreenWidthLabel = new JLabel();
		ScreenShareRTMPT.instance.vscreenWidthLabel.setText("SharingScreen Width:");
		ScreenShareRTMPT.instance.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vscreenWidthLabel);

		ScreenShareRTMPT.instance.jVScreenWidthSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerWidth, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		ScreenShareRTMPT.instance.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		ScreenShareRTMPT.instance.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueWidthSpin();
			}
		});
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.jVScreenWidthSpin);

		//Spinner Height
		VirtualScreenBean.vScreenSpinnerHeight = VirtualScreenBean.screenHeightMax;
		ScreenShareRTMPT.instance.vscreenHeightLabel = new JLabel();
		ScreenShareRTMPT.instance.vscreenHeightLabel.setText("SharingScreen Height:");
		ScreenShareRTMPT.instance.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vscreenHeightLabel);

		ScreenShareRTMPT.instance.jVScreenHeightSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerHeight, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		ScreenShareRTMPT.instance.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		ScreenShareRTMPT.instance.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueHeightSpin();
			}
		});
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.jVScreenHeightSpin);
		
		
		//String[] selectResize = { ScreenShareRTMPT.instance.label1090,ScreenShareRTMPT.instance.label1091,ScreenShareRTMPT.instance.label1092,ScreenShareRTMPT.instance.label1093 };
		String[] selectResize = { ScreenShareRTMPT.instance.label1091,ScreenShareRTMPT.instance.label1092,ScreenShareRTMPT.instance.label1093 };
		VirtualScreenBean.vScreenResizeX = 640;
		VirtualScreenBean.vScreenResizeY = 400;
		
		ScreenShareRTMPT.instance.vscreenResizeLabel = new JLabel();
		ScreenShareRTMPT.instance.vscreenResizeLabel.setText(ScreenShareRTMPT.instance.label1089);
		ScreenShareRTMPT.instance.vscreenResizeLabel.setBounds(250, 300, 200,24 );
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.vscreenResizeLabel);
		
		JComboBox comboResize  = new JComboBox(selectResize);	
		comboResize.setBounds(250, 330, 200, 24);
		comboResize.setSelectedIndex(ScreenShareRTMPT.instance.defaultQualityScreensharing);
		comboResize.addActionListener(new GetResizeChoice()); 
		
		ScreenShareRTMPT.instance.jVScreenResizeMode = comboResize;
		ScreenShareRTMPT.instance.t.add(ScreenShareRTMPT.instance.jVScreenResizeMode);
		
	}
	class GetResizeChoice implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			
			JComboBox cb = (JComboBox)e.getSource();
	        String petName = (String)cb.getSelectedItem();
	        
	        VirtualScreenBean.vScreenScaleFactor = petName;
	        
	        calcRescaleFactors();
	        
		}
		
//		public void actionPerformed (ActionEvent e)
//		{
//			
//			JComboBox cb = (JComboBox)e.getSource();
//	        String petName = (String)cb.getSelectedItem();
//	        if(petName == "High Quality")
//	        {
//	        	logger.debug("resize: X:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()+
//	        						" Y:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue());
//	        	
//	        	VirtualScreenBean.vScreenResizeX = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue();
//	        	VirtualScreenBean.vScreenResizeY = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue();
//	        	updateVScreenBounds();
//	        }
//	        else if(petName == "Medium Quality")
//	        {
//	        	logger.debug("resize: X:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/2+
//	        						" Y:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/2);
//	        	
//	        	VirtualScreenBean.vScreenResizeX = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue())/2;
//	        	VirtualScreenBean.vScreenResizeY = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue())/2;
//	        	updateVScreenBounds();
//	        }
//	        else if(petName == "Low Quality")
//	        {
//	        	logger.debug("resize: X:"+(Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/8)*3+
//	        						" Y:"+(Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/8)*3);
//	        	
//	        	VirtualScreenBean.vScreenResizeX = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/8)*3;
//	        	VirtualScreenBean.vScreenResizeY = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/8)*3;
//	        	updateVScreenBounds();
//	        }
//		}
	}
	
	/**
	 * Needs to be always invoked after every re-scaling
	 */
	void calcRescaleFactors() {
		
		logger.debug("calcRescaleFactors -- ");
		
		if(VirtualScreenBean.vScreenScaleFactor.equals(ScreenShareRTMPT.instance.label1090))
        {
        	logger.debug("resize: X:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()+
        						" Y:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue());
        	
        	VirtualScreenBean.vScreenResizeX = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue();
        	VirtualScreenBean.vScreenResizeY = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue();
        	updateVScreenBounds();
        }
        else if(VirtualScreenBean.vScreenScaleFactor.equals(ScreenShareRTMPT.instance.label1091))
        {
        	logger.debug("resize: X:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()+
        						" Y:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue());
        	
        	VirtualScreenBean.vScreenResizeX = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue();
        	VirtualScreenBean.vScreenResizeY = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue();
        	updateVScreenBounds();
        }
        else if(VirtualScreenBean.vScreenScaleFactor.equals(ScreenShareRTMPT.instance.label1092))
        {
        	logger.debug("resize: X:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/2+
        						" Y:"+Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/2);
        	
        	VirtualScreenBean.vScreenResizeX = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue())/2;
        	VirtualScreenBean.vScreenResizeY = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue())/2;
        	updateVScreenBounds();
        }
        else if(VirtualScreenBean.vScreenScaleFactor.equals(ScreenShareRTMPT.instance.label1093))
        {
        	logger.debug("resize: X:"+(Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/8)*3+
        						" Y:"+(Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/8)*3);
        	
        	VirtualScreenBean.vScreenResizeX = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue()/8)*3;
        	VirtualScreenBean.vScreenResizeY = (Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue()/8)*3;
        	updateVScreenBounds();
        }
		
		 logger.debug("########## calcRescaleFactors vScreenResizeX " + VirtualScreenBean.vScreenResizeX);
         logger.debug("########## calcRescaleFactors vScreenResizeY " + VirtualScreenBean.vScreenResizeY);
         logger.debug( "setup capture thread getCanonicalName "+VirtualScreenBean.class.getCanonicalName());
		 logger.debug( "setup capture thread getName "+VirtualScreenBean.class.getName());
         logger.debug( "setup capture thread vScreenSpinnerWidth "+VirtualScreenBean.vScreenSpinnerWidth);
		 logger.debug( "setup capture thread vScreenSpinnerHeight "+VirtualScreenBean.vScreenSpinnerHeight);

	}

	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenXSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerWidth+newX > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING X "+VirtualScreenBean.vScreenSpinnerWidth+" "+newX);
				newX=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerWidth;
				ScreenShareRTMPT.instance.jVScreenXSpin.setValue(newX);
				if (this.showWarning) ScreenShareRTMPT.instance.showBandwidthWarning("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				VirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerX = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenXSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenYSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerHeight+newY > VirtualScreenBean.screenHeightMax){
				newY=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerHeight;
				ScreenShareRTMPT.instance.jVScreenYSpin.setValue(newY);
				if (this.showWarning) ScreenShareRTMPT.instance.showBandwidthWarning("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				VirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerY = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenYSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerX+newWidth > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING WIDTH");
				newWidth=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerX;
				ScreenShareRTMPT.instance.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)ScreenShareRTMPT.instance.showBandwidthWarning("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				VirtualScreenBean.vScreenSpinnerWidth = newWidth;
				logger.debug("Modify 222 "+VirtualScreenBean.vScreenSpinnerWidth);
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerWidth = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenWidthSpin.getValue().toString()).intValue();
			logger.debug("Modify 333 "+VirtualScreenBean.vScreenSpinnerWidth);
		}
		
		calcRescaleFactors();
		//System.out.println("calcNewValueWidthSpin "+VirtualScreenBean.vScreenSpinnerWidth);
	}

	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerY+newHeight > VirtualScreenBean.screenHeightMax){
				newHeight=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerY;
				ScreenShareRTMPT.instance.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)ScreenShareRTMPT.instance.showBandwidthWarning("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				VirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerHeight = Integer.valueOf(ScreenShareRTMPT.instance.jVScreenHeightSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
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

			ScreenShareRTMPT.instance.vScreenIconLeft.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+(Long.valueOf(Math.round(newvScreenHeight)).intValue()/2));
			
			ScreenShareRTMPT.instance.vScreenIconRight.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+Long.valueOf(Math.round(newvScreenWidth)).intValue()-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+((Long.valueOf(Math.round(newvScreenHeight)).intValue())/2));

			ScreenShareRTMPT.instance.vScreenIconUp.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8);

			ScreenShareRTMPT.instance.vScreenIconDown.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8+(Long.valueOf(Math.round(newvScreenHeight)).intValue()));


			ScreenShareRTMPT.instance.virtualScreen.setBounds(
					30+Long.valueOf(Math.round(newvScreenX)).intValue(),
							170+Long.valueOf(Math.round(newvScreenY)).intValue() ,
									Long.valueOf(Math.round(newvScreenWidth)).intValue(),
											Long.valueOf(Math.round(newvScreenHeight)).intValue() );

			ScreenShareRTMPT.instance.virtualScreen.setText(VirtualScreenBean.vScreenSpinnerWidth+":"+VirtualScreenBean.vScreenSpinnerHeight);

			//System.out.println(30+Long.valueOf(Math.round(newvScreenX)).intValue()+"|||"+  Long.valueOf(Math.round(newvScreenWidth)).intValue());

			//System.out.println(170+Long.valueOf(Math.round(newvScreenY)).intValue() +","+ Long.valueOf(Math.round(newvScreenHeight)).intValue() );

	}

}
