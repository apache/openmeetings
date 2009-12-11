package org.red5.screen.webstart.gui;

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

import org.red5.screen.webstart.BlankArea;
import org.red5.screen.webstart.ScreenShare;


public class VirtualScreen {

	public static VirtualScreen instance = null;

	public boolean showWarning = true;

	public boolean doUpdateBounds = true;

	public VirtualScreen() throws Exception{
		instance = this;

		ScreenShare.instance.tFieldScreenZoom = new JLabel();
		ScreenShare.instance.tFieldScreenZoom.setBounds(10, 120, 200, 20);
		ScreenShare.instance.tFieldScreenZoom.setText("Select your screen Area:");
		ScreenShare.instance.t.add(ScreenShare.instance.tFieldScreenZoom);

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		VirtualScreenBean.screenratio = screenSize.getHeight()/screenSize.getWidth();
		VirtualScreenBean.screenWidthMax = Double.valueOf(screenSize.getWidth()).intValue();
		VirtualScreenBean.screenHeightMax = Double.valueOf(screenSize.getHeight()).intValue();

		VirtualScreenBean.vScreenHeight = Long.valueOf(Math.round( VirtualScreenBean.vScreenWidth*VirtualScreenBean.screenratio )).intValue();

		int width = VirtualScreenBean.vScreenWidth;
		int height = Long.valueOf(Math.round(width*VirtualScreenBean.screenratio )).intValue();

		ScreenShare.instance.vScreenIconLeft = new JLabel();
		ScreenShare.instance.vScreenIconLeft.setBounds(14, 162+(height/2), 32, 16);

		ScreenShare.instance.vScreenIconRight = new JLabel();
		ScreenShare.instance.vScreenIconRight.setBounds(30+width-16, 162+(height/2), 32, 16);

		ScreenShare.instance.vScreenIconUp = new JLabel();
		ScreenShare.instance.vScreenIconUp.setBounds(30+(width/2)-8, 162-8, 16, 32);

		ScreenShare.instance.vScreenIconDown = new JLabel();
		ScreenShare.instance.vScreenIconDown.setBounds(30+(width/2)-8, 162+height-8, 16, 32);

		Image im_left = ImageIO.read(ScreenShare.class.getResource("/1leftarrow.png"));
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(ScreenShare.class.getResource("/1rightarrow.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(ScreenShare.class.getResource("/1uparrow.png"));
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(ScreenShare.class.getResource("/1downarrow.png"));
		ImageIcon iIcon4 = new ImageIcon(im_down);

		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);
		ScreenShare.instance.vScreenIconLeft.add(jLab1);
		ScreenShare.instance.vScreenIconLeft.add(jLab2);
		ScreenShare.instance.vScreenIconLeft.setToolTipText("Change width");
		VirtualScreenXMouseListener xLeftMouseListener = new VirtualScreenXMouseListener();
		ScreenShare.instance.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		ScreenShare.instance.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		ScreenShare.instance.t.add(ScreenShare.instance.vScreenIconLeft);

		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);
		ScreenShare.instance.vScreenIconRight.add(jLab3);
		ScreenShare.instance.vScreenIconRight.add(jLab4);
		ScreenShare.instance.vScreenIconRight.setToolTipText("Change width");
		VirtualScreenWidthMouseListener widthMouseListener = new VirtualScreenWidthMouseListener();
		ScreenShare.instance.vScreenIconRight.addMouseListener(widthMouseListener);
		ScreenShare.instance.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		ScreenShare.instance.t.add(ScreenShare.instance.vScreenIconRight);

		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);
		ScreenShare.instance.vScreenIconUp.add(jLab5);
		ScreenShare.instance.vScreenIconUp.add(jLab6);
		ScreenShare.instance.vScreenIconUp.setToolTipText("Change height");
		VirtualScreenYMouseListener yMouseListener = new VirtualScreenYMouseListener();
		ScreenShare.instance.vScreenIconUp.addMouseListener(yMouseListener);
		ScreenShare.instance.vScreenIconUp.addMouseMotionListener(yMouseListener);
		ScreenShare.instance.t.add(ScreenShare.instance.vScreenIconUp);

		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);
		ScreenShare.instance.vScreenIconDown.add(jLab7);
		ScreenShare.instance.vScreenIconDown.add(jLab8);
		ScreenShare.instance.vScreenIconDown.setToolTipText("Change height");
		VirtualScreenHeightMouseListener heightMouseListener = new VirtualScreenHeightMouseListener();
		ScreenShare.instance.vScreenIconDown.addMouseListener(heightMouseListener);
		ScreenShare.instance.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		ScreenShare.instance.t.add(ScreenShare.instance.vScreenIconDown);


		ScreenShare.instance.virtualScreen = new BlankArea(new Color(255,255,255,100));
		ScreenShare.instance.virtualScreen.setOpaque(true);
		ScreenShare.instance.virtualScreen.setHorizontalAlignment(SwingConstants.LEFT);
		ScreenShare.instance.virtualScreen.setVerticalAlignment(SwingConstants.TOP);
		ScreenShare.instance.virtualScreen.setText(VirtualScreenBean.screenWidthMax+":"+VirtualScreenBean.screenHeightMax);
		ScreenShare.instance.virtualScreen.setBounds(30, 170, VirtualScreenBean.vScreenWidth, VirtualScreenBean.vScreenHeight);
		VirtualScreenMouseListener vListener = new VirtualScreenMouseListener();
		ScreenShare.instance.virtualScreen.addMouseListener(vListener);
		ScreenShare.instance.virtualScreen.addMouseMotionListener(vListener);
		ScreenShare.instance.t.add(ScreenShare.instance.virtualScreen);

		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);

		Image img = imageScreen.getScaledInstance(width, height ,Image.SCALE_SMOOTH);
		//imageScreen.
		System.out.println("img"+img);
		ImageIcon image = new ImageIcon(img);

		ScreenShare.instance.blankArea = new JLabel(image);
		ScreenShare.instance.blankArea.setBounds(30, 170, width, height);
		ScreenShare.instance.t.add(ScreenShare.instance.blankArea);

		//Spinner X
		VirtualScreenBean.vScreenSpinnerX = 0;
		ScreenShare.instance.vscreenXLabel = new JLabel();
		ScreenShare.instance.vscreenXLabel.setText("SharingScreen X:");
		ScreenShare.instance.vscreenXLabel.setBounds(250, 170, 150, 24);
		ScreenShare.instance.t.add(ScreenShare.instance.vscreenXLabel);

		ScreenShare.instance.jVScreenXSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerX, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		ScreenShare.instance.jVScreenXSpin.setBounds(400, 170, 60, 24);
		ScreenShare.instance.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueXSpin();
			}
		});
		ScreenShare.instance.t.add(ScreenShare.instance.jVScreenXSpin);

		//Spinner Y
		VirtualScreenBean.vScreenSpinnerY = 0;
		ScreenShare.instance.vscreenYLabel = new JLabel();
		ScreenShare.instance.vscreenYLabel.setText("SharingScreen Y:");
		ScreenShare.instance.vscreenYLabel.setBounds(250, 200, 150, 24);
		ScreenShare.instance.t.add(ScreenShare.instance.vscreenYLabel);

		ScreenShare.instance.jVScreenYSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerY, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		ScreenShare.instance.jVScreenYSpin.setBounds(400, 200, 60, 24);
		ScreenShare.instance.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueYSpin();
			}
		});
		ScreenShare.instance.t.add(ScreenShare.instance.jVScreenYSpin);

		//Spinner Width
		VirtualScreenBean.vScreenSpinnerWidth = VirtualScreenBean.screenWidthMax;
		ScreenShare.instance.vscreenWidthLabel = new JLabel();
		ScreenShare.instance.vscreenWidthLabel.setText("SharingScreen Width:");
		ScreenShare.instance.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		ScreenShare.instance.t.add(ScreenShare.instance.vscreenWidthLabel);

		ScreenShare.instance.jVScreenWidthSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerWidth, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		ScreenShare.instance.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		ScreenShare.instance.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueWidthSpin();
			}
		});
		ScreenShare.instance.t.add(ScreenShare.instance.jVScreenWidthSpin);

		//Spinner Height
		VirtualScreenBean.vScreenSpinnerHeight = VirtualScreenBean.screenHeightMax;
		ScreenShare.instance.vscreenHeightLabel = new JLabel();
		ScreenShare.instance.vscreenHeightLabel.setText("SharingScreen Height:");
		ScreenShare.instance.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		ScreenShare.instance.t.add(ScreenShare.instance.vscreenHeightLabel);

		ScreenShare.instance.jVScreenHeightSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerHeight, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		ScreenShare.instance.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		ScreenShare.instance.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				calcNewValueHeightSpin();
			}
		});
		ScreenShare.instance.t.add(ScreenShare.instance.jVScreenHeightSpin);

	}




	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = Integer.valueOf(ScreenShare.instance.jVScreenXSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerWidth+newX > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING X "+VirtualScreenBean.vScreenSpinnerWidth+" "+newX);
				newX=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerWidth;
				ScreenShare.instance.jVScreenXSpin.setValue(newX);
				if (this.showWarning) ScreenShare.instance.showBandwidthWarning("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				VirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerX = Integer.valueOf(ScreenShare.instance.jVScreenXSpin.getValue().toString()).intValue();
		}
	}

	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = Integer.valueOf(ScreenShare.instance.jVScreenYSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerHeight+newY > VirtualScreenBean.screenHeightMax){
				newY=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerHeight;
				ScreenShare.instance.jVScreenYSpin.setValue(newY);
				if (this.showWarning) ScreenShare.instance.showBandwidthWarning("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				VirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerY = Integer.valueOf(ScreenShare.instance.jVScreenYSpin.getValue().toString()).intValue();
		}
	}

	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = Integer.valueOf(ScreenShare.instance.jVScreenWidthSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerX+newWidth > VirtualScreenBean.screenWidthMax){
//				System.out.println("WARNING WIDTH");
				newWidth=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerX;
				ScreenShare.instance.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)ScreenShare.instance.showBandwidthWarning("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				VirtualScreenBean.vScreenSpinnerWidth = newWidth;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerWidth = Integer.valueOf(ScreenShare.instance.jVScreenWidthSpin.getValue().toString()).intValue();
		}
		//System.out.println("calcNewValueWidthSpin "+VirtualScreenBean.vScreenSpinnerWidth);
	}

	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = Integer.valueOf(ScreenShare.instance.jVScreenHeightSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerY+newHeight > VirtualScreenBean.screenHeightMax){
				newHeight=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerY;
				ScreenShare.instance.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)ScreenShare.instance.showBandwidthWarning("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				VirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerHeight = Integer.valueOf(ScreenShare.instance.jVScreenHeightSpin.getValue().toString()).intValue();
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

			ScreenShare.instance.vScreenIconLeft.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+(Long.valueOf(Math.round(newvScreenHeight)).intValue()/2));
			ScreenShare.instance.vScreenIconRight.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+Long.valueOf(Math.round(newvScreenWidth)).intValue()-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+((Long.valueOf(Math.round(newvScreenHeight)).intValue())/2));

			ScreenShare.instance.vScreenIconUp.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8);

			ScreenShare.instance.vScreenIconDown.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8+(Long.valueOf(Math.round(newvScreenHeight)).intValue()));


			ScreenShare.instance.virtualScreen.setBounds(
					30+Long.valueOf(Math.round(newvScreenX)).intValue(),
							170+Long.valueOf(Math.round(newvScreenY)).intValue() ,
									Long.valueOf(Math.round(newvScreenWidth)).intValue(),
											Long.valueOf(Math.round(newvScreenHeight)).intValue() );

			ScreenShare.instance.virtualScreen.setText(VirtualScreenBean.vScreenSpinnerWidth+":"+VirtualScreenBean.vScreenSpinnerHeight);

			//System.out.println(30+Long.valueOf(Math.round(newvScreenX)).intValue()+"|||"+  Long.valueOf(Math.round(newvScreenWidth)).intValue());

			//System.out.println(170+Long.valueOf(Math.round(newvScreenY)).intValue() +","+ Long.valueOf(Math.round(newvScreenHeight)).intValue() );

	}

}
