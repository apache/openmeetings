package org.openmeetings.app.data.calendar.daos;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.AppointmentCategory;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.math.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private AppointmentCategoryDaoImpl appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private Invitationmanagement invitationManagement;
	@Autowired
	private MeetingMemberLogic meetingMemberLogic;
	@Autowired
	private TimezoneUtil timezoneUtil;

	/*
	 * insert, update, delete, select
	 */

	/**
	 * @author o.becherer Retrievment of Appointment for room
	 */
	// -----------------------------------------------------------------------------------------------
	public Appointment getAppointmentByRoom(Long room_id) throws Exception {
		log.debug("AppointMentDaoImpl.getAppointmentByRoom");

		String hql = "select a from Appointment a "
				+ "WHERE a.deleted <> :deleted "
				+ "AND a.room.rooms_id = :room_id ";

		TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
		query.setParameter("deleted", "true");
		query.setParameter("room_id", room_id);

		List<Appointment> appoint = query.getResultList();

		if (appoint.size() > 0) {
			return appoint.get(0);
		}

		return null;
	}

	// -----------------------------------------------------------------------------------------------

	public Appointment getAppointmentById(Long appointmentId) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.appointmentId = :appointmentId ";

			TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
			query.setParameter("deleted", "true");
			query.setParameter("appointmentId", appointmentId);

			Appointment appoint = null;
			try {
				appoint = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return appoint;
		} catch (Exception ex2) {
			log.error("[getAppointmentById]: ", ex2);
		}
		return null;
	}

	public Appointment getAppointmentByIdBackup(Long appointmentId) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.appointmentId = :appointmentId ";

			TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
			query.setParameter("appointmentId", appointmentId);

			Appointment appoint = null;
			try {
				appoint = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appoint;
		} catch (Exception ex2) {
			log.error("[getAppointmentById]: ", ex2);
		}
		return null;
	}

	public List<Appointment> getAppointments() {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted ";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("deleted", "true");

			List<Appointment> appointList = query.getResultList();

			for (Appointment appointment : appointList) {

				appointment.setMeetingMember(meetingMemberDao
						.getMeetingMemberByAppointmentId(appointment
								.getAppointmentId()));

			}

			return appointList;

		} catch (Exception ex2) {
			log.error("[getAppointmentById]: ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param appointmentName
	 * @param userId
	 * @param appointmentLocation
	 * @param appointmentDescription
	 * @param appointmentstart
	 * @param appointmentend
	 * @param isDaily
	 * @param isWeekly
	 * @param isMonthly
	 * @param isYearly
	 * @param categoryId
	 * @param remind
	 * @param room
	 * @return
	 */
	// ----------------------------------------------------------------------------------------------------------------------------
	public Long addAppointment(String appointmentName, Long userId,
			String appointmentLocation, String appointmentDescription,
			Date appointmentstart, Date appointmentend, Boolean isDaily,
			Boolean isWeekly, Boolean isMonthly, Boolean isYearly,
			Long categoryId, Long remind, Rooms room, Long language_id,
			Boolean isPasswordProtected, String password,
			Boolean isConnectedEvent, String jNameTimeZone) {
		try {

			Appointment ap = new Appointment();

			ap.setAppointmentName(appointmentName);
			ap.setAppointmentLocation(appointmentLocation);

			log.debug("addAppointment appointmentstart :1: "
					+ CalendarPatterns
							.getDateWithTimeByMiliSecondsWithZone(appointmentstart));
			log.debug("addAppointment appointmentend :1: "
					+ CalendarPatterns
							.getDateWithTimeByMiliSecondsWithZone(appointmentend));

			ap.setAppointmentStarttime(appointmentstart);
			ap.setAppointmentEndtime(appointmentend);
			ap.setAppointmentDescription(appointmentDescription);
			ap.setRemind(appointmentReminderTypDaoImpl
					.getAppointmentReminderTypById(remind));
			ap.setStarttime(new Date());
			ap.setIsReminderEmailSend(false);
			ap.setDeleted("false");
			ap.setIsDaily(isDaily);
			ap.setIsWeekly(isWeekly);
			ap.setIsMonthly(isMonthly);
			ap.setIsYearly(isYearly);
			ap.setLanguage_id(language_id);
			ap.setIsPasswordProtected(isPasswordProtected);
			ap.setPassword(password);
			ap.setUserId(usersDao.getUser(userId));
			ap.setAppointmentCategory(appointmentCategoryDaoImpl
					.getAppointmentCategoryById(categoryId));
			ap.setRoom(room);
			ap.setIsConnectedEvent(isConnectedEvent);

			ap = em.merge(ap);

			return ap.getAppointmentId();
		} catch (Exception ex2) {
			log.error("[addAppointment]: ", ex2);
		}
		return null;
	}

	public Long addAppointmentObj(Appointment ap) {
		try {

			ap.setStarttime(new Date());

			ap = em.merge(ap);

			return ap.getAppointmentId();
		} catch (Exception ex2) {
			log.error("[addAppointmentObj]: ", ex2);
		}
		return null;
	}

	// ----------------------------------------------------------------------------------------------------------------------------

	public Long updateAppointment(Appointment appointment) {
		if (appointment.getAppointmentId() > 0) {
			try {
				if (appointment.getAppointmentId() == null) {
					em.persist(appointment);
				} else {
					if (!em.contains(appointment)) {
						em.merge(appointment);
					}
				}
				return appointment.getAppointmentId();
			} catch (Exception ex2) {
				log.error("[updateAppointment] ", ex2);
			}
		} else {
			log.error("[updateAppointment] " + "Error: No AppointmentId given");
		}
		return null;
	}

	public List<Appointment> getAppointmentsByRoomId(Long roomId) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.room.rooms_id = :roomId ";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("roomId", roomId);
			List<Appointment> ll = query.getResultList();

			return ll;
		} catch (Exception e) {
			log.error("[getAppointmentsByRoomId]", e);
		}
		return null;
	}

	private void updateConnectedEventsTimeOnly(Appointment ap,
			Date appointmentstart, Date appointmentend) {
		try {

			if (ap.getRoom() == null) {
				return;
			}

			List<Appointment> appointments = this.getAppointmentsByRoomId(ap
					.getRoom().getRooms_id());

			for (Appointment appointment : appointments) {

				if (!ap.getAppointmentId().equals(
						appointment.getAppointmentId())) {

					ap.setAppointmentStarttime(appointmentstart);
					ap.setAppointmentEndtime(appointmentend);
					ap.setUpdatetime(new Date());
					if (ap.getAppointmentId() == null) {
						em.persist(ap);
					} else {
						if (!em.contains(ap)) {
							em.merge(ap);
						}
					}

				}

			}

		} catch (Exception err) {
			log.error("[updateConnectedEvents]", err);
		}
	}

	private void updateConnectedEvents(Appointment ap, String appointmentName,
			String appointmentDescription, Date appointmentstart,
			Date appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly,
			AppointmentCategory appointmentCategory,
			AppointmentReminderTyps appointmentReminderTyps, @SuppressWarnings("rawtypes") List mmClient,
			Long users_id, String baseUrl, Long language_id,
			Boolean isPasswordProtected, String password) {
		try {

			if (ap.getRoom() == null) {
				return;
			}

			List<Appointment> appointments = this.getAppointmentsByRoomId(ap
					.getRoom().getRooms_id());

			for (Appointment appointment : appointments) {

				if (!ap.getAppointmentId().equals(
						appointment.getAppointmentId())) {

					appointment.setAppointmentName(appointmentName);
					appointment.setAppointmentStarttime(appointmentstart);
					appointment.setAppointmentEndtime(appointmentend);
					appointment
							.setAppointmentDescription(appointmentDescription);
					appointment.setUpdatetime(new Date());
					appointment.setRemind(appointmentReminderTyps);
					appointment.setIsDaily(isDaily);
					appointment.setIsWeekly(isWeekly);
					appointment.setIsMonthly(isMonthly);
					appointment.setIsYearly(isYearly);
					appointment.setLanguage_id(language_id);
					appointment.setIsPasswordProtected(isPasswordProtected);
					appointment.setPassword(password);
					// ap.setUserId(usersDao.getUser(userId));
					appointment.setAppointmentCategory(appointmentCategory);

					if (appointment.getAppointmentId() == null) {
						em.persist(appointment);
					} else {
						if (!em.contains(appointment)) {
							em.merge(appointment);
						}
					}

				}

			}

		} catch (Exception err) {
			log.error("[updateConnectedEvents]", err);
		}
	}

	/**
	 * 
	 * @param appointmentId
	 * @param appointmentName
	 * @param appointmentDescription
	 * @param appointmentstart
	 * @param appointmentend
	 * @param isDaily
	 * @param isWeekly
	 * @param isMonthly
	 * @param isYearly
	 * @param categoryId
	 * @param remind
	 * @param mmClient
	 * @param users_id
	 * @return
	 */
	// ----------------------------------------------------------------------------------------------------------
	public Long updateAppointment(Long appointmentId, String appointmentName,
			String appointmentDescription, Date appointmentstart,
			Date appointmentend, Boolean isDaily, Boolean isWeekly,
			Boolean isMonthly, Boolean isYearly, Long categoryId, Long remind,
			@SuppressWarnings("rawtypes") List mmClient, Long users_id, String baseUrl, Long language_id,
			Boolean isPasswordProtected, String password, String iCalTimeZone) {

		log.debug("AppointmentDAOImpl.updateAppointment");
		try {

			Appointment ap = this.getAppointmentById(appointmentId);

			AppointmentReminderTyps appointmentReminderTyps = appointmentReminderTypDaoImpl
					.getAppointmentReminderTypById(remind);
			AppointmentCategory appointmentCategory = appointmentCategoryDaoImpl
					.getAppointmentCategoryById(categoryId);

			// change connected events of other participants
			if (ap.getIsConnectedEvent() != null && ap.getIsConnectedEvent()) {
				this.updateConnectedEvents(ap, appointmentName,
						appointmentDescription, appointmentstart,
						appointmentend, isDaily, isWeekly, isMonthly, isYearly,
						appointmentCategory, appointmentReminderTyps, mmClient,
						users_id, baseUrl, language_id, isPasswordProtected,
						password);
			}

			// Update Invitation hash to new time
			invitationManagement.updateInvitationByAppointment(appointmentId,
					appointmentstart, appointmentend);

			ap.setAppointmentName(appointmentName);
			ap.setAppointmentStarttime(appointmentstart);
			ap.setAppointmentEndtime(appointmentend);
			ap.setAppointmentDescription(appointmentDescription);
			ap.setUpdatetime(new Date());
			ap.setRemind(appointmentReminderTyps);
			ap.setIsDaily(isDaily);
			ap.setIsWeekly(isWeekly);
			ap.setIsMonthly(isMonthly);
			ap.setIsYearly(isYearly);
			ap.setLanguage_id(language_id);
			ap.setIsPasswordProtected(isPasswordProtected);
			ap.setPassword(password);
			// ap.setUserId(usersDao.getUser(userId));
			ap.setAppointmentCategory(appointmentCategory);

			if (ap.getAppointmentId() == null) {
				em.persist(ap);
			} else {
				if (!em.contains(ap)) {
					em.merge(ap);
				}
			}

			// Adding Invitor as Meetingmember
			Users user = userManagement.getUserById(users_id);

			String invitorName = user.getFirstname() + " " + user.getLastname()
					+ " [" + user.getAdresses().getEmail() + "]";

			List<MeetingMember> meetingsRemoteMembers = meetingMemberDao
					.getMeetingMemberByAppointmentId(ap.getAppointmentId());

			// to remove
			for (MeetingMember memberRemote : meetingsRemoteMembers) {

				boolean found = false;

				if (mmClient != null) {
					for (int i = 0; i < mmClient.size(); i++) {
						@SuppressWarnings("rawtypes")
						Map clientMemeber = (Map) mmClient.get(i);
						Long meetingMemberId = Long
								.valueOf(
										clientMemeber.get("meetingMemberId")
												.toString()).longValue();

						if (memberRemote.getMeetingMemberId().equals(
								meetingMemberId)) {
							log.debug("AppointMentDAOImpl.updateAppointment  - member "
									+ meetingMemberId + " is to be removed!");
							// Notifying Member for Update
							found = true;
						}

					}
				}

				if (!found) {

					// Not in List in client delete it
					meetingMemberLogic.deleteMeetingMember(
							memberRemote.getMeetingMemberId(), users_id,
							language_id);
					// meetingMemberDao.deleteMeetingMember(memberRemote.getMeetingMemberId());
				} else {
					// Notify member of changes
					invitationManagement.updateInvitation(ap, memberRemote,
							users_id, language_id, invitorName);

				}
			}

			// add items
			if (mmClient != null) {

				for (int i = 0; i < mmClient.size(); i++) {

					@SuppressWarnings("rawtypes")
					Map clientMember = (Map) mmClient.get(i);

					Long meetingMemberId = Long.valueOf(
							clientMember.get("meetingMemberId").toString())
							.longValue();

					boolean found = false;

					for (MeetingMember memberRemote : meetingsRemoteMembers) {
						if (memberRemote.getMeetingMemberId().equals(
								meetingMemberId)) {
							found = true;
						}
					}

					if (!found) {

						// We need two different timeZones, the internal Java
						// Object
						// TimeZone, and
						// the one for the UI display object to map to, cause
						// the UI
						// only has around 24 timezones
						// and Java around 600++
						Long sendToUserId = 0L;
						TimeZone timezoneMember = null;
						OmTimeZone omTimeZone = null;
						if (clientMember.get("userId") != null) {
							sendToUserId = Long.valueOf(
									clientMember.get("userId").toString())
									.longValue();
						}

						// Check if this is an internal user, if yes use the
						// timezone from his profile otherwise get the timezones
						// from the variable jNameTimeZone
						if (sendToUserId > 0) {
							Users interalUser = userManagement
									.getUserById(sendToUserId);
							timezoneMember = timezoneUtil
									.getTimezoneByUser(interalUser);
							omTimeZone = interalUser.getOmTimeZone();
						} else {
							// Get the internal-name of the timezone set in the
							// client object and convert it to a real one
							Object jName = clientMember.get("jNameTimeZone");
							if (jName == null) {
								log.error("jNameTimeZone not set in user object variable");
								jName = "";
							}
							omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jName
									.toString());
							timezoneMember = timezoneUtil
									.getTimezoneByInternalJName(jName
											.toString());
						}

						// Not In Remote List available - intern OR extern user
						meetingMemberLogic.addMeetingMember(
								clientMember.get("firstname").toString(),
								clientMember.get("lastname").toString(),
								"0", // member - Status
								"0", // appointement - Status
								appointmentId,
								null, // UserId
								clientMember.get("email").toString(), // Email
																		// to
																		// send
																		// to
								baseUrl, // URL to send to
								sendToUserId, // sending To: External users have
												// a 0 here
								new Boolean(false), // invitor
								language_id, isPasswordProtected, password,
								timezoneMember, omTimeZone, invitorName);

					}

				}
			}

			return appointmentId;
		} catch (Exception ex2) {
			log.error("[updateAppointment]: ", ex2);
		}
		return null;

	}

	public Long updateAppointmentByTime(Long appointmentId,
			Date appointmentstart, Date appointmentend, Long users_id,
			String baseUrl, Long language_id, String iCalTimeZone) {

		log.debug("AppointmentDAOImpl.updateAppointment");
		try {

			Appointment ap = this.getAppointmentById(appointmentId);

			// change connected events of other participants
			if (ap.getIsConnectedEvent() != null && ap.getIsConnectedEvent()) {
				this.updateConnectedEventsTimeOnly(ap, appointmentstart,
						appointmentend);
			}

			// Update Invitation hash to new time
			invitationManagement.updateInvitationByAppointment(appointmentId,
					appointmentstart, appointmentend);

			ap.setAppointmentStarttime(appointmentstart);
			ap.setAppointmentEndtime(appointmentend);
			ap.setUpdatetime(new Date());

			if (ap.getAppointmentId() == null) {
				em.persist(ap);
			} else {
				if (!em.contains(ap)) {
					em.merge(ap);
				}
			}

			List<MeetingMember> meetingsRemoteMembers = meetingMemberDao
					.getMeetingMemberByAppointmentId(ap.getAppointmentId());

			// Adding Invitor Name
			Users user = userManagement.getUserById(users_id);
			String invitorName = user.getFirstname() + " " + user.getLastname()
					+ " [" + user.getAdresses().getEmail() + "]";

			// Send notification of updated Event
			for (MeetingMember memberRemote : meetingsRemoteMembers) {

				// Notify member of changes
				invitationManagement.updateInvitation(ap, memberRemote,
						users_id, language_id, invitorName);

			}

			return appointmentId;
		} catch (Exception ex2) {
			log.error("[updateAppointmentByTime]: ", ex2);
		}
		return null;

	}

	// ----------------------------------------------------------------------------------------------------------

	public Long deleteAppointement(Long appointmentId) {
		log.debug("deleteAppointMent");
		try {

			Appointment app = this.getAppointmentById(appointmentId);
			app.setUpdatetime(new Date());
			app.setDeleted("true");

			if (app.getAppointmentId() == null) {
				em.persist(app);
			} else {
				if (!em.contains(app)) {
					em.merge(app);
				}
			}
			return appointmentId;
		} catch (Exception ex2) {
			log.error("[deleteAppointement]: " + ex2);
		}
		return null;
	}

	public List<Appointment> getAppointmentsByRange(Long userId,
			Date starttime, Date endtime) {
		try {

			Calendar calstart = Calendar.getInstance();
			calstart.setTime(starttime);
			calstart.set(Calendar.HOUR, 0);

			Calendar calend = Calendar.getInstance();
			calend.setTime(endtime);
			calend.set(Calendar.HOUR, 23);
			calend.set(Calendar.MINUTE, 59);

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted  "
					+ "AND "
					+ "( "
					+ "(a.appointmentStarttime BETWEEN :starttime AND :endtime) "
					+ "OR "
					+ "(a.appointmentEndtime BETWEEN :starttime AND :endtime) "
					+ "OR "
					+ "(a.appointmentStarttime < :starttime AND a.appointmentEndtime > :endtime) "
					+ ") " + "AND " + "( " + "a.userId.user_id = :userId "
					+ ")";

			// "AND (a.terminstatus != 4 AND a.terminstatus != 5)";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("deleted", "true");
			query.setParameter("starttime", calstart.getTime());
			query.setParameter("endtime", calend.getTime());
			query.setParameter("userId", userId);

			List<Appointment> listAppoints = query.getResultList();

			for (Appointment appointment : listAppoints) {
				log.debug("" + appointment);

				appointment.setMeetingMember(meetingMemberDao
						.getMeetingMemberByAppointmentId(appointment
								.getAppointmentId()));

			}

			return listAppoints;
		} catch (Exception ex2) {
			log.error("[getAppointmentsByRange]: ", ex2);
		}
		return null;
	}

	public List<Appointment> getAppointmentsByCat(Long categoryId) {
		try {

			String hql = "select a from Appointments a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.appointmentCategory.categoryId = :categoryId";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("deleted", "true");
			query.setParameter("categoryId", categoryId);

			List<Appointment> listAppoints = query.getResultList();
			return listAppoints;
		} catch (Exception ex2) {
			log.error("[getAppointements]: ", ex2);
		}
		return null;
	}

	public List<Appointment> getAppointmentsByCritAndCat(Long cat_id) {
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Appointment> cq = cb.createQuery(Appointment.class);
			Root<Appointment> c = cq.from(Appointment.class);
			Predicate condition = cb.equal(c.get("deleted"), "false");
			Predicate subCondition = cb.equal(c.get("categoryId"), cat_id);
			cq.where(condition, subCondition);
			TypedQuery<Appointment> q = em.createQuery(cq);
			List<Appointment> listAppoints = q.getResultList();

			return listAppoints;
		} catch (Exception ex2) {
			log.error("[getAppointements]: ", ex2);
		}
		return null;
	}

	// next appointment to select date
	public Appointment getNextAppointment(Date appointmentStarttime) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.appointmentStarttime > :appointmentStarttime ";

			TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
			query.setParameter("deleted", "true");
			query.setParameter("appointmentStarttime", appointmentStarttime);

			Appointment appoint = null;
			try {
				appoint = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appoint;
		} catch (Exception ex2) {
			log.error("[getNextAppointmentById]: ", ex2);
		}
		return null;
	}

	public List<Appointment> searchAppointmentsByName(String name) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.appointmentName LIKE :appointmentName";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("deleted", "true");
			query.setParameter("appointmentName", name);

			List<Appointment> listAppoints = query.getResultList();

			return listAppoints;
		} catch (Exception ex2) {
			log.error("[searchAppointmentsByName]: ", ex2);
		}
		return null;
	}

	/**
	 * @author becherer
	 * @param userId
	 * @return
	 */
	public List<Appointment> getTodaysAppointmentsbyRangeAndMember(Long userId) {
		log.debug("getAppoitmentbyRangeAndMember : UserID - " + userId);

		String hql = "SELECT app from MeetingMember mm "
				+ "JOIN mm.appointment as app "
				+ "WHERE mm.userid.user_id= :userId "
				+ "AND mm.deleted <> :mm_deleted "
				+ "AND app.deleted <> :app_deleted " + "AND  "
				+ "app.appointmentStarttime between :starttime " + "AND "
				+ " :endtime";

		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.SECOND, 1);

		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.MINUTE, 23);
		endCal.set(Calendar.HOUR, 59);
		endCal.set(Calendar.SECOND, 59);

		try {
			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);

			query.setParameter("mm_deleted", true);
			query.setParameter("app_deleted", "true");
			query.setParameter("userId", userId);

			query.setParameter("starttime", startCal.getTime());
			query.setParameter("endtime", endCal.getTime());

			List<Appointment> listAppoints = query.getResultList();
			return listAppoints;
		} catch (Exception e) {
			log.error("Error in getTodaysAppoitmentsbyRangeAndMember : ", e);
			return null;
		}
	}

	/**
	 * Get the meetings according to a time range. It starts by now to
	 * Calendar.getInstance().getTime().getTime() + milliseconds
	 * 
	 * @author o.becherer,seba.wagner
	 * @param milliseconds
	 *            to get events in the past make milliseconds < 0
	 * @param isReminderEmailSend
	 *            if null all events in the time range, if false or true the
	 *            param is set
	 * @return
	 */
	public List<Appointment> getAppointmentsForAllUsersByTimeRangeStartingNow(
			long milliseconds, Boolean isReminderEmailSend) {
		try {

			String hql = "SELECT app from MeetingMember mm "
					+ "JOIN mm.appointment as app "
					+ "WHERE mm.deleted <> :mm_deleted "
					+ "AND app.deleted <> :app_deleted "
					+ "AND app.appointmentStarttime between :starttime AND :endtime ";

			if (isReminderEmailSend != null) {
				hql += "AND ( app.isReminderEmailSend IS NULL OR app.isReminderEmailSend = :isReminderEmailSend ) ";
			}

			Calendar startCal = Calendar.getInstance();
			if (milliseconds < 0) {
				startCal.setTimeInMillis(startCal.getTimeInMillis()+milliseconds);
			}
			Calendar endCal = Calendar.getInstance();
			if (milliseconds > 0) {
				endCal.setTimeInMillis(endCal.getTimeInMillis()+milliseconds);
			}

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			
			Timestamp startStamp = new Timestamp(startCal.getTime().getTime());
            Timestamp stopStamp = new Timestamp(endCal.getTime().getTime());
            
            log.debug("startStamp "+startStamp);
            log.debug("stopStamp "+stopStamp);

			query.setParameter("mm_deleted", true);
			query.setParameter("app_deleted", "true");
			query.setParameter("starttime", startStamp);
			query.setParameter("endtime", stopStamp);
			if (isReminderEmailSend != null) {
				query.setParameter("isReminderEmailSend", isReminderEmailSend);
			}

			List<Appointment> listAppoints = query.getResultList();

			return listAppoints;
		} catch (Exception e) {
			log.error("Error in getAppointmentsForAllUsersByTimeRangeStartingNow : ", e);
			return null;
		}
	}

	// ---------------------------------------------------------------------------------------------

	public Appointment getAppointmentByRoomId(Long user_id, Long rooms_id) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.userId.user_id = :user_id "
					+ "AND a.room.rooms_id = :rooms_id ";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);

			query.setParameter("deleted", "true");
			query.setParameter("user_id", user_id);
			query.setParameter("rooms_id", rooms_id);

			List<Appointment> listAppoints = query.getResultList();

			if (listAppoints.size() > 0) {
				return listAppoints.get(0);
			}

			return null;

		} catch (Exception e) {
			log.error("[getAppointmentByRoomId]", e);
			return null;
		}
	}

}
