package org.openmeetings.client.beans;

import java.util.Date;

public class ClientConnectionBean {
	
	/**Connection Settings**/
	
	public static boolean isViewer = false;
	
	public static String host = "127.0.0.1";
	
	public static int port = 4445;
	
	public static String connectionURL = "http://macbook:5080/xmlcrm/ScreenServlet";
	
	public static String SID = "3010";
	
	public static String room = "1";
	
	public static String domain = "public";
	
	public static String publicSID = "test";
	
	public static String record = "";
	
	public static int mode = 3;
	
	public static int maxPayLoadSize = 952;
	
	public static Date startDate;
	
	public static boolean sendMousePosition = true;
	
	/**Intervall Settings**/
	
	public static int intervallSeconds = 1;
	
	//public static int tileWidth = (512*2) / 3;
	//public static int tileHeight = (512*2) / 3;
	public static int tileWidth = 512;
	public static int tileHeight = 512;
	
	
	public static String quartzScreenJobName = "grabScreen";
	
	/**Picture Quality Settings
	 * Some guidelines: 0.75 high quality
     *           		0.5  medium quality
     *           		0.25 low quality
	 * **/
	
	public static Float imgQuality = new Float(0.68);
	
	public static Float imgQualityDefault = new Float(0.6);
	
	/**
	 * current loading to server
	 */
	public static boolean isloading = false;
	
	public static int frameNumber = 0;
	
	public static synchronized int getFrameNumber() {
		
		//The max value is a Byte number, that is 2147483647, after that the
		//sequence has to start from zero again
		if (frameNumber >= 2147483647) {
			frameNumber = 0;
		}
		
		return frameNumber++;
	}

}
