package org.apache.openmeetings.web.components.user.dashboard;

import org.apache.openmeetings.web.app.WebSession;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class PrivateRoomsWidgetDescriptor implements WidgetDescriptor {
	private static final long serialVersionUID = 389662353299319574L;

	public String getName() {
		return WebSession.getString(781L);
	}

	public String getProvider() {
		return "Apache Openmeetings";
	}

	public String getDescription() {
		return WebSession.getString(782L);
	}

	public String getWidgetClassName() {
		return PrivateRoomsWidget.class.getName();
	}

}
