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
package org.apache.openmeetings.screen.webstart.gui;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import org.apache.openmeetings.screen.webstart.CoreScreenShare;
import org.apache.openmeetings.screen.webstart.gui.ScreenDimensions.ScreenQuality;
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
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private boolean doUpdateBounds = true;
	private boolean showWarning = true;
	private JButton btnStartStopSharing;
	private JButton btnStartStopRecording;
	private JButton btnStartStopPublish;
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
	private boolean recordingStarted = false;
	private boolean publishStarted = false;
	private boolean recordingActionRequested = false;
	private boolean publishingActionRequested = false;
	private boolean sharingActionRequested = false;
	private ImageIcon startIcon;
	private ImageIcon stopIcon;
	private String startSharingLabel;
	private String stopSharingLabel;
	private String startRecordingLabel;
	private String stopRecordingLabel;
	private String startPublishLabel;
	private String stopPublishLabel;
	private String reduceWidthLabel;
	private String reduceHeightLabel;
	private String reduceXLabel;
	private String reduceYLabel;
	private String recordingTipLabel;
	private String publishingTipLabel;
	
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
			ImageIcon iUp = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/up.png"));
			ImageIcon iDown = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/down.png"));
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
			ImageIcon iLeft = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/previous.png"));
			ImageIcon iRight = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/next.png"));
			setSize(32, 16);
			JLabel jLeft = new JLabel(iLeft);
			jLeft.setBounds(0, 0, 16, 16);
			add(jLeft);
			JLabel jRight = new JLabel(iRight);
			jRight.setBounds(16, 0, 16, 16);
			add(jRight);
		}
	}
	
	private String getTextLabel(String[] textLabels, int idx) {
		return textLabels != null && idx < textLabels.length ? textLabels[idx] : "#STAB#";
	}
	
	/**
	 * Create the frame.
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public ScreenSharerFrame(final CoreScreenShare core, String[] textLabels) throws AWTException {
		setTitle(getTextLabel(textLabels, 0)); //#id 730
		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				core.stopStream();
				ScreenSharerFrame.this.setVisible(false);
				System.exit(0);
			}
		});
		setBounds(30, 30, 500, 505);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 0, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblStartSharing = new JLabel(getTextLabel(textLabels, 1)); //#id 731
		
		startSharingLabel = getTextLabel(textLabels, 2); //#id 732
		stopSharingLabel = getTextLabel(textLabels, 3); //#id 733
		startRecordingLabel = getTextLabel(textLabels, 15); //#id 871
		stopRecordingLabel = getTextLabel(textLabels, 16); //#id 872
		startPublishLabel = getTextLabel(textLabels, 24); //#id 1466
		stopPublishLabel = getTextLabel(textLabels, 25); //#id 1467
		reduceWidthLabel = getTextLabel(textLabels, 29); //#id 1471
		reduceHeightLabel = getTextLabel(textLabels, 30); //#id 1472
		reduceXLabel = getTextLabel(textLabels, 31); //#id 1473
		reduceYLabel = getTextLabel(textLabels, 32); //#id 1474
		recordingTipLabel = getTextLabel(textLabels, 35); //#id 1477
		publishingTipLabel = getTextLabel(textLabels, 34); //#id 1476
		startIcon = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/play.png"));
		stopIcon = new ImageIcon(ScreenSharerFrame.class.getResource("/org/apache/openmeetings/screen/stop.png"));
		btnStartStopSharing = new JButton(startSharingLabel);
		btnStartStopSharing.setToolTipText(startSharingLabel);
		btnStartStopSharing.setIcon(startIcon);
		btnStartStopSharing.setSize(200, 32);
		btnStartStopSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (sharingStarted) {
					if (!sharingActionRequested) {
						sharingActionRequested = true;
						core.sendCaptureScreenStop(true, false);
					} else {
						logger.warn("Sharing action is already requested");
					}
				} else {
					if (!sharingActionRequested) {
						sharingActionRequested = true;
						core.captureScreenStart(true, false);
					} else {
						logger.warn("Sharing action is already requested");
					}
				}
			}
		});
		
		JLabel lblSelectArea = new JLabel(getTextLabel(textLabels, 4)); //#id 734
		JPanel panelStatus = new JPanel();
		
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
							.addComponent(btnStartStopSharing, 200, 200, 200)
							.addGap(52)
							)
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
						.addComponent(btnStartStopSharing, 32, 32, 32)
						)
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
		upSlider.setToolTipText(getTextLabel(textLabels, 6)); //#id 737
		panelScreen.add(upSlider);
		downSlider.addListener(new ScreenHeightMouseListener(this));
		downSlider.setToolTipText(getTextLabel(textLabels, 6)); //#id 737
		panelScreen.add(downSlider);
		
		leftSlider.addListener(new ScreenXMouseListener(this));
		leftSlider.setToolTipText(getTextLabel(textLabels, 5)); //#id 735
		panelScreen.add(leftSlider);
		rightSlider.addListener(new ScreenWidthMouseListener(this));
		rightSlider.setToolTipText(getTextLabel(textLabels, 5)); //#id 735
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
		labelX.setText(getTextLabel(textLabels, 7)); //#id 738
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
		labelY.setText(getTextLabel(textLabels, 8)); //#id 739
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
		vscreenWidthLabel.setText(getTextLabel(textLabels, 9)); //#id 740
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
		labelHeight.setText(getTextLabel(textLabels, 10)); //#id 741
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
		labelQuality.setText(getTextLabel(textLabels, 18)); //#id 1089
		labelQuality.setBounds(250, 140, 200, 24);
		panelScreen.add(labelQuality);
		
		comboQuality = new JComboBox();
		comboQuality.addItem(new KeyValue<ScreenQuality>(getTextLabel(textLabels, 19), ScreenQuality.VeryHigh)); //#id 1090
		comboQuality.addItem(new KeyValue<ScreenQuality>(getTextLabel(textLabels, 20), ScreenQuality.High)); //#id 1091
		comboQuality.addItem(new KeyValue<ScreenQuality>(getTextLabel(textLabels, 21), ScreenQuality.Medium)); //#id 1092
		comboQuality.addItem(new KeyValue<ScreenQuality>(getTextLabel(textLabels, 22), ScreenQuality.Low)); //#id 1093
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
		
		panelRecording.setBackground(Color.WHITE);
		tabbedPane.addTab(getTextLabel(textLabels, 13), null, panelRecording, null); //#id 869
		tabbedPane.setEnabledAt(0, true);
		panelRecording.setLayout(null);
		panelRecording.setEnabled(false);
		
		JLabel lblRecordingDesc = new JLabel("<html>" + getTextLabel(textLabels, 14) + "</html>"); //#id 870
		lblRecordingDesc.setVerticalAlignment(SwingConstants.TOP);
		lblRecordingDesc.setBounds(10, 10, 447, 60);
		panelRecording.add(lblRecordingDesc);
		
		btnStartStopRecording = new JButton(getTextLabel(textLabels, 15)); //#id 871
		btnStartStopRecording.setToolTipText(getTextLabel(textLabels, 15)); //#id 871
		btnStartStopRecording.setIcon(startIcon);
		btnStartStopRecording.setBounds(10, 82, 200, 32);
		btnStartStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (recordingStarted) {
					if (!recordingActionRequested) {
						recordingActionRequested = true;
						core.sendCaptureScreenStop(false, true);
					} else {
						logger.warn("Recording action is already requested");
					}
				} else {
					if (!recordingActionRequested) {
						recordingActionRequested = true;
						core.captureScreenStart(false, true);
					} else {
						logger.warn("Recording action is already requested");
					}
				}
			}
		});
		panelRecording.add(btnStartStopRecording);
		
		panelPublish.setBackground(Color.WHITE);
		tabbedPane.addTab(getTextLabel(textLabels, 23), null, panelPublish, null); //#id 1465
		tabbedPane.setEnabledAt(1, true);
		panelPublish.setEnabled(false);
		panelPublish.setLayout(null);
		
		JLabel lblPublishDesc = new JLabel(getTextLabel(textLabels, 33)); //#id 1475
		lblPublishDesc.setVerticalAlignment(SwingConstants.TOP);
		lblPublishDesc.setBounds(10, 5, 450, 20);
		panelPublish.add(lblPublishDesc);
		
		JLabel lblPublishHost = new JLabel(getTextLabel(textLabels, 26)); //#id 1468
		lblPublishHost.setVerticalAlignment(SwingConstants.TOP);
		lblPublishHost.setBounds(10, 27, 140, 20);
		panelPublish.add(lblPublishHost);
		
		JLabel lblPublishApp = new JLabel(getTextLabel(textLabels, 27)); //#id 1469
		lblPublishApp.setVerticalAlignment(SwingConstants.TOP);
		lblPublishApp.setBounds(160, 27, 140, 20);
		panelPublish.add(lblPublishApp);
		
		JLabel lblPublishId = new JLabel(getTextLabel(textLabels, 28)); //#id 1470
		lblPublishId.setVerticalAlignment(SwingConstants.TOP);
		lblPublishId.setBounds(310, 27, 140, 20);
		panelPublish.add(lblPublishId);
		
		textPublishHost = new PublishTextField();
		textPublishHost.setBounds(10, 45, 140, 20);
		panelPublish.add(textPublishHost);
		
		textPublishApp = new PublishTextField();
		textPublishApp.setBounds(160, 45, 140, 20);
		panelPublish.add(textPublishApp);
		
		textPublishId = new PublishTextField();
		textPublishId.setBounds(310, 45, 140, 20);
		panelPublish.add(textPublishId);
		
		lblPublishURL = new JLabel("");
		lblPublishURL.setBounds(10, 69, 447, 14);
		panelPublish.add(lblPublishURL);
		
		btnStartStopPublish = new JButton(getTextLabel(textLabels, 24)); //#id 1466
		btnStartStopPublish.setToolTipText(getTextLabel(textLabels, 24)); //#id 1466
		btnStartStopPublish.setIcon(startIcon);
		btnStartStopPublish.setBounds(10, 86, 200, 32);
		btnStartStopPublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (publishStarted) {
					if (!publishingActionRequested) {
						publishingActionRequested = true;
						core.sendStopPublishing();
					} else {
						logger.warn("Publishing action is already requested");
					}
				} else {
					if (!publishingActionRequested) {
						publishingActionRequested = true;
						core.captureScreenStart(false, false, true);
					} else {
						logger.warn("Publishing action is already requested");
					}
				}
			}
		});
		panelPublish.add(btnStartStopPublish);
		
		panelScreen.setBackground(Color.WHITE);
		
		panelStatus.setBackground(SystemColor.control);
		panelStatus.setLayout(null);
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setBounds(0, 0, 494, 20);
		lblStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));
		panelStatus.add(lblStatus);
		
		contentPane.setLayout(gl_contentPane);
		
		// Background Image
		//We have no logo, that is why we need no background, sebawagner 29.04.2012
	}

	public void setSharingStatus(boolean status, boolean unlockScreen) {
		panelScreen.setEnabled(unlockScreen);
		if (status != sharingStarted) {
			sharingActionRequested = false;
		}
		sharingStarted = status;
		btnStartStopSharing.setIcon(status ? stopIcon : startIcon);
		btnStartStopSharing.setText(status ? stopSharingLabel : startSharingLabel);
		btnStartStopSharing.setToolTipText(status ? stopSharingLabel : startSharingLabel);
	}
	
	public void setRecordingStatus(boolean status, boolean unlockScreen) {
		panelScreen.setEnabled(unlockScreen);
		if (status != recordingStarted) {
			recordingActionRequested = false;
		}
		logger.debug("recordingActionRequested=" + recordingActionRequested);
		recordingStarted = status;
		btnStartStopRecording.setIcon(status ? stopIcon : startIcon);
		btnStartStopRecording.setText(status ? stopRecordingLabel : startRecordingLabel);
		btnStartStopRecording.setToolTipText(status ? stopRecordingLabel : startRecordingLabel);
	}
	
	public void setPublishingStatus(boolean status, boolean unlockScreen) {
		panelScreen.setEnabled(unlockScreen);
		if (status != publishStarted) {
			publishingActionRequested = false;
		}
		publishStarted = status;
		btnStartStopPublish.setIcon(status ? stopIcon : startIcon);
		btnStartStopPublish.setText(status ? stopPublishLabel : startPublishLabel);
		btnStartStopPublish.setToolTipText(status ? stopPublishLabel : startPublishLabel);
	}
	
	public void setRecordingTabEnabled(boolean enabled) {
		panelRecording.setEnabled(enabled);
		tabbedPane.setEnabledAt(0, enabled);
		tabbedPane.setToolTipTextAt(0, enabled ? null : recordingTipLabel);
	}
	
	public void setPublishingTabEnabled(boolean enabled) {
		panelPublish.setEnabled(enabled);
		tabbedPane.setEnabledAt(1, enabled);
		tabbedPane.setToolTipTextAt(1, enabled ? null : publishingTipLabel);
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
		logger.trace("calcRescaleFactors -- ");
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
		logger.trace("resize: X:" + ScreenDimensions.resizeX + " Y: " + ScreenDimensions.resizeY);
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
