package org.openmeetings.app.data.calendar.beans;

import java.util.ArrayList;
import java.util.List;

public class Week {
	
	private List<Day> days = new ArrayList(7);

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}
	

}
