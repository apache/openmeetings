package org.openmeetings.app.backup;

import org.openmeetings.app.data.calendar.daos.AppointmentCategoryDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class AppointmentCategoryConverter  extends OmConverter<AppointmentCategory> {
	private AppointmentCategoryDaoImpl appointmentCategoryDaoImpl;
	
	public AppointmentCategoryConverter() {
		//default constructor is for export
	}
	
	public AppointmentCategoryConverter(AppointmentCategoryDaoImpl appointmentCategoryDaoImpl) {
		this.appointmentCategoryDaoImpl = appointmentCategoryDaoImpl;
	}
	
	public AppointmentCategory read(InputNode node) throws Exception {
		return appointmentCategoryDaoImpl.getAppointmentCategoryById(getlongValue(node));
	}

	public void write(OutputNode node, AppointmentCategory value)
			throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getCategoryId());
	}
}
