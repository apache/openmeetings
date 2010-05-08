package org.red5.screen.webstart.tgui;

import java.awt.Robot;

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
	public static int vScreenResizeX = 480;
	public static int vScreenResizeY = 360;
	

	public static Robot robot = null;

	public static Float imgQuality = new Float(0.40);
}
