package org.apache.openmeetings.conference.room;

import org.apache.openmeetings.persistence.beans.basic.Server;

/**
 * Utils generate keys and hashs for the {@link ClientSession} and
 * {@link RoomClient}
 * 
 * @author swagner
 * 
 */
public class ClientSessionUtil {

	/**
	 * produce a unique key based on the stream-id and server
	 * 
	 * @param server
	 * @param streamId
	 * @return
	 */
	public static String getClientSessionKey(Server server, String streamId) {
		if (server == null) {
			return "__" + streamId;
		}
		return "_" + server.getId() + "_" + streamId;
	}

}
