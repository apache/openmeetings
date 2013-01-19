package org.apache.openmeetings.session;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.basic.Server;

public class ClientDatabaseStore implements ISessionStore {

	public IClientSession addClientListItem(String streamId, String scopeName,
			Integer remotePort, String remoteAddress, String swfUrl,
			boolean isAVClient) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Client> getAllClients() {
		// TODO Auto-generated method stub
		return null;
	}

	public Client getClientByStreamId(String streamId, Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public IClientSession getSyncClientByStreamId(String streamId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Client getClientByPublicSID(String publicSID,
			boolean isAVClient, Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientSessionInfo getClientByPublicSIDAnyServer(String publicSID,
			boolean isAVClient) {
		// TODO Auto-generated method stub
		return null;
	}

	public IClientSession getClientByUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean updateAVClientByStreamId(String streamId, Client rcm) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean updateClientByStreamId(String streamId, Client rcm,
			boolean updateRoomCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean removeClient(String streamId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getClientListByRoom(Long room_id, Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Client> getClientListByRoomAll(Long room_id,
			Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getCurrentModeratorByRoom(Long room_id) {
		// TODO Auto-generated method stub
		return null;
	}

	public SearchResult<ClientSession> getListByStartAndMax(int start, int max,
			String orderby, boolean asc) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getRecordingCount(long roomId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getPublishingCount(long roomId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<Long> getActiveRoomIdsByServer(Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSessionStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
