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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmeetings.screen.webstart.CoreScreenShare;
import org.openmeetings.screen.webstart.gui.ScreenDimensions.ScreenQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenSharerFrame extends JFrame {
	private static final long serialVersionUID = 6892639796386017437L;
	private static final Logger logger = LoggerFactory.getLogger(ScreenSharerFrame.class);
	
	private JPanel contentPane;
	private JPanel panelScreen = new DisabledPanel();
	private JPanel panelRecording = new DisabledPanel();
	private JPanel panelPublish = new DisabledPanel();
	private JLabel lblStatus = new JLabel();
	private int vScreenX = 20;
	private int vScreenY = 20;
	private VerticalSlider upSlider = new VerticalSlider();
	private VerticalSlider downSlider = new VerticalSlider();
	private HorizontalSlider leftSlider = new HorizontalSlider();
	private HorizontalSlider rightSlider = new HorizontalSlider();
	private BlankArea virtualScreen = new BlankArea(new Color(255, 255, 255, 100));
	private JTabbedPane tabbedPane;
	private boolean doUpdateBounds = true;
	private boolean showWarning = true;
	private JButton btnStartPauseSharing;
	private JButton btnStartRecording;
	private JButton btnStopRecording;
	private JButton btnStartPublish;
	private JButton btnStopPublish;
	private NumberSpinner spinnerX;
	private NumberSpinner spinnerY;
	private NumberSpinner spinnerWidth;
	private NumberSpinner spinnerHeight;
	private JComboBox comboQuality;
	private JTextField textPublishHost;
	private JTextField textPublishApp;
	private JTextField textPublishId;
	private JLabel lblPublishURL;
	private boolean sharingStarted = false;
	private String startLabel;
	private ImageIcon startIcon;
	private String pauseLabel;
	private ImageIcon pauseIcon;
	private String reduceWidthLabel;
	private String reduceHeightLabel;
	private String reduceXLabel;
	private String reduceYLabel;
	
	private class PublishTextField extends JTextField {
		private static final long serialVersionUID = -2104245360975135871L;

		PublishTextField() {
			getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						updatePublishURL();
					}

					public void removeUpdate(DocumentEvent e) {
						updatePublishURL();
					}

					public void insertUpdate(DocumentEvent e) {
						updatePublishURL();
					}
				});

			setColumns(10);
		}
	}
	
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
	
	//this implementation will not allow to Enable Panel in runtime
	private class DisabledPanel extends JPanel {
		private static final long serialVersionUID = -2679640611165728979L;

		@Override
		public void setEnabled(boolean enabled) {
			for (Component c : getComponents()) {
				c.setEnabled(enabled);
			}
			super.setEnabled(enabled);
		}
	}
	
	private class VerticalSlider extends MouseListenerable {
		private static final long serialVersionUID = 6388951979741767971L;

		public VerticalSlider() {
			ImageIcon iUp = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/up.png"));
			ImageIcon iDown = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/down.png"));
			setSize(16, 32);
			JLabel jUp = new JLabel(iUp);
			jUp.setBounds(0, 0, 16, 16);
			add(jUp);
			JLabel jDown = new JLabel(iDown);
			jDown.setBounds(0, 16, 16, 16);
			add(jDown);
		}
	}
	
	private class HorizontalSlider extends MouseListenerable {
		private static final long serialVersionUID = 4630712955901760443L;

		public HorizontalSlider() {
			ImageIcon iLeft = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/previous.png"));
			ImageIcon iRight = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/next.png"));
			setSize(32, 16);
			JLabel jLeft = new JLabel(iLeft);
			jLeft.setBounds(0, 0, 16, 16);
			add(jLeft);
			JLabel jRight = new JLabel(iRight);
			jRight.setBounds(16, 0, 16, 16);
			add(jRight);
		}
	}
	
	/**
	 * Create the frame.
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public ScreenSharerFrame(final CoreScreenShare core) throws AWTException {
		setTitle(core.label730);
		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(30, 30, 500, 505);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 0, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblStartSharing = new JLabel(core.label731);
		
		startLabel = core.label732;
		pauseLabel = core.label733;
		reduceWidthLabel = core.label1471;
		reduceHeightLabel = core.label1472;
		reduceXLabel = core.label1473;
		reduceYLabel = core.label1474;
		startIcon = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/play.png"));
		pauseIcon = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/pause.png"));
		btnStartPauseSharing = new JButton(startLabel);
		btnStartPauseSharing.setToolTipText(startLabel);
		btnStartPauseSharing.setIcon(startIcon);
		btnStartPauseSharing.setSize(200, 32);
		btnStartPauseSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (sharingStarted) {
					core.captureScreenStop(true, false);
				} else {
					core.captureScreenStart(true, false);
				}
			}
		});
		JButton btnStopSharing = new JButton(core.label878);
		btnStopSharing.setToolTipText(core.label878);
		btnStopSharing.setSize(200, 32);
		btnStopSharing.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/exit.png")));
		btnStopSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScreenSharerFrame.this.setVisible(false);
				System.exit(0);
			}
		});
		
		JLabel lblSelectArea = new JLabel(core.label734);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		panelRecording.setBackground(Color.WHITE);
		tabbedPane.addTab(core.label869, null, panelRecording, null);
		tabbedPane.setEnabledAt(0, true);
		panelRecording.setLayout(null);
		panelRecording.setEnabled(false);
		
		JLabel lblRecordingDesc = new JLabel(core.label870);
		lblRecordingDesc.setVerticalAlignment(SwingConstants.TOP);
		lblRecordingDesc.setBounds(10, 10, 447, 60);
		panelRecording.add(lblRecordingDesc);
		
		btnStartRecording = new JButton(core.label871);
		btnStartRecording.setToolTipText(core.label871);
		btnStartRecording.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/record.png")));
		btnStartRecording.setBounds(10, 82, 200, 32);
		btnStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.captureScreenStart(false, true);
			}
		});
		panelRecording.add(btnStartRecording);
		
		ImageIcon stopIcon = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/stop.png"));
		btnStopRecording = new JButton(core.label872, stopIcon);
		btnStopRecording.setToolTipText(core.label872);
		btnStopRecording.setBounds(257, 82, 200, 32);
		btnStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.captureScreenStop(false, true);
			}
		});
		panelRecording.add(btnStopRecording);
		
		panelPublish.setBackground(Color.WHITE);
		tabbedPane.addTab(core.label1465, null, panelPublish, null);
		tabbedPane.setEnabledAt(1, true);
		panelPublish.setEnabled(false);
		panelPublish.setLayout(null);
		
		btnStartPublish = new JButton(core.label1466);
		btnStartPublish.setToolTipText(core.label1466);
		//btnStartPublish.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/record.png")));
		btnStartPublish.setBounds(10, 82, 200, 32);
		btnStartPublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.captureScreenStart(false, false, true);
			}
		});
		panelPublish.add(btnStartPublish);
		
		btnStopPublish = new JButton(core.label1467);
		btnStopPublish.setToolTipText(core.label1467);
		btnStopPublish.setIcon(stopIcon);
		btnStopPublish.setBounds(257, 82, 200, 32);
		btnStopPublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.captureScreenStop(false, false, true);
			}
		});
		panelPublish.add(btnStopPublish);
		
		JLabel lblPublishHost = new JLabel(core.label1468);
		lblPublishHost.setVerticalAlignment(SwingConstants.TOP);
		lblPublishHost.setBounds(10, 10, 140, 20);
		panelPublish.add(lblPublishHost);
		
		textPublishHost = new PublishTextField();
		textPublishHost.setBounds(10, 38, 140, 20);
		panelPublish.add(textPublishHost);
		
		JLabel lblPublishApp = new JLabel(core.label1469);
		lblPublishApp.setVerticalAlignment(SwingConstants.TOP);
		lblPublishApp.setBounds(160, 10, 140, 20);
		panelPublish.add(lblPublishApp);
		
		textPublishApp = new PublishTextField();
		textPublishApp.setBounds(160, 38, 140, 20);
		panelPublish.add(textPublishApp);
		
		JLabel lblPublishId = new JLabel(core.label1470);
		lblPublishId.setVerticalAlignment(SwingConstants.TOP);
		lblPublishId.setBounds(310, 10, 140, 20);
		panelPublish.add(lblPublishId);
		
		textPublishId = new PublishTextField();
		textPublishId.setBounds(310, 38, 140, 20);
		panelPublish.add(textPublishId);
		
		lblPublishURL = new JLabel("");
		lblPublishURL.setBounds(10, 63, 447, 14);
		panelPublish.add(lblPublishURL);
		
		panelScreen.setBackground(Color.WHITE);
		
		JPanel panelStatus = new JPanel();
		panelStatus.setBackground(SystemColor.control);
		panelStatus.setLayout(null);
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setBounds(0, 0, 494, 20);
		lblStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));
		panelStatus.add(lblStatus);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblStartSharing))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(21)
							.addComponent(btnStartPauseSharing, 200, 200, 200)
							.addGap(52)
							.addComponent(btnStopSharing, 200, 200, 200))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblSelectArea, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panelScreen, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(panelStatus, 494, 494, 494)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(7)
					.addComponent(lblStartSharing)
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnStartPauseSharing, 32, 32, 32)
						.addComponent(btnStopSharing, 32, 32, 32))
					.addGap(4)
					.addComponent(lblSelectArea)
					.addGap(4)
					.addComponent(panelScreen, 210, 210, 210)
					.addGap(4)
					.addComponent(tabbedPane, 150, 150, 150)
					.addGap(5)
					.addComponent(panelStatus, 20, 20, 20)
					)
		);
		panelScreen.setLayout(null);
		
		int width = ScreenDimensions.width;
		int height = ScreenDimensions.height;
		
		//Sliders
		upSlider.addListener(new ScreenYMouseListener(this));
		upSlider.setToolTipText(core.label737);
		panelScreen.add(upSlider);
		downSlider.addListener(new ScreenHeightMouseListener(this));
		downSlider.setToolTipText(core.label737);
		panelScreen.add(downSlider);
		
		leftSlider.addListener(new ScreenXMouseListener(this));
		leftSlider.setToolTipText(core.label735);
		panelScreen.add(leftSlider);
		rightSlider.addListener(new ScreenWidthMouseListener(this));
		rightSlider.setToolTipText(core.label735);
		panelScreen.add(rightSlider);
		
		//Virtual Screen
		virtualScreen.addListener(new ScreenMouseListener(this));
		virtualScreen.setBounds(vScreenX, vScreenY, width, height);
		panelScreen.add(virtualScreen);

		ImageIcon imgBgScreen = new ImageIcon(
			new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()))
				.getScaledInstance(width, height, Image.SCALE_SMOOTH));
		JLabel bgScreen = new JLabel(imgBgScreen);
		bgScreen.setBounds(vScreenX, vScreenY, width, height);
		panelScreen.add(bgScreen);

		//Spinner X
		JLabel labelX = new JLabel();
		labelX.setText(core.label738);
		labelX.setBounds(250, 20, 150, 24);
		panelScreen.add(labelX);

		spinnerX = new NumberSpinner(ScreenDimensions.spinnerX, 0, ScreenDimensions.widthMax, 1);
		spinnerX.setBounds(400, 20, 60, 24);
		spinnerX.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueXSpin();
			}
		});
		panelScreen.add(spinnerX);

		//Spinner Y
		JLabel labelY = new JLabel();
		labelY.setText(core.label739);
		labelY.setBounds(250, 50, 150, 24);
		panelScreen.add(labelY);

		spinnerY = new NumberSpinner(ScreenDimensions.spinnerY, 0, ScreenDimensions.heightMax, 1);
		spinnerY.setBounds(400, 50, 60, 24);
		spinnerY.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueYSpin();
			}
		});
		panelScreen.add(spinnerY);
		
		JLabel vscreenWidthLabel = new JLabel();
		vscreenWidthLabel.setText(core.label740);
		vscreenWidthLabel.setBounds(250, 80, 150, 24);
		panelScreen.add(vscreenWidthLabel);

		spinnerWidth = new NumberSpinner(ScreenDimensions.spinnerWidth, 0, ScreenDimensions.widthMax, 1);
		spinnerWidth.setBounds(400, 80, 60, 24);
		spinnerWidth.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueWidthSpin();
			}
		});
		panelScreen.add(spinnerWidth);

		//Spinner Height
		JLabel labelHeight = new JLabel();
		labelHeight.setText(core.label741);
		labelHeight.setBounds(250, 110, 150, 24);
		panelScreen.add(labelHeight);

		spinnerHeight = new NumberSpinner(ScreenDimensions.spinnerHeight, 0, ScreenDimensions.heightMax, 1);
		spinnerHeight.setBounds(400, 110, 60, 24);
		spinnerHeight.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueHeightSpin();
			}
		});
		panelScreen.add(spinnerHeight);
		
		//Quality
		JLabel labelQuality = new JLabel();
		labelQuality.setText(core.label1089);
		labelQuality.setBounds(250, 140, 200, 24);
		panelScreen.add(labelQuality);
		
		comboQuality = new JComboBox();
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1090, ScreenQuality.VeryHigh));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1091, ScreenQuality.High));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1092, ScreenQuality.Medium));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1093, ScreenQuality.Low));
		comboQuality.setBounds(250, 170, 200, 24);
		comboQuality.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
		        ScreenDimensions.quality = ((KeyValue<ScreenQuality>)comboQuality.getSelectedItem()).getValue();
		        calcRescaleFactors();
			}
		}); 
		comboQuality.setSelectedIndex(core.defaultQuality);
		panelScreen.add(comboQuality);
		
		setPublishingTabEnabled(false);
		contentPane.setLayout(gl_contentPane);
		
		// Background Image
		//We have no logo, that is why we need no background, sebawagner 29.04.2012
	}

	public void setSharingStatus(boolean status) {
		panelScreen.setEnabled(!status);
		sharingStarted = status;
		btnStartPauseSharing.setIcon(status ? pauseIcon : startIcon);
		btnStartPauseSharing.setText(status ? pauseLabel : startLabel);
		btnStartPauseSharing.setToolTipText(status ? pauseLabel : startLabel);
		setPublishingTabEnabled(status);
	}
	
	public void setRecordingStatus(boolean status) {
		panelScreen.setEnabled(!status);
		btnStartRecording.setEnabled(!status);
		btnStopRecording.setEnabled(status);
	}
	
	public void setPublishingStatus(boolean status) {
		panelScreen.setEnabled(!status);
		btnStartPublish.setEnabled(!status);
		btnStopPublish.setEnabled(status);
	}
	
	public void setRecordingTabEnabled(boolean enabled) {
		panelRecording.setEnabled(enabled);
		btnStopRecording.setEnabled(false);
		tabbedPane.setEnabledAt(0, enabled);
	}
	
	private void setPublishingTabEnabled(boolean enabled) {
		panelPublish.setEnabled(enabled);
		btnStopPublish.setEnabled(false);
		tabbedPane.setEnabledAt(1, enabled);
	}
	
	public String getPublishHost() {
		return textPublishHost.getText();
	}
	
	public String getPublishApp() {
		return textPublishApp.getText();
	}
	
	public String getPublishId() {
		return textPublishId.getText();
	}
	
	private void updatePublishURL() {
		lblPublishURL.setText("rtmp://" + textPublishHost.getText() + ":1935/"
				+ textPublishApp.getText() + "/" + textPublishId.getText());
	}
	
	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}
	
	public void setDoUpdateBounds(boolean doUpdateBounds) {
		this.doUpdateBounds = doUpdateBounds;
	}
	
	public void setStatus(String status) {
		lblStatus.setText(status);
	}

	public void setSpinnerX(int val) {
		spinnerX.setValue(val);
	}

	public void setSpinnerY(int val) {
		spinnerY.setValue(val);
	}

	public void setSpinnerWidth(int val) {
		spinnerWidth.setValue(val);
	}

	public void setSpinnerHeight(int val) {
		spinnerHeight.setValue(val);
	}

	void calcNewValueXSpin() {
		if (doUpdateBounds) {
			int newX = spinnerX.getValue();
			if (ScreenDimensions.spinnerWidth + newX > ScreenDimensions.widthMax) {
				newX = ScreenDimensions.widthMax - ScreenDimensions.spinnerWidth;
				spinnerX.setValue(newX);
				if (showWarning) {
					setStatus(reduceWidthLabel);
				}
			} else {
				ScreenDimensions.spinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerX = spinnerX.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueYSpin() {
		if (doUpdateBounds) {
			int newY = spinnerY.getValue();
			if (ScreenDimensions.spinnerHeight + newY > ScreenDimensions.heightMax) {
				newY = ScreenDimensions.heightMax - ScreenDimensions.spinnerHeight;
				spinnerY.setValue(newY);
				if (showWarning) {
					setStatus(reduceHeightLabel);
				}
			} else {
				ScreenDimensions.spinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerY = spinnerY.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueWidthSpin() {
		if (doUpdateBounds) {
			int newWidth = spinnerWidth.getValue();
			if (ScreenDimensions.spinnerX + newWidth > ScreenDimensions.widthMax) {
				newWidth = ScreenDimensions.widthMax - ScreenDimensions.spinnerX;
				spinnerWidth.setValue(newWidth);
				if (showWarning) {
					setStatus(reduceXLabel);
				}
			} else {
				ScreenDimensions.spinnerWidth = newWidth;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerWidth = spinnerWidth.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueHeightSpin() {
		if (doUpdateBounds) {
			int newHeight = spinnerHeight.getValue();
			if (ScreenDimensions.spinnerY + newHeight > ScreenDimensions.heightMax) {
				newHeight = ScreenDimensions.heightMax - ScreenDimensions.spinnerY;
				spinnerHeight.setValue(newHeight);
				if (showWarning) {
					setStatus(reduceYLabel);
				}
			} else {
				ScreenDimensions.spinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerHeight = spinnerHeight.getValue();
		}

		calcRescaleFactors();
	}

	/**
	 * Needs to be always invoked after every re-scaling
	 */
	void calcRescaleFactors() {
		logger.debug("calcRescaleFactors -- ");
		ScreenDimensions.resizeX = spinnerWidth.getValue();
		ScreenDimensions.resizeY = spinnerHeight.getValue();
		switch (ScreenDimensions.quality) {
			case Medium:
				ScreenDimensions.resizeX = (int)(1.0/2 * ScreenDimensions.resizeX);
				ScreenDimensions.resizeY = (int)(1.0/2 * ScreenDimensions.resizeY);
				break;
			case Low:
				ScreenDimensions.resizeX = (int)(3.0/8 * ScreenDimensions.resizeX);
				ScreenDimensions.resizeY = (int)(3.0/8 * ScreenDimensions.resizeY);
				break;
			case VeryHigh:
			case High:
			default:
				break;
		}
		logger.debug("resize: X:" + ScreenDimensions.resizeX + " Y: " + ScreenDimensions.resizeY);
		updateVScreenBounds();
	}

	private void setVScreenBounds(int x, int y, int width, int height) {
		leftSlider.setBounds(x + vScreenX - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		rightSlider.setBounds(x + vScreenX + width - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		upSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16, 16, 32);
		downSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16 + height, 16, 32);
		
		virtualScreen.setText(ScreenDimensions.spinnerWidth + ":" + ScreenDimensions.spinnerHeight);
		virtualScreen.setBounds(x + vScreenX, y + vScreenY, width, height);
	}
	
	/**
	 * update the bounds of the vScreen
	 * by using the vars from the Spinners
	 *
	 */
	void updateVScreenBounds() {
		double ratio = ((double)ScreenDimensions.width) / ScreenDimensions.widthMax;
		int newWidth = (int)(ScreenDimensions.spinnerWidth * ratio);
		int newX = (int)(ScreenDimensions.spinnerX * ratio);

		int newHeight = (int)(ScreenDimensions.spinnerHeight * ratio);
		int newY = (int)(ScreenDimensions.spinnerY * ratio);

		setVScreenBounds(newX, newY, newWidth, newHeight);
	}
}
