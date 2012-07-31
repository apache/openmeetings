package org.openmeetings.app.backup;

import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.persistence.beans.rooms.RoomTypes;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class RoomTypeConverter extends OmConverter<RoomTypes> {
	private Roommanagement roommanagement;
	
	public RoomTypeConverter() {
		//default constructor is for export
	}
	
	public RoomTypeConverter(Roommanagement roommanagement) {
		this.roommanagement = roommanagement;
	}
	
	public RoomTypes read(InputNode node) throws Exception {
		return roommanagement.getRoomTypesById(getlongValue(node));
	}

	public void write(OutputNode node, RoomTypes value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getRoomtypes_id());
	}
}