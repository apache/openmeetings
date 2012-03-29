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


public class VirtualScreenBean {
	/**
	 * image recalcing value's from the virtual Screen drawer
	 */
	public static int vScreenWidth = 200;
	public static int vScreenHeight = 0;

	/**
	 * Values calced by the original Screen
	 */

	public static double screenratio = 0;

	public static int screenWidthMax = 0;

	public static int screenHeightMax = 0;

	/**
	 * Values set by the virtualScreen
	 */
	public static int vScreenSpinnerWidth = 0;
	public static int vScreenSpinnerHeight = 0;
	public static int vScreenSpinnerX = 0;
	public static int vScreenSpinnerY = 0;
	
	public static ScreenQuality screenQuality = ScreenQuality.Medium;
	
	public static int vScreenResizeX = 480;
	public static int vScreenResizeY = 360;
	
	public enum ScreenQuality {
		VeryHigh
		, High
		, Medium
		, Low
	}
}
