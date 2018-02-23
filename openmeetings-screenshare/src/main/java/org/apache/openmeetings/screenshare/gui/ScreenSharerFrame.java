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
package org.apache.openmeetings.screenshare.gui;

import static org.apache.openmeetings.screenshare.gui.ScreenDimensions.ROUND_VALUE;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.openmeetings.screenshare.Core;
import org.apache.openmeetings.screenshare.gui.ScreenDimensions.ScreenQuality;
import org.apache.openmeetings.screenshare.gui.listener.ScreenHeightMouseListener;
import org.apache.openmeetings.screenshare.gui.listener.ScreenMouseListener;
import org.apache.openmeetings.screenshare.gui.listener.ScreenWidthMouseListener;
import org.apache.openmeetings.screenshare.gui.listener.ScreenXMouseListener;
import org.apache.openmeetings.screenshare.gui.listener.ScreenYMouseListener;
import org.slf4j.Logger;

public class ScreenSharerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = getLogger(ScreenSharerFrame.class);

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
	private JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private boolean doUpdateBounds = true;
	private boolean showWarning = true;
	private JButton btnStartStopSharing;
	private JButton btnStartStopRecording;
	private JButton btnStartStopPublish;
	private NumberSpinner spinnerX;
	private NumberSpinner spinnerY;
	private NumberSpinner spinnerWidth;
	private NumberSpinner spinnerHeight;
	private JComboBox<KeyValue<ScreenQuality>> comboQuality;
	private JComboBox<KeyValue<Integer>> comboFPS;
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
	private JCheckBox audioNotify;
	private final transient Core core;

	private class PublishTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		PublishTextField() {
			getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) {
						updatePublishURL();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						updatePublishURL();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						updatePublishURL();
					}
				});

			setColumns(10);
		}

		private void updatePublishURL() {
			lblPublishURL.setText("rtmp://" + textPublishHost.getText() + ":1935/"
					+ textPublishApp.getText() + "/" + textPublishId.getText());
		}
	}

	private static class KeyValue<T> {
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
				return kv.value.equals(this.value);
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
	private static class DisabledPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void setEnabled(boolean enabled) {
			for (Component c : getComponents()) {
				c.setEnabled(enabled);
			}
			super.setEnabled(enabled);
		}
	}

	private static class VerticalSlider extends MouseListenerable {
		private static final long serialVersionUID = 1L;

		public VerticalSlider() {
			ImageIcon iUp = new ImageIcon(ScreenSharerFrame.class.getResource("up.png"));
			ImageIcon iDown = new ImageIcon(ScreenSharerFrame.class.getResource("down.png"));
			setSize(16, 32);
			JLabel jUp = new JLabel(iUp);
			jUp.setBounds(0, 0, 16, 16);
			add(jUp);
			JLabel jDown = new JLabel(iDown);
			jDown.setBounds(0, 16, 16, 16);
			add(jDown);
		}
	}

	private static class HorizontalSlider extends MouseListenerable {
		private static final long serialVersionUID = 1L;

		public HorizontalSlider() {
			ImageIcon iLeft = new ImageIcon(ScreenSharerFrame.class.getResource("previous.png"));
			ImageIcon iRight = new ImageIcon(ScreenSharerFrame.class.getResource("next.png"));
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
	 *
	 * @param core - core object
	 * @param textLabels - translated labels
	 * @throws AWTException - in case of AWT exception
	 */
	public ScreenSharerFrame(final Core core, String[] textLabels) throws AWTException {
		this.core = core;
		setTitle(getTextLabel(textLabels, 0)); //#id 730
		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				core.stopStream(null);
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
		startIcon = new ImageIcon(getClass().getResource("play.png"));
		stopIcon = new ImageIcon(getClass().getResource("stop.png"));
		btnStartStopSharing = new JButton(startSharingLabel);
		btnStartStopSharing.setToolTipText(startSharingLabel);
		btnStartStopSharing.setIcon(startIcon);
		btnStartStopSharing.setSize(200, 32);
		btnStartStopSharing.addActionListener(evt -> {
				if (sharingStarted) {
					if (!sharingActionRequested) {
						sharingActionRequested = true;
						core.sharingStop();
					} else {
						logger.warn("Sharing action is already requested");
					}
				} else {
					if (!sharingActionRequested) {
						sharingActionRequested = true;
						core.sharingStart();
					} else {
						logger.warn("Sharing action is already requested");
					}
				}
			});

		JLabel lblSelectArea = new JLabel(getTextLabel(textLabels, 4)); //#id 734
		JPanel panelStatus = new JPanel();
		audioNotify = new JCheckBox(getTextLabel(textLabels, 36)); //#id 1589
		audioNotify.setBackground(Color.WHITE);
		audioNotify.setSelected(core.isAudioNotify());
		audioNotify.addActionListener(e -> core.setAudioNotify(audioNotify.isSelected()));

		GroupLayout glContentPane = new GroupLayout(contentPane);
		glContentPane.setHorizontalGroup(
			glContentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPane.createSequentialGroup()
					.addGroup(glContentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(glContentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblStartSharing)
							.addGap(15)
							.addComponent(audioNotify))
						.addGroup(glContentPane.createSequentialGroup()
							.addGap(21)
							.addComponent(btnStartStopSharing, 200, 200, 200)
							.addGap(52)
							)
						.addGroup(glContentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblSelectArea, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE))
						.addGroup(glContentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panelScreen, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(glContentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(glContentPane.createSequentialGroup()
							.addComponent(panelStatus, 494, 494, 494)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		glContentPane.setVerticalGroup(
			glContentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPane.createSequentialGroup()
					.addGap(7)
					.addComponent(lblStartSharing)
					.addGap(4)
					.addGroup(glContentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnStartStopSharing, 32, 32, 32).addComponent(audioNotify, 32, 32, 32)
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
		panelScreen.setBackground(Color.WHITE);

		int width = getDim().getWidth();
		int height = getDim().getHeight();

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

		//Spinner Width
		JLabel vscreenWidthLabel = new JLabel();
		vscreenWidthLabel.setText(getTextLabel(textLabels, 9)); //#id 740
		vscreenWidthLabel.setBounds(250, 20, 150, 24);
		panelScreen.add(vscreenWidthLabel);

		spinnerWidth = new NumberSpinner(getDim().getSpinnerWidth(), 0, getDim().getWidthMax(), 1);
		spinnerWidth.setBounds(400, 20, 60, 24);
		spinnerWidth.addChangeListener(e -> calcNewValueWidthSpin());
		panelScreen.add(spinnerWidth);

		//Spinner Height
		JLabel labelHeight = new JLabel();
		labelHeight.setText(getTextLabel(textLabels, 10)); //#id 741
		labelHeight.setBounds(250, 50, 150, 24);
		panelScreen.add(labelHeight);

		spinnerHeight = new NumberSpinner(getDim().getSpinnerHeight(), 0, getDim().getHeightMax(), 1);
		spinnerHeight.setBounds(400, 50, 60, 24);
		spinnerHeight.addChangeListener(e -> calcNewValueHeightSpin());
		panelScreen.add(spinnerHeight);

		//Spinner X
		JLabel labelX = new JLabel();
		labelX.setText(getTextLabel(textLabels, 7)); //#id 738
		labelX.setBounds(250, 80, 150, 24);
		panelScreen.add(labelX);

		spinnerX = new NumberSpinner(getDim().getSpinnerX(), 0, getDim().getWidthMax(), 1);
		spinnerX.setBounds(400, 80, 60, 24);
		spinnerX.addChangeListener(e -> calcNewValueXSpin());
		panelScreen.add(spinnerX);

		//Spinner Y
		JLabel labelY = new JLabel();
		labelY.setText(getTextLabel(textLabels, 8)); //#id 739
		labelY.setBounds(250, 110, 150, 24);
		panelScreen.add(labelY);

		spinnerY = new NumberSpinner(getDim().getSpinnerY(), 0, getDim().getHeightMax(), 1);
		spinnerY.setBounds(400, 110, 60, 24);
		spinnerY.addChangeListener(e -> calcNewValueYSpin());
		panelScreen.add(spinnerY);

		//Quality
		JLabel labelQuality = new JLabel();
		labelQuality.setText(getTextLabel(textLabels, 18)); //#id 1089
		labelQuality.setBounds(250, 140, 200, 24);
		panelScreen.add(labelQuality);

		comboQuality = new JComboBox<>();
		comboQuality.addItem(new KeyValue<>(getTextLabel(textLabels, 19), ScreenQuality.VeryHigh)); //#id 1090
		comboQuality.addItem(new KeyValue<>(getTextLabel(textLabels, 20), ScreenQuality.High)); //#id 1091
		comboQuality.addItem(new KeyValue<>(getTextLabel(textLabels, 21), ScreenQuality.Medium)); //#id 1092
		comboQuality.addItem(new KeyValue<>(getTextLabel(textLabels, 22), ScreenQuality.Low)); //#id 1093
		comboQuality.setBounds(250, 170, 130, 24);
		comboQuality.addActionListener(e -> {
				@SuppressWarnings("unchecked")
				ScreenQuality q = ((KeyValue<ScreenQuality>)comboQuality.getSelectedItem()).getValue();
				getDim().setQuality(q);
				calcRescaleFactors();
			});
		comboQuality.setSelectedIndex(core.getDefaultQuality());
		panelScreen.add(comboQuality);
		comboFPS = new JComboBox<>();
		comboFPS.addItem(new KeyValue<>("2 FPS", 2));
		comboFPS.addItem(new KeyValue<>("5 FPS", 5));
		comboFPS.addItem(new KeyValue<>("10 FPS", 10));
		comboFPS.addItem(new KeyValue<>("15 FPS", 15));
		comboFPS.addItem(new KeyValue<>("20 FPS", 20));
		comboFPS.addItem(new KeyValue<>("25 FPS", 25));
		comboFPS.addItem(new KeyValue<>("30 FPS", 30));
		comboFPS.addActionListener(e -> {
				@SuppressWarnings("unchecked")
				Integer v = ((KeyValue<Integer>)comboFPS.getSelectedItem()).getValue();
				getDim().setFps(v);
				calcRescaleFactors();
			});
		boolean fpsSelected = false;
		for (int i = 0; i < comboFPS.getItemCount(); ++i) {
			KeyValue<Integer> v = comboFPS.getItemAt(i);
			if (core.getDefaultFps() == v.value) {
				comboFPS.setSelectedIndex(i);
				fpsSelected = true;
				break;
			}
		}
		if (!fpsSelected) {
			comboFPS.addItem(new KeyValue<>(core.getDefaultFps() + " FPS", core.getDefaultFps()));
			comboFPS.setSelectedIndex(comboFPS.getItemCount() - 1);
		}
		comboFPS.setBounds(390, 170, 70, 24);
		comboFPS.setVisible(core.isShowFps());
		panelScreen.add(comboFPS);

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
		btnStartStopRecording.addActionListener(e -> {
				if (recordingStarted) {
					if (!recordingActionRequested) {
						recordingActionRequested = true;
						core.recordingStop();
					} else {
						logger.warn("Recording action is already requested");
					}
				} else {
					if (!recordingActionRequested) {
						recordingActionRequested = true;
						core.recordingStart();
					} else {
						logger.warn("Recording action is already requested");
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
		btnStartStopPublish.addActionListener(e -> {
				if (publishStarted) {
					if (!publishingActionRequested) {
						publishingActionRequested = true;
						core.publishingStop();
					} else {
						logger.warn("Publishing action is already requested");
					}
				} else {
					if (!publishingActionRequested) {
						publishingActionRequested = true;
						core.publishingStart();
					} else {
						logger.warn("Publishing action is already requested");
					}
				}
			});
		panelPublish.add(btnStartStopPublish);

		JPanel panelSecurity = new JPanel();
		panelSecurity.setLayout(null);
		panelSecurity.setBackground(Color.WHITE);
		tabbedPane.addTab(getTextLabel(textLabels, 37), null, panelSecurity, null); //#id 1598

		final JCheckBox remoteEnabled = new JCheckBox(getTextLabel(textLabels, 38)); //#id 1078
		remoteEnabled.setBackground(Color.WHITE);
		remoteEnabled.setSelected(core.isRemoteEnabled());
		remoteEnabled.setBounds(10, 10, 450, 24);
		remoteEnabled.setEnabled(core.isRemoteEnabled());
		remoteEnabled.addActionListener(e -> core.setRemoteEnabled(remoteEnabled.isSelected()));
		panelSecurity.add(remoteEnabled);

		panelStatus.setBackground(SystemColor.control);
		panelStatus.setLayout(null);
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setBounds(0, 0, 494, 20);
		lblStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));
		panelStatus.add(lblStatus);

		contentPane.setLayout(glContentPane);

		// Background Image
		//We have no logo, that is why we need no background, sebawagner 29.04.2012
	}

	private static String getTextLabel(String[] textLabels, int idx) {
		return textLabels != null && idx < textLabels.length ? textLabels[idx] : "#STAB#";
	}

	public void setSharingStatus(boolean status, boolean unlockScreen) {
		panelScreen.setEnabled(unlockScreen);
		if (status != sharingStarted) {
			sharingActionRequested = false;
		}
		logger.debug("sharingActionRequested=" + sharingActionRequested);
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
			int val = getDim().getWidthMax() - getDim().getSpinnerWidth();
			if (newX > val) {
				newX = val;
				spinnerX.setValue(newX);
				if (showWarning) {
					setStatus(reduceWidthLabel);
				}
			} else {
				getDim().setSpinnerX(newX);
				updateVScreenBounds();
			}
		} else {
			getDim().setSpinnerX(spinnerX.getValue());
		}

		calcRescaleFactors();
	}

	void calcNewValueYSpin() {
		if (doUpdateBounds) {
			int newY = spinnerY.getValue();
			int val = getDim().getHeightMax() - getDim().getSpinnerHeight();
			if (newY > val) {
				newY = val;
				spinnerY.setValue(newY);
				if (showWarning) {
					setStatus(reduceHeightLabel);
				}
			} else {
				getDim().setSpinnerY(newY);
				updateVScreenBounds();
			}
		} else {
			getDim().setSpinnerY(spinnerY.getValue());
		}

		calcRescaleFactors();
	}

	void calcNewValueWidthSpin() {
		if (doUpdateBounds) {
			int newWidth = spinnerWidth.getValue();
			int val = getDim().getWidthMax() - getDim().getSpinnerX();
			if (newWidth > val) {
				newWidth = val;
				spinnerWidth.setValue(newWidth);
				if (showWarning) {
					setStatus(reduceXLabel);
				}
			} else {
				getDim().setSpinnerWidth(newWidth);
				updateVScreenBounds();
			}
		} else {
			getDim().setSpinnerWidth(spinnerWidth.getValue());
		}

		calcRescaleFactors();
	}

	void calcNewValueHeightSpin() {
		if (doUpdateBounds) {
			int newHeight = spinnerHeight.getValue();
			int val = getDim().getHeightMax() - getDim().getSpinnerY();
			if (newHeight > val) {
				newHeight = val;
				spinnerHeight.setValue(newHeight);
				if (showWarning) {
					setStatus(reduceYLabel);
				}
			} else {
				getDim().setSpinnerHeight(newHeight);
				updateVScreenBounds();
			}
		} else {
			getDim().setSpinnerHeight(spinnerHeight.getValue());
		}

		calcRescaleFactors();
	}

	/**
	 * Needs to be always invoked after every re-scaling
	 */
	public void calcRescaleFactors() {
		logger.trace("calcRescaleFactors -- ");
		int resizeX = spinnerWidth.getValue();
		int resizeY = spinnerHeight.getValue();
		switch (getDim().getQuality()) {
			case Low:
				resizeX = (int)(2.0 * resizeX / 8);
				resizeY = (int)(2.0 * resizeY / 8);
				break;
			case Medium:
				resizeX = (int)(4.0 * resizeX / 8);
				resizeY = (int)(4.0 * resizeY / 8);
				break;
			case High:
				resizeX = (int)(6.0 * resizeX / 8);
				resizeY = (int)(6.0 * resizeY / 8);
				break;
			case VeryHigh:
			default:
				break;
		}
		int dX = resizeX % ROUND_VALUE;
		int dY = resizeY % ROUND_VALUE;
		resizeX += dX == 0 ? 0 : ROUND_VALUE - dX;
		resizeY += dY == 0 ? 0 : ROUND_VALUE - dY;
		logger.trace("resize: X: {} Y: {}", resizeX, resizeY);
		getDim().setResizeX(resizeX);
		getDim().setResizeY(resizeY);
		updateVScreenBounds();
	}

	private void setVScreenBounds(int x, int y, int width, int height) {
		leftSlider.setBounds(x + vScreenX - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		rightSlider.setBounds(x + vScreenX + width - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		upSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16, 16, 32);
		downSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16 + height, 16, 32);

		virtualScreen.setText(String.format("%s:%s", getDim().getSpinnerWidth(), getDim().getSpinnerHeight()));
		virtualScreen.setBounds(x + vScreenX, y + vScreenY, width, height);
	}

	/**
	 * update the bounds of the vScreen
	 * by using the vars from the Spinners
	 *
	 */
	public void updateVScreenBounds() {
		double ratio = ((double)getDim().getWidth()) / getDim().getWidthMax();
		int newWidth = (int)(getDim().getSpinnerWidth() * ratio);
		int newX = (int)(getDim().getSpinnerX() * ratio);

		int newHeight = (int)(getDim().getSpinnerHeight() * ratio);
		int newY = (int)(getDim().getSpinnerY() * ratio);

		setVScreenBounds(newX, newY, newWidth, newHeight);
	}

	public ScreenDimensions getDim() {
		return core.getDim();
	}
}
