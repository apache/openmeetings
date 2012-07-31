package org.openmeetings.app.backup;

import java.util.Map;

import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class RoomConverter extends OmConverter<Rooms> {
	private Roommanagement roommanagement;
	private Map<Long, Long> idMap;
	
	public RoomConverter() {
		//default constructor is for export
	}
	
	public RoomConverter(Roommanagement roommanagement, Map<Long, Long> idMap) {
		this.roommanagement = roommanagement;
		this.idMap = idMap;
	}
	
	public Rooms read(InputNode node) throws Exception {
		long oldOrgId = getlongValue(node);
		long newId = idMap.containsKey(oldOrgId) ? idMap.get(oldOrgId) : oldOrgId;

		return roommanagement.getRoomById(newId);
	}

	public void write(OutputNode node, Rooms value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getRooms_id());
	}
}