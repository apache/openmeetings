package de.medint.rtpsharer.main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;

import de.medint.rtpsharer.util.ConfigUtil;


/**
 * 
 * @author o.becherer
 *
 */
public class ServletFunctions {
	
	/**
	 * 
	 * @param servletUrl
	 * @param jpegQuality
	 * @param height
	 * @param width
	 */

	public static void sendStartSignal() throws Exception{

		
		
		String servletUrl = "http://"+ConfigUtil.rtmphostlocal
								+":"+ConfigUtil.red5httpport
								+"/"+ConfigUtil.webAppRootKey
								+"/RTPMethodServlet";
		

		String url = servletUrl + "?method=streamer_start&room=" + ConfigUtil.ROOM 
						+ "&height=" + ConfigUtil.videoHeight 
						+ "&width=" + ConfigUtil.videoWidth 
						+ "&quality=" + ConfigUtil.quality 
						+ "&sid=" + ConfigUtil.SID 
						+ "&sharerIP=" + ConfigUtil.sharerIP
						+ "&publicSID=" + ConfigUtil.PUBLIC_SID
						+ "&host=" + ConfigUtil.rtmphostlocal
						+ "&rtpport=" + ConfigUtil.destinationPort;
			

		
		URLConnection c = getConnection(url);
		
		// open a stream which can write to the url
		DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

		dstream.writeUTF("ServletCall");
		dstream.flush();
		dstream.close();
		
		 // read the output from the URL
		DataInputStream in = new DataInputStream(new BufferedInputStream(c.getInputStream()));
		
		String sIn = in.readLine();
		while (sIn != null) {
			if (sIn != null) {
				System.out.println(sIn);
			}
			sIn += in.readLine();
		}
		
		c = null;
		
	}
	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param servletUrl
	 * @param jpegQuality
	 * @param height
	 * @param width
	 */
	//---------------------------------------------------------------------------------------------------------
	public static void sendStopSignal() throws Exception{
		
		String servletUrl = "http://"+ConfigUtil.rtmphostlocal
								+":"+ConfigUtil.red5httpport
								+"/"+ConfigUtil.webAppRootKey
								+"/RTPMethodServlet";
		
		//Building ServletUrl
		String url = servletUrl + "?method=streamer_stop" +
											"&room=" + ConfigUtil.ROOM + 
											"&sid=" + ConfigUtil.SID +
											"&publicSID=" + ConfigUtil.PUBLIC_SID;
		
		URLConnection c = getConnection(url);
		
		// open a stream which can write to the url
		DataOutputStream dstream = new DataOutputStream(c.getOutputStream());

		dstream.writeUTF("ServletCall");
		dstream.flush();
		dstream.close();
		
		 // read the output from the URL
		DataInputStream in = new DataInputStream(new BufferedInputStream(c.getInputStream()));
		
		String sIn = in.readLine();
		while (sIn != null) {
			if (sIn != null) {
				System.out.println(sIn);
			}
			sIn += in.readLine();
		}
		
		c = null;
		
	}
	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Opening URLConnection
	 */
	//---------------------------------------------------------------------------------------------------------
	private static URLConnection getConnection(String servletUrl) throws Exception{
		
		URL u = new URL(servletUrl);
		URLConnection c = u.openConnection();

		// post multipart data
		c.setDoOutput(true);
		c.setDoInput(true);
		c.setUseCaches(false);
		
		return c;

	}
	//---------------------------------------------------------------------------------------------------------
	
}
