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

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenDimensions {
	public static final int ROUND_VALUE = 16;

	public enum ScreenQuality {
		VeryHigh
		, High
		, Medium
		, Low
	}
	/**
	 * image recalcing value's from the virtual Screen drawer
	 */
	private int width = 200;
	private int height = 0;

	/**
	 * Values calced by the original Screen
	 */
	private double ratio;
	private final int widthMax;
	private final int heightMax;

	/**
	 * Values set by the virtualScreen
	 */
	private int spinnerWidth;
	private int spinnerHeight;
	private int spinnerX;
	private int spinnerY;

	private ScreenQuality quality = ScreenQuality.Medium;
	private int fps = 10;

	private int resizeX;
	private int resizeY;

	public ScreenDimensions() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		ratio = screenSize.getHeight() / screenSize.getWidth();
		widthMax = (int)screenSize.getWidth();
		heightMax = (int)screenSize.getHeight();
		height = (int)(width * ratio);
		spinnerX = 0;
		spinnerY = 0;
		spinnerWidth = widthMax;
		spinnerHeight = heightMax;
		resizeX = 640;
		resizeY = 400;
	}

	public int getResizeX() {
		return resizeX;
	}

	public void setResizeX(int resizeX) {
		this.resizeX = resizeX;
	}

	public int getResizeY() {
		return resizeY;
	}

	public void setResizeY(int resizeY) {
		this.resizeY = resizeY;
	}

	public int getSpinnerWidth() {
		return spinnerWidth;
	}

	public void setSpinnerWidth(int spinnerWidth) {
		this.spinnerWidth = spinnerWidth;
	}

	public int getSpinnerHeight() {
		return spinnerHeight;
	}

	public void setSpinnerHeight(int spinnerHeight) {
		this.spinnerHeight = spinnerHeight;
	}

	public int getSpinnerX() {
		return spinnerX;
	}

	public void setSpinnerX(int spinnerX) {
		this.spinnerX = spinnerX;
	}

	public int getSpinnerY() {
		return spinnerY;
	}

	public void setSpinnerY(int spinnerY) {
		this.spinnerY = spinnerY;
	}

	public int getWidth() {
		return width;
	}

	public int getWidthMax() {
		return widthMax;
	}

	public int getHeight() {
		return height;
	}

	public int getHeightMax() {
		return heightMax;
	}

	public ScreenQuality getQuality() {
		return quality;
	}

	public void setQuality(ScreenQuality quality) {
		this.quality = quality;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}
}
