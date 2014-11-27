package org.apache.openmeetings.service.mail.template;

import java.util.TimeZone;

import org.apache.openmeetings.db.entity.calendar.Appointment;

public abstract class AbstractAppointmentTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;
	protected long langId;
	protected Appointment a;
	protected TimeZone tz;

	public AbstractAppointmentTemplate(Long langId, Appointment a, TimeZone tz, String invitorName) {
		super(TemplatePage.COMP_ID);
		this.langId = langId == null ? 1 : langId;
		this.a = a;
		this.tz = tz;
		ensureApplication(langId);
	}

	public String getEmail() {
		return renderPanel(this).toString();
	}

	public abstract String getSubject();
}
