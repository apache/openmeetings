package org.apache.openmeetings.conference.room;

import org.apache.openmeetings.persistence.beans.basic.Server;

/**
 * 
 * Session object, is never populated to the clients, stays on the server
 * 
 * So in this object you can store meta information that the client does not
 * need to know. This is handy because the RoomClient object otherwise gets too
 * big.
 * 
 * @author sebawagner
 * 
 */
public class ClientSession {

	/**
	 * if null, the connection is handled on the master, otherwise the
	 * connection is handled via a slave of the cluster
	 */
	private Server server;

	private RoomClient roomClient;

	public ClientSession(Server server, RoomClient roomClient) {
		super();
		this.server = server;
		this.roomClient = roomClient;
	}

	/**
	 * @see ClientSession#server
	 */
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public RoomClient getRoomClient() {
		return roomClient;
	}

	public void setRoomClient(RoomClient roomClient) {
		this.roomClient = roomClient;
	}

}
