package org.apache.openmeetings.web.components.user.dashboard;

import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.user.rooms.RoomsPanel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class PrivateRoomsWidgetView extends WidgetView {
	private static final long serialVersionUID = 6950427893821991173L;

	public PrivateRoomsWidgetView(String id, Model<Widget> model) {
		super(id, model);
		
		//FIXME 2 !!!! fake rooms;
		//FIXME need to be generalized with RoomsSelectorPanel
		add(new RoomsPanel("rooms", Application.getBean(RoomDao.class).getAppointedRoomsByUser(WebSession.getUserId())));
	}
}
