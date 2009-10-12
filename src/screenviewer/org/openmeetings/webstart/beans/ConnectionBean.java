package org.openmeetings.webstart.beans;

public class ConnectionBean {
	
	/**Connection Settings**/
	
	public static String connectionURL = "http://macbook:5080/xmlcrm/ScreenServlet";
	
	public static String SID = "3010";
	
	public static String room = "1";
	
	public static String domain = "public";
	
	public static String publicSID = "";
	
	public static String record = "";
	
	/**Intervall Settings**/
	
	public static int intervallSeconds = 1;
	
	public static String quartzScreenJobName = "grabScreen";
	
	/**Picture Quality Settings
	 * Some guidelines: 0.75 high quality
     *           		0.5  medium quality
     *           		0.25 low quality
	 * **/
	
	public static Float imgQuality = new Float(0.40);
	
	/**
	 * current loading to server
	 */
	public static boolean isloading = false;

}
