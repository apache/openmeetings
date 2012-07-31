package org.openmeetings.app.backup;

import java.util.Map;

import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class AppointmentConverter extends OmConverter<Appointment> {
	private AppointmentDaoImpl appointmentDao;
	private Map<Long, Long> idMap;
	
	public AppointmentConverter() {
		//default constructor is for export
	}
	
	public AppointmentConverter(AppointmentDaoImpl appointmentDao, Map<Long, Long> idMap) {
		this.appointmentDao = appointmentDao;
		this.idMap = idMap;
	}
	
	public Appointment read(InputNode node) throws Exception {
		long oldOrgId = getlongValue(node);
		long newId = idMap.containsKey(oldOrgId) ? idMap.get(oldOrgId) : oldOrgId;
		
		return appointmentDao.getAppointmentByIdBackup(newId);
	}

	public void write(OutputNode node, Appointment value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getAppointmentId());
	}
}