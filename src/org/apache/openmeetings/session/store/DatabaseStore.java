package org.apache.openmeetings.session.store;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.rooms.Client;

public class DatabaseStore implements IClientPersistenceStore {

	public void put(Server server, String streamId, Client rcl) {
		// TODO Auto-generated method stub
		
	}

	public boolean containsKey(Server server, String streamId) {
		// TODO Auto-generated method stub
		return false;
	}

	public Client get(Server server, String streamId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getClientsByPublicSID(Server server, String publicSID) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Long, List<Client>> getClientsByPublicSID(String publicSID) {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashMap<String, Client> getClientsByServer(Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Client> getClientsByUserId(Server server, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashMap<String, Client> getClientsByRoomId(Server server,
			Long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(Server server, String streamId) {
		// TODO Auto-generated method stub
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int sizeByServer(Server server) {
		// TODO Auto-generated method stub
		return 0;
	}

	public LinkedHashMap<Long, LinkedHashMap<String, Client>> getClientsByServerAndRoom(
			Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashMap<Long, LinkedHashMap<String, Client>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDebugInformation(List<DEBUG_DETAILS> detailLevel) {
		// TODO Auto-generated method stub
		return null;
	}

}
