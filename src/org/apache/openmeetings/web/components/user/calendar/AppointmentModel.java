package org.apache.openmeetings.web.components.user.calendar;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;

import com.googlecode.wicket.jquery.ui.calendar.CalendarEvent;
import com.googlecode.wicket.jquery.ui.calendar.CalendarModel;
import com.googlecode.wicket.jquery.ui.calendar.ICalendarVisitor;

public class AppointmentModel extends CalendarModel implements ICalendarVisitor {
	private static final long serialVersionUID = -8707880381422490413L;

	public void visit(CalendarEvent event) {
		//every event can be customized
	}

	@Override
	protected List<? extends CalendarEvent> load() {
		List<CalendarEvent> list = new ArrayList<CalendarEvent>();
		for (Appointment a : Application.getBean(AppointmentDao.class).getAppointmentsByRange(WebSession.getUserId(), this.getStart(), this.getEnd())) {
			CalendarEvent c = new CalendarEvent(a.getAppointmentId().intValue(), a.getAppointmentName(), a.getAppointmentStarttime(), a.getAppointmentEndtime());
			c.setAllDay(false);
			//FIXME desc c.set
			list.add(c);
		}
		return list;
	}

}
