package de.medint.rtpsharer.util;

import de.medint.rtpsharer.main.Streamer;

/**
 * @author sebastianwagner
 *
 */
public class ConfigUtil {

	/** Destination Address */
	public static String destinationAddress = "127.0.0.1";  //  @jve:decl-index=0:
	
	/** Destination RTP Port */
	public static Integer destinationPort = 22224;  //  @jve:decl-index=0:
	
	/** Source RTP Port */
	public static Integer sourcePort = 22227;  //  @jve:decl-index=0:
	
	/** This is the IP Adress of the Current User **/
	public static String sharerIP = "127.0.0.1";
	
	/** FrameRate */
	public static int frameRate = 20;
	
	/** TargetVideoheight */
	public static int videoHeight = 768;
	
	/** TargerVideoWidth*/
	public static int videoWidth = 1024;
	
	/** Quality */
	public static float quality = 1;
	
	/** Connected */
	public static boolean connection = false;
	
	/** Session ID OM => Don't use this, the value may change and also it should be never 
	 * transfered anywhere it should strictly stay on the server - swagner */
	public static String SID = null;
	
	/** Public Session ID OM that is the value to identify a user, that can be known 
	 * by everybody */
	public static String PUBLIC_SID = null;
	
	public static String RECORDER = null;
	
	/** Room id OM */
	public static String ROOM = null;
	
	/** Servlet URL for Function calls */
	public static String rtmphostlocal;
	public static String red5httpport;
	public static String webAppRootKey;
	
}
