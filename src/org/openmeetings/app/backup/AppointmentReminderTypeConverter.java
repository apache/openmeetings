package org.openmeetings.app.backup;

import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class AppointmentReminderTypeConverter extends OmConverter<AppointmentReminderTyps> {
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	
	public AppointmentReminderTypeConverter() {
		//default constructor is for export
	}
	
	public AppointmentReminderTypeConverter(AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl) {
		this.appointmentReminderTypDaoImpl = appointmentReminderTypDaoImpl;
	}
	
	public AppointmentReminderTyps read(InputNode node) throws Exception {
		return appointmentReminderTypDaoImpl.getAppointmentReminderTypById(getlongValue(node));
	}

	public void write(OutputNode node, AppointmentReminderTyps value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getTypId());
	}
}