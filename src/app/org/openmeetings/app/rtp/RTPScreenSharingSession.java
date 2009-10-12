package org.openmeetings.app.rtp;

import java.util.HashMap;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;
/**
 * represents a ScreenSharingSession within Conference
 * @author o.becherer
 *
 */
public class RTPScreenSharingSession {
	
	/** User sharing his Desktop */
	private Users sharingUser = null;
	
	/** public SID of the User that streams **/
	private String publicSID;
	
	/** Ip Address of sharing user */
	private String sharingIpAddress = "127.0.0.1";
	
	/** IP of RED5 host */
	private String red5Host = "127.0.0.1";
	
	/** RTP Port incoming(Port the Shareres Client sends on)*/
	private  int incomingRTPPort = 0;
	
	/** RTP Port outgoing (Port on which the RTP Stream is spread to single clients*/
	private  int outgoingRTPPort = 0;
	
	/** Stream Width */
	private  int streamWidth = 1024;
	
	/** StreamHeight */
	private  int streamHeight = 768;
	
	/** ConferenceRoom */
	private Rooms room = null;
	
	/** JpegQuality */
	private  float jpegQuality = 1f;
	
	/** Thread running?*/
	private boolean running = false;
	
	/** Users, that are consuming the Sharing Stream */
	private HashMap<String, Integer> viewers = new HashMap<String, Integer>();
	
	/** Thread */
	private RTPStreamReceiver receiver;
	
	public Users getSharingUser() {
		return sharingUser;
	}
	
	public void startReceiver() throws Exception{
		receiver = new RTPStreamReceiver(this);
		
	}
	
	/**
	 * Adding a new Viewer (coming late to conference...)
	 * @param ipAddress
	 * @param port
	 * @throws Exception
	 */
	public void addNewViewer(String ipAddress, int port) throws Exception{
		if(receiver == null)
			throw new Exception("receiver thread is null");
		
		receiver.addNewViewer(ipAddress, port);
	}

	public void setSharingUser(Users sharingUser) {
		this.sharingUser = sharingUser;
	}

	public String getSharingIpAddress() {
		return sharingIpAddress;
	}

	public void setSharingIpAddress(String sharingIpAddress) {
		this.sharingIpAddress = sharingIpAddress;
	}

	public int getIncomingRTPPort() {
		return incomingRTPPort;
	}

	public void setIncomingRTPPort(int incomingRTPPort) {
		this.incomingRTPPort = incomingRTPPort;
	}

	public int getOutgoingRTPPort() {
		return outgoingRTPPort;
	}

	public void setOutgoingRTPPort(int outgoingRTPPort) {
		this.outgoingRTPPort = outgoingRTPPort;
	}

	public int getStreamWidth() {
		return streamWidth;
	}

	public void setStreamWidth(int streamWidth) {
		this.streamWidth = streamWidth;
	}

	public int getStreamHeight() {
		return streamHeight;
	}

	public void setStreamHeight(int streamHeight) {
		this.streamHeight = streamHeight;
	}

	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	public float getJpegQuality() {
		return jpegQuality;
	}

	public void setJpegQuality(float jpegQuality) {
		this.jpegQuality = jpegQuality;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public HashMap<String, Integer> getViewers() {
		return viewers;
	}

	public void setViewers(HashMap<String, Integer> viewers) {
		this.viewers = viewers;
	}

	public RTPStreamReceiver getReceiver() {
		return receiver;
	}

	public void setReceiver(RTPStreamReceiver receiver) {
		this.receiver = receiver;
	}

	public String getRed5Host() {
		return red5Host;
	}

	public void setRed5Host(String red5Host) {
		this.red5Host = red5Host;
	}

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}
	
}
