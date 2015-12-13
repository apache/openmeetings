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

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenDimensions {
	/**
	 * image recalcing value's from the virtual Screen drawer
	 */
	public static int width = 200;
	public static int height = 0;

	/**
	 * Values calced by the original Screen
	 */

	public static double ratio = 0;

	public static int widthMax = 0;
	public static int heightMax = 0;

	/**
	 * Values set by the virtualScreen
	 */
	public static int spinnerWidth = 0;
	public static int spinnerHeight = 0;
	public static int spinnerX = 0;
	public static int spinnerY = 0;
	
	public static ScreenQuality quality = ScreenQuality.Medium;
	
	public static int resizeX = 640;
	public static int resizeY = 480;
	
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		ScreenDimensions.ratio = screenSize.getHeight() / screenSize.getWidth();
		ScreenDimensions.widthMax = (int)screenSize.getWidth();
		ScreenDimensions.heightMax = (int)screenSize.getHeight();
		ScreenDimensions.height = (int)Math.round(ScreenDimensions.width * ScreenDimensions.ratio);
		ScreenDimensions.spinnerX = 0;
		ScreenDimensions.spinnerY = 0;
		ScreenDimensions.spinnerWidth = ScreenDimensions.widthMax;
		ScreenDimensions.spinnerHeight = ScreenDimensions.heightMax;
		ScreenDimensions.resizeX = 640;
		ScreenDimensions.resizeY = 400;
	}
	
	public enum ScreenQuality {
		VeryHigh
		, High
		, Medium
		, Low
	}
}
