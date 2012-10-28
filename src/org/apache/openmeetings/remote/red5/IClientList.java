package org.apache.openmeetings.remote.red5;

import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.rooms.RoomClient;

public interface IClientList {

	/**
	 * Get current clients and extends the room client with its potential 
	 * audio/video client and settings
	 * 
	 * @param room_id
	 * @return
	 */
	public abstract List<RoomClient> getRoomClients(Long room_id);

	public abstract RoomClient addClientListItem(String streamId,
			String scopeName, Integer remotePort, String remoteAddress,
			String swfUrl, boolean isAVClient);

	public abstract Collection<RoomClient> getAllClients();

	public abstract RoomClient getClientByStreamId(String streamId);

	/**
	 * Additionally checks if the client receives sync events
	 * 
	 * Sync events will no be broadcasted to:
	 * - Screensharing users
	 * - Audio/Video connections only
	 * 
	 * @param streamId
	 * @return
	 */
	public abstract RoomClient getSyncClientByStreamId(String streamId);

	public abstract RoomClient getClientByPublicSID(String publicSID,
			boolean isAVClient);

	public abstract RoomClient getClientByUserId(Long userId);

	/**
	 * Update the session object of the audio/video-connection and additionally swap the 
	 * values to the session object of the user that holds the full session object
	 * @param streamId
	 * @param rcm
	 * @return
	 */
	public abstract Boolean updateAVClientByStreamId(String streamId,
			RoomClient rcm);

	public abstract Boolean updateClientByStreamId(String streamId,
			RoomClient rcm);

	public abstract Boolean removeClient(String streamId);

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 * 
	 * @return
	 */
	public abstract List<RoomClient> getClientListByRoom(Long room_id);

	public abstract List<RoomClient> getClientListByRoomAll(Long room_id);

	/**
	 * get the current Moderator in this room
	 * 
	 * @param roomname
	 * @return
	 */
	public abstract List<RoomClient> getCurrentModeratorByRoom(Long room_id);

	//FIXME not sorted
	public abstract SearchResult<RoomClient> getListByStartAndMax(int start,
			int max, String orderby, boolean asc);

	public abstract void removeAllClients();

	public abstract long getRecordingCount(long roomId);

	public abstract long getPublisingCount(long roomId);

}