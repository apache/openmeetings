package org.apache.openmeetings.web.components.admin.rooms;

import org.apache.openmeetings.persistence.beans.rooms.Rooms;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.model.CompoundPropertyModel;

public class RoomForm extends AdminBaseForm<Rooms> {

	private static final long serialVersionUID = 1L;

	public RoomForm(String id, final Rooms room) {
		super(id, new CompoundPropertyModel<Rooms>(room));
		setOutputMarkupId(true);
		
	}
}
