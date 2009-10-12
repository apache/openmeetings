package org.openmeetings.client.beans;

import java.awt.Robot;

public class ClientVirtualScreenBean {
	
	
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
	
	public static boolean isActive = true;
	
	public static Robot robot = null;
}
