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
package org.openmeetings.screen.webstart.gui;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmeetings.screen.webstart.BlankArea;
import org.openmeetings.screen.webstart.CoreScreenShare;
import org.openmeetings.screen.webstart.RTMPScreenShare;
import org.openmeetings.screen.webstart.gui.VirtualScreenBean.ScreenQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualScreen {
	
	private static final Logger logger = LoggerFactory.getLogger( VirtualScreen.class );
	CoreScreenShare css;
	public boolean showWarning = true;

	public boolean doUpdateBounds = true;
	private int vScreenX = 30;
	private int vScreenY = 170;
	
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
	
	public VirtualScreen(CoreScreenShare css) throws Exception {
		this.css = css;

		css.tFieldScreenZoom = new JLabel();
		css.tFieldScreenZoom.setBounds(10, 120, 200, 20);
		css.tFieldScreenZoom.setText(css.label734);
		css.contentPane.add(css.tFieldScreenZoom);

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		VirtualScreenBean.screenratio = screenSize.getHeight() / screenSize.getWidth();
		VirtualScreenBean.screenWidthMax = (int)screenSize.getWidth();
		VirtualScreenBean.screenHeightMax = (int)screenSize.getHeight();

		VirtualScreenBean.vScreenHeight = (int)Math.round(VirtualScreenBean.vScreenWidth * VirtualScreenBean.screenratio);

		int width = VirtualScreenBean.vScreenWidth;
		int height = VirtualScreenBean.vScreenHeight;

		Image im_left = ImageIO.read(RTMPScreenShare.class.getResource("/org/openmeetings/screen/resultset_previous.png"));
		ImageIcon iIcon1 = new ImageIcon(im_left);
		Image im_right = ImageIO.read(RTMPScreenShare.class.getResource("/org/openmeetings/screen/resultset_next.png"));
		ImageIcon iIcon2 = new ImageIcon(im_right);
		Image im_up = ImageIO.read(RTMPScreenShare.class.getResource("/org/openmeetings/screen/resultset_up.png"));
		ImageIcon iIcon3 = new ImageIcon(im_up);
		Image im_down = ImageIO.read(RTMPScreenShare.class.getResource("/org/openmeetings/screen/resultset_down.png"));
		ImageIcon iIcon4 = new ImageIcon(im_down);

		JLabel jLab1 = new JLabel(iIcon1);
		jLab1.setBounds(0, 0, 16, 16);
		JLabel jLab2 = new JLabel(iIcon2);
		jLab2.setBounds(16, 0, 16, 16);
		css.vScreenIconLeft.add(jLab1);
		css.vScreenIconLeft.add(jLab2);
		css.vScreenIconLeft.setToolTipText(css.label735);
		VirtualScreenXMouseListener xLeftMouseListener = new VirtualScreenXMouseListener(this);
		css.vScreenIconLeft.addMouseListener(xLeftMouseListener);
		css.vScreenIconLeft.addMouseMotionListener(xLeftMouseListener);
		css.contentPane.add(css.vScreenIconLeft);

		JLabel jLab3 = new JLabel(iIcon1);
		jLab3.setBounds(0, 0, 16, 16);
		JLabel jLab4 = new JLabel(iIcon2);
		jLab4.setBounds(16, 0, 16, 16);
		css.vScreenIconRight.add(jLab3);
		css.vScreenIconRight.add(jLab4);
		css.vScreenIconRight.setToolTipText(css.label735);
		VirtualScreenWidthMouseListener widthMouseListener = new VirtualScreenWidthMouseListener(this);
		css.vScreenIconRight.addMouseListener(widthMouseListener);
		css.vScreenIconRight.addMouseMotionListener(widthMouseListener);
		css.contentPane.add(css.vScreenIconRight);

		JLabel jLab5 = new JLabel(iIcon3);
		jLab5.setBounds(0, 0, 16, 16);
		JLabel jLab6 = new JLabel(iIcon4);
		jLab6.setBounds(0, 16, 16, 16);
		css.vScreenIconUp.add(jLab5);
		css.vScreenIconUp.add(jLab6);
		css.vScreenIconUp.setToolTipText(css.label737);
		VirtualScreenYMouseListener yMouseListener = new VirtualScreenYMouseListener(this);
		css.vScreenIconUp.addMouseListener(yMouseListener);
		css.vScreenIconUp.addMouseMotionListener(yMouseListener);
		css.contentPane.add(css.vScreenIconUp);

		JLabel jLab7 = new JLabel(iIcon3);
		jLab7.setBounds(0, 0, 16, 16);
		JLabel jLab8 = new JLabel(iIcon4);
		jLab8.setBounds(0, 16, 16, 16);
		css.vScreenIconDown.add(jLab7);
		css.vScreenIconDown.add(jLab8);
		css.vScreenIconDown.setToolTipText(css.label737);
		VirtualScreenHeightMouseListener heightMouseListener = new VirtualScreenHeightMouseListener(this);
		css.vScreenIconDown.addMouseListener(heightMouseListener);
		css.vScreenIconDown.addMouseMotionListener(heightMouseListener);
		css.contentPane.add(css.vScreenIconDown);


		css.virtualScreen = new BlankArea(new Color(255, 255, 255, 100));
		VirtualScreenMouseListener vListener = new VirtualScreenMouseListener(this);
		css.virtualScreen.addMouseListener(vListener);
		css.virtualScreen.addMouseMotionListener(vListener);
		css.contentPane.add(css.virtualScreen);
		setBounds(0, 0, width, height);

		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage imageScreen = robot.createScreenCapture(screenRectangle);

		Image img = imageScreen.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		//imageScreen.
		System.out.println("img"+img);
		ImageIcon image = new ImageIcon(img);

		css.blankArea = new JLabel(image);
		css.blankArea.setBounds(vScreenX, vScreenY, width, height);
		css.contentPane.add(css.blankArea);

		//Spinner X
		VirtualScreenBean.vScreenSpinnerX = 0;
		css.vscreenXLabel = new JLabel();
		css.vscreenXLabel.setText(css.label738);
		css.vscreenXLabel.setBounds(250, 170, 150, 24);
		css.contentPane.add(css.vscreenXLabel);

		css.jVScreenXSpin = new NumberSpinner(VirtualScreenBean.vScreenSpinnerX, 0, VirtualScreenBean.screenWidthMax, 1);
		css.jVScreenXSpin.setBounds(400, 170, 60, 24);
		css.jVScreenXSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueXSpin();
			}
		});
		css.contentPane.add(css.jVScreenXSpin);

		//Spinner Y
		VirtualScreenBean.vScreenSpinnerY = 0;
		css.vscreenYLabel = new JLabel();
		css.vscreenYLabel.setText(css.label739);
		css.vscreenYLabel.setBounds(250, 200, 150, 24);
		css.contentPane.add(css.vscreenYLabel);

		css.jVScreenYSpin = new NumberSpinner(VirtualScreenBean.vScreenSpinnerY, 0, VirtualScreenBean.screenHeightMax, 1);
		css.jVScreenYSpin.setBounds(400, 200, 60, 24);
		css.jVScreenYSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueYSpin();
			}
		});
		css.contentPane.add(css.jVScreenYSpin);

		//Spinner Width
		VirtualScreenBean.vScreenSpinnerWidth = VirtualScreenBean.screenWidthMax;
		css.vscreenWidthLabel = new JLabel();
		css.vscreenWidthLabel.setText(css.label740);
		css.vscreenWidthLabel.setBounds(250, 240, 150, 24);
		css.contentPane.add(css.vscreenWidthLabel);

		css.jVScreenWidthSpin = new NumberSpinner(VirtualScreenBean.vScreenSpinnerWidth, 0, VirtualScreenBean.screenWidthMax, 1);
		css.jVScreenWidthSpin.setBounds(400, 240, 60, 24);
		css.jVScreenWidthSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueWidthSpin();
			}
		});
		css.contentPane.add(css.jVScreenWidthSpin);

		//Spinner Height
		VirtualScreenBean.vScreenSpinnerHeight = VirtualScreenBean.screenHeightMax;
		css.vscreenHeightLabel = new JLabel();
		css.vscreenHeightLabel.setText(css.label741);
		css.vscreenHeightLabel.setBounds(250, 270, 150, 24);
		css.contentPane.add(css.vscreenHeightLabel);

		css.jVScreenHeightSpin = new NumberSpinner(VirtualScreenBean.vScreenSpinnerHeight, 0, VirtualScreenBean.screenHeightMax, 1);
		css.jVScreenHeightSpin.setBounds(400, 270, 60, 24);
		css.jVScreenHeightSpin.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueHeightSpin();
			}
		});
		css.contentPane.add(css.jVScreenHeightSpin);
		
		
		VirtualScreenBean.vScreenResizeX = 640;
		VirtualScreenBean.vScreenResizeY = 400;
		
		css.vscreenResizeLabel = new JLabel();
		css.vscreenResizeLabel.setText(css.label1089);
		css.vscreenResizeLabel.setBounds(250, 300, 200,24 );
		css.contentPane.add(css.vscreenResizeLabel);
		
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
		css.contentPane.add(css.jVScreenResizeMode);
		
	}
	
	/**
	 * Needs to be always invoked after every re-scaling
	 */
	void calcRescaleFactors() {
		logger.debug("calcRescaleFactors -- ");
		VirtualScreenBean.vScreenResizeX = css.jVScreenWidthSpin.getValue();
		VirtualScreenBean.vScreenResizeY = css.jVScreenHeightSpin.getValue();
		switch (VirtualScreenBean.screenQuality) {
			case Medium:
				VirtualScreenBean.vScreenResizeX = (int)(1.0/2 * VirtualScreenBean.vScreenResizeX);
				VirtualScreenBean.vScreenResizeY = (int)(1.0/2 * VirtualScreenBean.vScreenResizeY);
				break;
			case Low:
				VirtualScreenBean.vScreenResizeX = (int)(3.0/8 * VirtualScreenBean.vScreenResizeX);
				VirtualScreenBean.vScreenResizeY = (int)(3.0/8 * VirtualScreenBean.vScreenResizeY);
				break;
			case VeryHigh:
			case High:
			default:
				break;
		}
		logger.debug("resize: X:" + VirtualScreenBean.vScreenResizeX + " Y: " + VirtualScreenBean.vScreenResizeY);
		updateVScreenBounds();
	}

	void calcNewValueXSpin(){
		if (this.doUpdateBounds){
			int newX = css.jVScreenXSpin.getValue();
			if(VirtualScreenBean.vScreenSpinnerWidth+newX > VirtualScreenBean.screenWidthMax){
				newX=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerWidth;
				css.jVScreenXSpin.setValue(newX);
				if (this.showWarning) css.showBandwidthWarning("Reduce the width of the SharingScreen before you try to move it left");
			} else {
				VirtualScreenBean.vScreenSpinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerX = css.jVScreenXSpin.getValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueYSpin(){
		if (this.doUpdateBounds){
			int newY = css.jVScreenYSpin.getValue();
			if(VirtualScreenBean.vScreenSpinnerHeight+newY > VirtualScreenBean.screenHeightMax){
				newY=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerHeight;
				css.jVScreenYSpin.setValue(newY);
				if (this.showWarning) css.showBandwidthWarning("Reduce the height of the SharingScreen before you try to move it bottom");
			} else {
				VirtualScreenBean.vScreenSpinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerY = css.jVScreenYSpin.getValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueWidthSpin(){
		if (this.doUpdateBounds){
			int newWidth = css.jVScreenWidthSpin.getValue();
			if(VirtualScreenBean.vScreenSpinnerX+newWidth > VirtualScreenBean.screenWidthMax){
				newWidth=VirtualScreenBean.screenWidthMax-VirtualScreenBean.vScreenSpinnerX;
				css.jVScreenWidthSpin.setValue(newWidth);
				if (this.showWarning)css.showBandwidthWarning("Reduce the x of the SharingScreen before you try to make it wider");
			} else {
				VirtualScreenBean.vScreenSpinnerWidth = newWidth;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerWidth = css.jVScreenWidthSpin.getValue();
		}
		
		calcRescaleFactors();
	}

	void calcNewValueHeightSpin(){
		if (this.doUpdateBounds){
			int newHeight = css.jVScreenHeightSpin.getValue();
			if(VirtualScreenBean.vScreenSpinnerY+newHeight > VirtualScreenBean.screenHeightMax){
				newHeight=VirtualScreenBean.screenHeightMax-VirtualScreenBean.vScreenSpinnerY;
				css.jVScreenHeightSpin.setValue(newHeight);
				if (this.showWarning)css.showBandwidthWarning("Reduce the y of the SharingScreen before you try to make it higher");
			} else {
				VirtualScreenBean.vScreenSpinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			VirtualScreenBean.vScreenSpinnerHeight = css.jVScreenHeightSpin.getValue();
		}
		
		calcRescaleFactors();
	}

	private void setBounds(int x, int y, int width, int height) {
		css.vScreenIconLeft.setBounds(x + vScreenX - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		css.vScreenIconRight.setBounds(x + vScreenX + width - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		css.vScreenIconUp.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16, 16, 32);
		css.vScreenIconDown.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16 + height, 16, 32);
		
		css.virtualScreen.setText(VirtualScreenBean.vScreenSpinnerWidth + ":" + VirtualScreenBean.vScreenSpinnerHeight);
		css.virtualScreen.setBounds(x + vScreenX, y + vScreenY, width, height);
	}
	
	/**
	 * update the bounds of the vScreen
	 * by useing the vars from the Spinners
	 *
	 */
	void updateVScreenBounds(){
		double ratio = ((double)VirtualScreenBean.vScreenWidth) / VirtualScreenBean.screenWidthMax;
		int newvScreenWidth = (int)(VirtualScreenBean.vScreenSpinnerWidth * ratio);
		int newvScreenX = (int)(VirtualScreenBean.vScreenSpinnerX * ratio);

		int newvScreenHeight = (int)(VirtualScreenBean.vScreenSpinnerHeight * ratio);
		int newvScreenY = (int)(VirtualScreenBean.vScreenSpinnerY * ratio);

		setBounds(newvScreenX, newvScreenY, newvScreenWidth, newvScreenHeight);
	}
}
