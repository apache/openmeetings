package org.openmeetings.utils.math;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TimezoneUtil {

	private static final Logger log = Red5LoggerFactory.getLogger(
			TimezoneUtil.class, ScopeApplicationAdapter.webAppRootKey);

	public static void main(String... args) {

		TimeZone testZoneT = TimeZone.getTimeZone("Etc/GMT+4");

		System.out.println("testZoneT OFFSET "
				+ testZoneT.getOffset(new Date().getTime()));

		TimeZone testZone = TimeZone.getTimeZone("America/New_York");

		System.out.println("testZoneT OFFSE2T "
				+ testZone.getOffset(new Date().getTime()));

		Calendar cal = reCalcDateToTimezonCalendarObj(new Date(), testZone);

		System.out.println(cal.getTime());

	}

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;

	/**
	 * Returns the timezone based on the user profile, if not return the
	 * timezone from the server
	 * 
	 * @param user
	 * @return
	 */
	public TimeZone getTimezoneByUser(Users user) {

		if (user != null && user.getOmTimeZone() != null) {

			TimeZone timeZone = TimeZone.getTimeZone(user.getOmTimeZone()
					.getIcal());

			if (timeZone != null) {
				return timeZone;
			}

		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone.getTimeZone(omTimeZone
					.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}

	/**
	 * Return the timezone based on our internal jName
	 * 
	 * @param jName
	 * @return
	 */
	public TimeZone getTimezoneByInternalJName(String jName) {

		OmTimeZone omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jName);

		TimeZone timeZone = TimeZone.getTimeZone(omTimeZone.getIcal());

		if (timeZone != null) {
			return timeZone;
		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZoneDefault = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone
					.getTimeZone(omTimeZoneDefault.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}

	/**
	 * Return the timezone based Id from omTimeZone table
	 * 
	 * @param jName
	 * @return
	 */
	public TimeZone getTimezoneByOmTimeZoneId(Long omtimezoneId) {

		OmTimeZone omTimeZone = omTimeZoneDaoImpl
				.getOmTimeZoneById(omtimezoneId);

		TimeZone timeZone = TimeZone.getTimeZone(omTimeZone.getIcal());

		if (timeZone != null) {
			return timeZone;
		}

		// if user has not time zone get one from the server configuration

		Configuration conf = cfgManagement.getConfKey(3L, "default.timezone");

		if (conf != null) {

			OmTimeZone omTimeZoneDefault = omTimeZoneDaoImpl.getOmTimeZone(conf
					.getConf_value());

			TimeZone timeZoneByOmTimeZone = TimeZone
					.getTimeZone(omTimeZoneDefault.getIcal());

			if (timeZoneByOmTimeZone != null) {
				return timeZoneByOmTimeZone;
			}

		}

		// If everything fails take the servers default one
		log.error("There is no correct time zone set in the configuration of OpenMeetings for the key default.timezone or key is missing in table, using default locale!");
		return TimeZone.getDefault();
	}

	/**
	 * We ignore the fact that a Date Object is always in UTC internally and
	 * treat it as if it contains only dd.mm.yyyy HH:mm:ss. We need to do this
	 * cause we cannot trust the Date Object send from the client. We have the
	 * timeZone information additional to the Date, so we need to transform it
	 * now to a Calendar Object.
	 * 
	 * The client for example send 01.01.2011 12:30:00 but he has the timezone
	 * PDT while the server stands in CET. The result is the the server will
	 * receive the time 01.01.2011 12:30:00 and adds CET to it, so we need to
	 * manipulate this time back to its original time.
	 * 
	 * @param dateTime
	 * @param timezone
	 * @return
	 */
	public static Calendar reCalcDateToTimezonCalendarObj(Date dateTime,
			TimeZone timezone) {

		Calendar calOrig = Calendar.getInstance();
		calOrig.setTime(dateTime);

		Calendar cal = Calendar.getInstance(timezone);
		cal.set(Calendar.YEAR, calOrig.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calOrig.get(Calendar.MONTH));
		cal.set(Calendar.DATE, calOrig.get(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY, calOrig.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calOrig.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calOrig.get(Calendar.SECOND));

		return cal;
	}

	public static long _getOffset(TimeZone timezone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timezone);
		return cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
	}

}
