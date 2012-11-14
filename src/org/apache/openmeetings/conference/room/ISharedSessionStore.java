package org.apache.openmeetings.conference.room;

import java.util.List;

import org.apache.openmeetings.persistence.beans.basic.Server;

public interface ISharedSessionStore {
	
	/**
	 * takes a list of clients from another slave of the cluster and sync its sessions to 
	 * the masters store, to have correct list of current users online and user load 
	 * at the master available that shows the current load on all slaves of the cluster.
	 * 
	 * There is zero notification when there is session/user removed or added, those events
	 * are handled by the slave, the master just needs the list to be synced.
	 * 
	 * @param server
	 * @param clients
	 */
	public void syncSlaveClientSession (Server server, List<SlaveClientDto> clients);
	
	/**
	 * Get the current sessions
	 * 
	 * @return
	 */
	public List<SlaveClientDto> getCurrentSlaveSessions();

}
