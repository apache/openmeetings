package org.openmeetings.app.data.calendar.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Day {
	
	private Date tDate;
	private List<AppointmentDTO> appointments = new ArrayList<AppointmentDTO>();

	public Day(Date tDate) {
		super();
		this.tDate = tDate;
	}

	public Date gettDate() {
		return tDate;
	}

	public void settDate(Date tDate) {
		this.tDate = tDate;
	}


	public List<AppointmentDTO> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<AppointmentDTO> appointments) {
		this.appointments = appointments;
	}
	
}
