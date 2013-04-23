package org.apache.openmeetings.web.components.user.dashboard;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetLocation;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class PrivateRoomsWidget extends AbstractWidget {
	private static final long serialVersionUID = 1769428980617610979L;

	public PrivateRoomsWidget(String id) {
		this();
		setId(id);
	}
	
	public PrivateRoomsWidget() {
		super();
		title = WebSession.getString(781L);
		location = new WidgetLocation(0, 1);
	}
	
	public WidgetView createView(String viewId) {
		return new PrivateRoomsWidgetView(viewId, new Model<Widget>(this));
	}
}
