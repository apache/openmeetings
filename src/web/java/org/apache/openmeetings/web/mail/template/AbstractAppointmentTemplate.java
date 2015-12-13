package org.apache.openmeetings.web.mail.template;

import java.util.TimeZone;

import org.apache.openmeetings.db.entity.calendar.Appointment;

public abstract class AbstractAppointmentTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;
	protected Appointment a;
	protected TimeZone tz;

	public AbstractAppointmentTemplate(Long langId, Appointment a, TimeZone tz) {
		super(langId);
		this.a = a;
		this.tz = tz;
	}

	public String getEmail() {
		return renderPanel(this).toString();
	}

	public abstract String getSubject();
}
