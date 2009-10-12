package org.openmeetings.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class TestSender extends TestCase {

	private static Logger log = Logger.getLogger(TestSender.class);
	
	
	public TestSender(String testname){
		super(testname);
	}
	
	public void testTestSocket(){
		try {
			
	       // get a datagram socket
		   DatagramSocket socket = new DatagramSocket();

		       // send request
		   byte[] buf = new byte[256];
		   InetAddress address = InetAddress.getByName("192.168.0.41");
		   DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
		   
		   socket.setSoTimeout(1000);
		   
		   socket.send(packet);

		       // get response
		   packet = new DatagramPacket(buf, buf.length);
		   socket.receive(packet);

		   // display response
		   String received = new String(packet.getData(), 0, packet.getLength());
		   System.out.println("Date of the Moment: " + received);

		   socket.close();
		   
		} catch (Exception err) {
			log.error("[TestSocket] ",err);
		}
		
	}
	

}
