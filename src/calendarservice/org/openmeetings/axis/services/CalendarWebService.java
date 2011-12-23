package org.openmeetings.axis.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CalendarService contains methods to create, edit delete calendar meetings
 * 
 * @author sebawagner
 * @webservice CalendarService
 *
 */
public class CalendarWebService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			CalendarWebService.class, ScopeApplicationAdapter.webAppRootKey);
	
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	
	public List<Appointment> getAppointmentByRange(String SID, Date starttime,
			Date endtime) {
		log.debug("getAppointmentByRange : startdate - " + starttime
				+ ", enddate - " + endtime);
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
				
			LinkedList<Appointment> apps = new LinkedList<Appointment>();
			apps.add(appointmentDao.getAppointmentById(1L));
			return apps;
				
		} catch (Exception err) {
			log.error("[getAppointmentByRange]", err);
		}
		return null;
	}

}
