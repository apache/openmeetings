/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.red5.screen.webstart.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.red5.screen.webstart.BlankArea;
import org.red5.screen.webstart.CommonScreenShare;
import org.red5.screen.webstart.ScreenShare;
import org.red5.screen.webstart.gui.VirtualScreenBean.ScreenQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualScreen {
	
	private static final Logger logger = LoggerFactory.getLogger( VirtualScreen.class );
	CommonScreenShare css;
	public boolean showWarning = true;

	public boolean doUpdateBounds = true;
	
	private class KeyValue<T> {
		private String key;
		private T value;
		
		public KeyValue(String key, T value) {
			this.key = key;
			this.value = value;
		}
	 
		@SuppressWarnings("unused")
		public String getKey() { return key; }
		public T getValue() { return value; }
	 
		@Override
		public String toString() { return key; }
	 
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof KeyValue) {
				@SuppressWarnings("unchecked")
				KeyValue<T> kv = (KeyValue<T>) obj;
				return (kv.value.equals(this.value));
			}
			return false;
		}
	 
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
			return hash;
		}
	}
	
	public VirtualScreen(CommonScreenShare css) throws Exception {
		this.css = css;

		css.tFieldScreenZoom = new JLabel();
		css.tFieldScreenZoom.setBounds(10, 120, 200, 20);
		css.tFieldScreenZoom.setText("Select your screen Area:");
		css.t.add(css.tFieldScreenZoom);

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		VirtualScreenBean.screenratio = screenSize.getHeight()/screenSize.getWidth();
		VirtualScreenBean.screenWidthMax = Double.valueOf(screenSize.getWidth()).intValue();
		VirtualScreenBean.screenHeightMax = Double.valueOf(screenSize.getHeight()).intValue();

		VirtualScreenBean.vScreenHeight = Long.valueOf(Math.round( VirtualScreenBean.vScreenWidth*VirtualScreenBean.screenratio )).intValue();

		int width = VirtualScreenBean.vScreenWidth;
		int height = Long.valueOf(Math.round(width*VirtualScreenBean.screenratio )).intValue();

		css.vScreenIconLeft = new JLabel();
		css.vScreenIconLeft.setBounds(14, 162+(height/2), 32, 16);

		css.vScreenIconRight = new JLabel();
		css.vScreenIconRight.setBounds(30+width-16, 162+(height/2), 32, 16);

		css.vScreenIconUp = new JLabel();
		css.vScreenIconUp.setBounds(30+(width/2)-8, 162-8, 16, 32);

		css.vScreenIconDown = new JLabel();
		css.vScreenIconDown.setBounds(30+(width/2)-8, 162+height-8, 16, 32);

		Image im_left = ImageIO.read(ScreenShare.class.getResource("/org/red5/screen/1leftarrow.png"));
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(ScreenShare.class.getResource("/org/red5/screen/1rightarrow.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(ScreenShare.class.getResource("/org/red5/screen/1uparrow.png"));
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(ScreenShare.class.getResource("/org/red5/screen/1downarrow.png"));
		ImageIcon iIcon4 = new ImageIcon(im_down);

		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);
		css.vScreenIconLeft.add(jLab1);
		css.vScreenIconLeft.add(jLab2);
		css.vScreenIconLeft.setToolTipText("Change width");
		VirtualScreenXMouseListener xLeftMouseListener = new VirtualScreenXMouseListener(this);
		css.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		css.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		css.t.add(css.vScreenIconLeft);

		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);
		css.vScreenIconRight.add(jLab3);
		css.vScreenIconRight.add(jLab4);
		css.vScreenIconRight.setToolTipText("Change width");
		VirtualScreenWidthMouseListener widthMouseListener = new VirtualScreenWidthMouseListener(this);
		css.vScreenIconRight.addMouseListener(widthMouseListener);
		css.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		css.t.add(css.vScreenIconRight);

		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);
		css.vScreenIconUp.add(jLab5);
		css.vScreenIconUp.add(jLab6);
		css.vScreenIconUp.setToolTipText("Change height");
		VirtualScreenYMouseListener yMouseListener = new VirtualScreenYMouseListener(this);
		css.vScreenIconUp.addMouseListener(yMouseListener);
		css.vScreenIconUp.addMouseMotionListener(yMouseListener);
		css.t.add(css.vScreenIconUp);

		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);
		css.vScreenIconDown.add(jLab7);
		css.vScreenIconDown.add(jLab8);
		css.vScreenIconDown.setToolTipText("Change height");
		VirtualScreenHeightMouseListener heightMouseListener = new VirtualScreenHeightMouseListener(this);
		css.vScreenIconDown.addMouseListener(heightMouseListener);
		css.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		css.t.add(css.vScreenIconDown);


		css.virtualScreen = new BlankArea(new Color(255,255,255,100));
		css.virtualScreen.setOpaque(true);
		css.virtualScreen.setHorizontalAlignment(SwingConstants.LEFT);
		css.virtualScreen.setVerticalAlignment(SwingConstants.TOP);
		css.virtualScreen.setText(VirtualScreenBean.screenWidthMax+":"+VirtualScreenBean.screenHeightMax);
		css.virtualScreen.setBounds(30, 170, VirtualScreenBean.vScreenWidth, VirtualScreenBean.vScreenHeight);
		VirtualScreenMouseListener vListener = new VirtualScreenMouseListener(this);
		css.virtualScreen.addMouseListener(vListener);
		css.virtualScreen.addMouseMotionListener(vListener);
		css.t.add(css.virtualScreen);

		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);

		Image img = imageScreen.getScaledInstance(width, height ,Image.SCALE_SMOOTH);
		//imageScreen.
		System.out.println("img"+img);
		ImageIcon image = new ImageIcon(img);

		css.blankArea = new JLabel(image);
		css.blankArea.setBounds(30, 170, width, height);
		css.t.add(css.blankArea);

		//Spinner X
		VirtualScreenBean.vScreenSpinnerX = 0;
		css.vscreenXLabel = new JLabel();
		css.vscreenXLabel.setText("SharingScreen X:");
		css.vscreenXLabel.setBounds(250, 170, 150, 24);
		css.t.add(css.vscreenXLabel);

		css.jVScreenXSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerX, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		css.jVScreenXSpin.setBounds(400, 170, 60, 24);
		css.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueXSpin();
			}
		});
		css.t.add(css.jVScreenXSpin);

		//Spinner Y
		VirtualScreenBean.vScreenSpinnerY = 0;
		css.vscreenYLabel = new JLabel();
		css.vscreenYLabel.setText("SharingScreen Y:");
		css.vscreenYLabel.setBounds(250, 200, 150, 24);
		css.t.add(css.vscreenYLabel);

		css.jVScreenYSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerY, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		css.jVScreenYSpin.setBounds(400, 200, 60, 24);
		css.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueYSpin();
			}
		});
		css.t.add(css.jVScreenYSpin);

		//Spinner Width
		VirtualScreenBean.vScreenSpinnerWidth = VirtualScreenBean.screenWidthMax;
		css.vscreenWidthLabel = new JLabel();
		css.vscreenWidthLabel.setText("SharingScreen Width:");
		css.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		css.t.add(css.vscreenWidthLabel);

		css.jVScreenWidthSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerWidth, 0, VirtualScreenBean.screenWidthMax, 1)
				);
		css.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		css.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueWidthSpin();
			}
		});
		css.t.add(css.jVScreenWidthSpin);

		//Spinner Height
		VirtualScreenBean.vScreenSpinnerHeight = VirtualScreenBean.screenHeightMax;
		css.vscreenHeightLabel = new JLabel();
		css.vscreenHeightLabel.setText("SharingScreen Height:");
		css.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		css.t.add(css.vscreenHeightLabel);

		css.jVScreenHeightSpin = new JSpinner(
					new SpinnerNumberModel(VirtualScreenBean.vScreenSpinnerHeight, 0, VirtualScreenBean.screenHeightMax, 1)
				);
		css.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		css.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueHeightSpin();
			}
		});
		css.t.add(css.jVScreenHeightSpin);
		
		
		VirtualScreenBean.vScreenResizeX = 640;
		VirtualScreenBean.vScreenResizeY = 400;
		
		css.vscreenResizeLabel = new JLabel();
		css.vscreenResizeLabel.setText(css.label1089);
		css.vscreenResizeLabel.setBounds(250, 300, 200,24 );
		css.t.add(css.vscreenResizeLabel);
		
		JComboBox comboResize  = new JComboBox();
		comboResize.addItem(new KeyValue<ScreenQuality>(css.label1090, ScreenQuality.VeryHigh));
		comboResize.addItem(new KeyValue<ScreenQuality>(css.label1091, ScreenQuality.High));
		comboResize.addItem(new KeyValue<ScreenQuality>(css.label1092, ScreenQuality.Medium));
		comboResize.addItem(new KeyValue<ScreenQuality>(css.label1093, ScreenQuality.Low));
		comboResize.setBounds(250, 330, 200, 24);
		comboResize.addActionListener(new ActionListener(){
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        VirtualScreenBean.screenQuality = ((KeyValue<ScreenQuality>)cb.getSelectedItem()).getValue();
		        calcRescaleFactors();
			}
		}); 
		comboResize.setSelectedIndex(css.defaultQualityScreensharing);
		
		css.jVScreenResizeMode = comboResize;
		css.t.add(css.jVScreenResizeMode);
		
	}
	
	/**
	 * Needs to be always invoked after every re-scaling
	 */
	void calcRescaleFactors() {
		logger.debug("calcRescaleFactors -- ");
		VirtualScreenBean.vScreenResizeX = Integer.valueOf(css.jVScreenWidthSpin.getValue().toString()).intValue();
		VirtualScreenBean.vScreenResizeY = Integer.valueOf(css.jVScreenHeightSpin.getValue().toString()).intValue();
		switch (VirtualScreenBean.screenQuality) {
			case VeryHigh:
			case High:
				break;
			case Medium:
				VirtualScreenBean.vScreenResizeX *= 1/2;
				VirtualScreenBean.vScreenResizeY *= 2;
				break;
			case Low:
				VirtualScreenBean.vScreenResizeX *= 3/8;
				VirtualScreenBean.vScreenResizeY *= 3/8;
				break;
		}
		logger.debug("resize: X:" + VirtualScreenBean.vScreenResizeX + " Y: " + VirtualScreenBean.vScreenResizeY);
		updateVScreenBounds();
	}

	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = Integer.valueOf(css.jVScreenXSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerWidth+newX > VirtualScreenBean.screenWidthMax){
				newX=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerWidth;
				css.jVScreenXSpin.setValue(newX);
				if (this.showWarning) css.showBandwidthWarning("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				VirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerX = Integer.valueOf(css.jVScreenXSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = Integer.valueOf(css.jVScreenYSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerHeight+newY > VirtualScreenBean.screenHeightMax){
				newY=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerHeight;
				css.jVScreenYSpin.setValue(newY);
				if (this.showWarning) css.showBandwidthWarning("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				VirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerY = Integer.valueOf(css.jVScreenYSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = Integer.valueOf(css.jVScreenWidthSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerX+newWidth > VirtualScreenBean.screenWidthMax){
				newWidth=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerX;
				css.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)css.showBandwidthWarning("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				VirtualScreenBean.vScreenSpinnerWidth = newWidth;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerWidth = Integer.valueOf(css.jVScreenWidthSpin.getValue().toString()).intValue();
		}
		
		calcRescaleFactors();
		//System.out.println("calcNewValueWidthSpin "+VirtualScreenBean.vScreenSpinnerWidth);
	}

	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = Integer.valueOf(css.jVScreenHeightSpin.getValue().toString()).intValue();
			if(VirtualScreenBean.vScreenSpinnerY+newHeight > VirtualScreenBean.screenHeightMax){
				newHeight=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerY;
				css.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)css.showBandwidthWarning("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				VirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerHeight = Integer.valueOf(css.jVScreenHeightSpin.getValue().toString()).intValue();
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

			css.vScreenIconLeft.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+(Long.valueOf(Math.round(newvScreenHeight)).intValue()/2));
			
			css.vScreenIconRight.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+Long.valueOf(Math.round(newvScreenWidth)).intValue()-16,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162+((Long.valueOf(Math.round(newvScreenHeight)).intValue())/2));

			css.vScreenIconUp.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8);

			css.vScreenIconDown.setLocation(
					Long.valueOf(Math.round(newvScreenX)).intValue()+30+(Long.valueOf(Math.round(newvScreenWidth)).intValue()/2)-8,
					Long.valueOf(Math.round(newvScreenY)).intValue()+162-8+(Long.valueOf(Math.round(newvScreenHeight)).intValue()));


			css.virtualScreen.setBounds(
					30+Long.valueOf(Math.round(newvScreenX)).intValue(),
							170+Long.valueOf(Math.round(newvScreenY)).intValue() ,
									Long.valueOf(Math.round(newvScreenWidth)).intValue(),
											Long.valueOf(Math.round(newvScreenHeight)).intValue() );

			css.virtualScreen.setText(VirtualScreenBean.vScreenSpinnerWidth+":"+VirtualScreenBean.vScreenSpinnerHeight);

			//System.out.println(30+Long.valueOf(Math.round(newvScreenX)).intValue()+"|||"+  Long.valueOf(Math.round(newvScreenWidth)).intValue());

			//System.out.println(170+Long.valueOf(Math.round(newvScreenY)).intValue() +","+ Long.valueOf(Math.round(newvScreenHeight)).intValue() );

	}

}
