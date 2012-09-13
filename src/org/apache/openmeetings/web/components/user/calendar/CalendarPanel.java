package org.apache.openmeetings.web.components.user.calendar;

import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class CalendarPanel extends UserPanel {
	private static final long serialVersionUID = -6536379497642291437L;

	public CalendarPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		WebMarkupContainer calendar = new WebMarkupContainer("calendar");
		calendar.setOutputMarkupId(true);
		calendar.setMarkupId("calendar");
		add(calendar);
	}

}
