/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;
import static org.apache.openmeetings.web.app.Application.isInvaldSession;
import static org.apache.openmeetings.web.app.Application.removeInvalidSession;
import static org.apache.openmeetings.util.CalendarPatterns.ISO8601_FULL_FORMAT_STRING;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.IWebSession;
import org.apache.openmeetings.core.ldap.LdapLoginManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidget;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidget;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidget;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.UserDashboard;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.dashboard.Dashboard;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.WidgetFactory;
import org.wicketstuff.dashboard.web.DashboardContext;

import jakarta.inject.Inject;

public class WebSession extends AbstractAuthenticatedWebSession implements IWebSession {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(WebSession.class);
	public static final int MILLIS_IN_MINUTE = 60000;
	public static final List<String> AVAILABLE_TIMEZONES = Collections.unmodifiableList(List.of(TimeZone.getAvailableIDs()));
	public static final Set<String> AVAILABLE_TIMEZONE_SET = Collections.unmodifiableSet(new LinkedHashSet<>(AVAILABLE_TIMEZONES));
	public static final String WICKET_ROOM_ID = "wicketroomid";
	private Long userId = null;
	private Set<Right> rights = new HashSet<>();
	private long languageId = -1;
	private OmUrlFragment area = null;
	private TimeZone tz;
	private TimeZone browserTz;
	private FastDateFormat iso8601Format = null;
	private FastDateFormat  sdf = null;
	private UserDashboard dashboard;
	private Invitation invitation = null;
	private SOAPLogin soap = null;
	private Long roomId = null;
	private Long recordingId = null;
	private boolean kickedByAdmin = false;
	private ExtendedClientProperties extProps = new ExtendedClientProperties();
	@Inject
	private ClientManager cm;
	@Inject
	private InvitationDao inviteDao;
	@Inject
	private SOAPLoginDao soapDao;
	@Inject
	private SessiondataDao sessionDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private UserDao userDao;
	@Inject
	private LdapLoginManager ldapManager;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private RoomDao roomDao;

	public WebSession(Request request) {
		super(request);
		Injector.get().inject(this);
	}

	@Override
	public void invalidate() {
		cm.invalidate(userId, getId());
		super.invalidate();
		userId = null;
		rights = Set.of();
		iso8601Format = null;
		sdf = null;
		languageId = -1;
		invitation = null;
		soap = null;
		roomId = null;
		recordingId = null;
		tz = null;
		browserTz = null;
		extProps = new ExtendedClientProperties();
	}

	@Override
	public Roles getRoles() {
		if (rights.isEmpty()) {
			isSignedIn();
		}
		Roles r = new Roles();
		for (Right right : rights) {
			r.add(right.name());
		}
		return r;
	}

	@Override
	public boolean isSignedIn() {
		if (userId == null) {
			IAuthenticationStrategy strategy = getAuthenticationStrategy();
			// get username and password from persistence store
			String[] data = strategy.load();
			if (data != null && data.length > 3 && data[2] != null) {
				Long domainId = null;
				try {
					domainId = Long.valueOf(data[3]);
				} catch (Exception e) {
					//no-op
				}
				// try to sign in the user
				try {
					if (!signIn(data[0], data[1], Type.valueOf(data[2]), domainId)) {
						// the loaded credentials are wrong. erase them.
						strategy.remove();
					}
				} catch (Exception e) {
					//no-op, bad credentials
				}
			}
		}
		return userId != null && userId.longValue() > 0;
	}

	private void redirectHash(Room r, Runnable nullAction) {
		if (r != null) {
			String url = cm.getServerUrl(r, baseUrl -> {
				PageParameters params = new PageParameters();
				IRequestParameters reqParams = RequestCycle.get().getRequest().getQueryParameters();
				reqParams.getParameterNames().forEach(name -> params.add(name, reqParams.getParameterValue(name)));
				return Application.urlForPage(HashPage.class
						, params
						, baseUrl);
			});
			if (url == null) {
				nullAction.run();
			} else {
				throw new RedirectToUrlException(url);
			}
		}
	}

	public void checkHashes(StringValue secure, StringValue inviteStr) {
		log.debug("checkHashes, secure: '{}', invitation: '{}'", secure, inviteStr);
		try {
			log.debug("checkHashes, has soap in session ? '{}'", (soap != null));
			if (!secure.isEmpty() && (soap == null || !soap.getHash().equals(secure.toString()))) {
				// otherwise already logged-in with the same hash
				if (isSignedIn()) {
					log.debug("secure: Session is authorized, going to invalidate");
					invalidateNow();
				}
				signIn(secure.toString(), true);
			}
			if (!inviteStr.isEmpty()) {
				// invitation should be re-checked each time, due to PERIOD invitation can be
				// 1) not ready
				// 2) already expired
				// otherwise already logged-in with the same hash
				if (isSignedIn()) {
					log.debug("invitation: Session is authorized, going to invalidate");
					invalidateNow();
				}
				invitation = inviteDao.getByHash(inviteStr.toString(), false);
				Room r = null;
				if (invitation != null && invitation.isAllowEntry()) {
					Set<Right> hrights = new HashSet<>();
					if (invitation.getRoom() != null) {
						r = invitation.getRoom();
					} else if (invitation.getAppointment() != null && invitation.getAppointment().getRoom() != null) {
						r = invitation.getAppointment().getRoom();
					} else if (invitation.getRecording() != null) {
						recordingId = invitation.getRecording().getId();
					}
					if (r != null) {
						redirectHash(r, () -> inviteDao.markUsed(invitation));
						hrights.add(Right.ROOM);
						roomId = r.getId();
					}
					setUser(invitation.getInvitee(), hrights);
				}
			}
		} catch (RedirectToUrlException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected exception while checking hashes", e);
		}
	}

	public void checkToken(StringValue intoken) {
		cm.getToken(intoken).ifPresent(token -> {
			invalidateNow();
			signIn(userDao.get(token.getUserId()));
			log.debug("Cluster:: Token for room {} is found, signedIn ? {}", token.getRoomId(), userId != null);
			area = RoomEnterBehavior.getRoomUrlFragment(token.getRoomId());
		});
	}

	public boolean signIn(String secureHash, boolean markUsed) {
		SOAPLogin soapLogin = soapDao.get(secureHash);
		if (soapLogin == null) {
			log.warn("Secure hash not found in DB");
			return false;
		}
		log.debug("Secure hash found, is used ? {}", soapLogin.isUsed());
		if (!soapLogin.isUsed() || soapLogin.getAllowSameURLMultipleTimes()) {
			Sessiondata sd = sessionDao.check(soapLogin.getSessionHash());
			log.debug("Do we have data for hash ? {}", (sd.getXml() != null));
			if (sd.getXml() != null) {
				RemoteSessionObject remoteUser = RemoteSessionObject.fromString(sd.getXml());
				log.debug("Hash data was parsed successfuly; containg exterlaId ? {}", !Strings.isEmpty(remoteUser.getExternalId()));
				if (!Strings.isEmpty(remoteUser.getExternalId())) {
					Room r;
					if (Strings.isEmpty(soapLogin.getExternalRoomId()) || Strings.isEmpty(soapLogin.getExternalType())) {
						r = roomDao.get(soapLogin.getRoomId());
					} else {
						r = roomDao.getExternal(soapLogin.getExternalType(), soapLogin.getExternalRoomId());
					}
					if (r == null) {
						log.warn("Room was not found");
					} else {
						redirectHash(r, () -> {});
					}
					User user = userDao.getExternalUser(remoteUser.getExternalId(), remoteUser.getExternalType());
					if (user == null) {
						user = getNewUserInstance(null);
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setLogin(remoteUser.getUsername());
						user.setType(Type.EXTERNAL);
						user.setExternalId(remoteUser.getExternalId());
						user.addGroup(groupDao.getExternal(remoteUser.getExternalType()));
						user.getRights().clear();
						user.getRights().add(Right.ROOM);
						user.getAddress().setEmail(remoteUser.getEmail());
						user.setPictureUri(remoteUser.getPictureUrl());
					} else {
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setPictureUri(remoteUser.getPictureUrl());
					}
					user = userDao.update(user, null);
					if (markUsed) {
						soapLogin.setUsed(true);
						soapLogin.setUseDate(new Date());
						soapDao.update(soapLogin);
					}
					roomId = r == null ? null : r.getId();
					sd.setUserId(user.getId());
					sd.setRoomId(roomId);
					sessionDao.update(sd);
					setUser(user, null);
					recordingId = soapLogin.getRecordingId();
					soap = soapLogin;
					log.info("Hash was authorized");
					return true;
				}
			}
		}
		log.warn("Hash was NOT authorized");
		return false;
	}

	private void setUser(User u, Set<Right> rights) {
		changeSessionId(); // required to prevent session fixation
		userId = u.getId();
		if (rights == null) {
			Set<Right> r = new HashSet<>(u.getRights());
			if (u.getGroupUsers() != null && !AuthLevelUtil.hasAdminLevel(r)) {
				for (GroupUser gu : u.getGroupUsers()) {
					if (gu.isModerator()) {
						r.add(Right.GROUP_ADMIN);
						break;
					}
				}
			}
			this.rights = Collections.unmodifiableSet(r);
		} else {
			this.rights = Collections.unmodifiableSet(rights);
		}
		languageId = u.getLanguageId();
		tz = getTimeZone(u);
		iso8601Format = FastDateFormat.getInstance(ISO8601_FULL_FORMAT_STRING, tz);
		setLocale(LocaleHelper.getLocale(u));
		sdf = FormatHelper.getDateTimeFormat(u);
	}

	public boolean signIn(String login, String password, Type type, Long domainId) throws OmException {
		User u;
		switch (type) {
			case LDAP:
				u = ldapManager.login(login, password, domainId);
				break;
			case USER:
				/* we will allow login against internal DB in case user 'guess' LDAP password */
				u = userDao.login(login, password);
				break;
			case OAUTH:
				// we did all the checks at this stage, just set the user
				u = userDao.getByLogin(login, Type.OAUTH, domainId);
				break;
			default:
				throw new OmException("error.unknown");
		}
		if (u == null) {
			return false;
		}
		signIn(u);
		return true;
	}

	public boolean signIn(User u) {
		if (u == null) {
			return false;
		}
		setUser(u, null);
		return true;
	}

	public static WebSession get() {
		return (WebSession)AbstractAuthenticatedWebSession.get();
	}

	@Override
	public void setLanguage(long languageId) {
		this.languageId = languageId;
	}

	public static long getLanguage() {
		checkIsInvalid();
		WebSession session = get();
		if (session.languageId < 0) {
			if (session.isSignedIn()) {
				session.languageId = session.userDao.get(session.userId).getLanguageId();
			} else {
				session.languageId = getDefaultLang();
			}
		}
		return session.languageId;
	}

	public static Long getUserId() {
		checkIsInvalid();
		return get().userId;
	}

	public static Long getRecordingId() {
		return get().recordingId;
	}

	public Long getRoomId() {
		return get().roomId;
	}

	public Invitation getInvitation() {
		return invitation;
	}

	public SOAPLogin getSoapLogin() {
		return soap;
	}

	public static TimeZone getUserTimeZone() {
		return get().tz;
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance(get().tz);
	}

	public static Calendar getClientCalendar() {
		return Calendar.getInstance(getClientTimeZone());
	}

	public static FastDateFormat getIsoDateFormat() {
		return get().iso8601Format;
	}

	public static FastDateFormat getDateFormat() {
		return get().sdf;
	}

	public static Set<Right> getRights() {
		checkIsInvalid();
		return get().rights;
	}

	public static void setKickedByAdmin(boolean kicked) {
		get().kickedByAdmin = kicked;
	}

	public boolean isKickedByAdmin() {
		return kickedByAdmin;
	}

	public OmUrlFragment getArea() {
		return area;
	}

	public void setArea(OmUrlFragment area) {
		this.area = area;
	}


	public static Dashboard getDashboard() {
		Dashboard d = get().dashboard;
		if (d == null) {
			get().initDashboard();
			d = get().dashboard;
		}
		return d;
	}

	public Long getLanguageByLocale() {
		return LabelDao.getLanguage(getLocale(), getDefaultLang());
	}

	public String getClientTZCode() {
		TimeZone curZone = browserTz;
		if (browserTz == null) {
			try {
				browserTz = getClientInfo().getProperties().getTimeZone();
				if (browserTz != null && !AVAILABLE_TIMEZONE_SET.contains(browserTz.getID())) {
					for (String availableID : AVAILABLE_TIMEZONES) {
						TimeZone zone = TimeZone.getTimeZone(availableID);
						if (zone.hasSameRules(browserTz)) {
							browserTz = zone;
							break;
						}
					}
				}
				curZone = browserTz;
			} catch (Exception e) {
				//no-op
			}
			if (browserTz == null) {
				curZone = Calendar.getInstance(getLocale()).getTimeZone();
			}
		}
		return curZone == null ? null : curZone.getID();
	}

	public static TimeZone getClientTimeZone() {
		String tzCode = get().getClientTZCode();
		return tzCode == null ? null : TimeZone.getTimeZone(tzCode);
	}

	private void initDashboard() {
		DashboardContext dashboardContext = getDashboardContext();
		dashboard = (UserDashboard)dashboardContext.getDashboardPersister().load();
		boolean existMyRoomWidget = false, existRssWidget = false, existAdminWidget = false;
		boolean showMyRoomConfValue = isMyRoomsEnabled() && cfgDao.getBool(CONFIG_DASHBOARD_SHOW_MYROOMS, false);
		boolean showRssConfValue = cfgDao.getBool(CONFIG_DASHBOARD_SHOW_RSS, false);
		boolean showAdminWidget = getRights().contains(User.Right.ADMIN);
		boolean save = false;

		WidgetFactory widgetFactory = dashboardContext.getWidgetFactory();

		if (dashboard == null) {
			dashboard = new UserDashboard("default", "Default");

			dashboard.addWidget(widgetFactory.createWidget(new WelcomeWidgetDescriptor()));
			dashboard.addWidget(widgetFactory.createWidget(new StartWidgetDescriptor()));
			if (showMyRoomConfValue) {
				dashboard.addWidget(widgetFactory.createWidget(new MyRoomsWidgetDescriptor()));
			}
			if (showRssConfValue) {
				dashboard.addWidget(widgetFactory.createWidget(new RssWidgetDescriptor()));
			}
			if (showAdminWidget) {
				dashboard.addWidget(widgetFactory.createWidget(new AdminWidgetDescriptor()));
			}
			save = true;
		} else {
			for (Iterator<Widget> iter = dashboard.getWidgets().iterator(); iter.hasNext();) {
				Widget w = iter.next();
				// PrivateRoomWidget is stored in the profile of user. Now, Show_MyRooms_key is disable.
				if (w.getClass().equals(MyRoomsWidget.class)) {
					existMyRoomWidget = true;
					if (!showMyRoomConfValue) {
						iter.remove();
					}
				} else if (w.getClass().equals(RssWidget.class)) {
					// RssWidget is stored in the profile of user. Now, Show_RSS_Key is disable.
					existRssWidget = true;
					if (!showRssConfValue) {
						iter.remove();
					}
				} else if (w.getClass().equals(AdminWidget.class)) {
					// AdminWidget is stored in the profile of user. check if user is admin.
					existAdminWidget = true;
					if (!showAdminWidget) {
						iter.remove();
					}
				} else {
					w.init();
				}
			}
			// PrivateRoomWidget was deleted from profile and now it's enabled. It's added again to dashboard.
			if (!existMyRoomWidget && showMyRoomConfValue && !dashboard.isWidgetMyRoomsDeleted()) {
				dashboard.addWidget(widgetFactory.createWidget(new MyRoomsWidgetDescriptor()));
				save = true;
			}
			// RssWidget was deleted from profile and now it's enabled. It's added again to dashboard.
			if (!existRssWidget && showRssConfValue && !dashboard.isWidgetRssDeleted()) {
				dashboard.addWidget(widgetFactory.createWidget(new RssWidgetDescriptor()));
				save = true;
			}
			// user had no admin rights, now he/she has.
			if (!existAdminWidget && showAdminWidget && !dashboard.isWidgetAdminDeleted()) {
				dashboard.addWidget(widgetFactory.createWidget(new AdminWidgetDescriptor()));
				save = true;
			}
		}
		if (save) {
			dashboardContext.getDashboardPersister().save(dashboard);
		}
	}

	private static void checkIsInvalid() {
		WebSession session = get();
		if (isInvaldSession(session.getId())) {
			setKickedByAdmin(true);
			removeInvalidSession(session.getId());
			session.invalidateNow();
			Application.get().restartResponseAtSignInPage();
		}
	}

	public ExtendedClientProperties getExtendedProperties() {
		return extProps;
	}
}
