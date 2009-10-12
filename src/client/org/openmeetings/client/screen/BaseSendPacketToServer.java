package org.openmeetings.client.screen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.openmeetings.client.beans.ClientConnectionBean;
import org.openmeetings.client.gui.ClientStartScreen;
import org.openmeetings.client.transport.ClientTransportMinaPool;

/**
 * @author sebastianwagner
 * 
 * 
 * @deprecated
 *
 */
public class BaseSendPacketToServer {
	
	private static Logger log = Logger.getLogger(BaseSendPacketToServer.class);

	
	protected void sendPacketToServer(byte[] packetFrame) {
		try {
			
			//ClientStartScreen.instance.clientPacketMinaProcess.sendData(packetFrame);
			
			//ClientTransportMinaPool.sendMessage(packetFrame);
			
		} catch (Exception err) {
			
			//ClientStartScreen.instance.showBandwidthWarning("Could not transmit Packets to Server");
			
			log.error("[sendPacketToServer]",err);
		}
	}
	
//	/**
//	 * @param bufferImage
//	 * @param url
//	 * @param fileName
//	 */
//	protected void sendPacketToServer(byte[] packetFrame) {
//		// TODO Auto-generated method stub
//		try {
//			
//			// get a datagram socket
//			   DatagramSocket socket = new DatagramSocket();
//
//			       // send request
//			   //byte[] buf = new byte[256];
//			   
//			   InetAddress address = InetAddress.getByName(ClientConnectionBean.host);
//			   
//			   DatagramPacket packet = new DatagramPacket(packetFrame, packetFrame.length, address, ClientConnectionBean.port);
//			   
//			   socket.setSoTimeout(1000);
//			   
//			   socket.send(packet);
//
//			   byte[] buf = new byte[256];
//			       // get response
//			   packet = new DatagramPacket(buf, buf.length);
//			   socket.receive(packet);
//
//			   // display response
//			   String received = new String(packet.getData(), 0, packet.getLength());
//			   System.out.println("Date of the Moment: " + received);
//
//			   socket.close();
//			
//		} catch (Exception err) {
//			
//			ClientStartScreen.instance.showBandwidthWarning("Could not transmit Packets to Server");
//			
//			log.error("[]",err);
//		}
//	}
	
}
