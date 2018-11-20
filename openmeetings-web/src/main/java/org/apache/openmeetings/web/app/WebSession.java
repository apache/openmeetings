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
import static org.apache.openmeetings.util.CalendarPatterns.ISO8601_FULL_FORMAT_STRING;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MYROOMS_ENABLED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;
import static org.apache.openmeetings.web.app.Application.isInvaldSession;
import static org.apache.openmeetings.web.app.Application.removeInvalidSession;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.util.Arrays;
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
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
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
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidget;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidget;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidget;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.util.ExtendedClientProperties;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.UserDashboard;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.wicketstuff.dashboard.Dashboard;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.WidgetFactory;
import org.wicketstuff.dashboard.web.DashboardContext;

public class WebSession extends AbstractAuthenticatedWebSession implements IWebSession {
	private static final long serialVersionUID = 1L;
	private static final Logger log = getLogger(WebSession.class, getWebAppRootKey());
	public static final int MILLIS_IN_MINUTE = 60000;
	public static final List<String> AVAILABLE_TIMEZONES = Arrays.asList(TimeZone.getAvailableIDs());
	public static final Set<String> AVAILABLE_TIMEZONE_SET = new LinkedHashSet<>(AVAILABLE_TIMEZONES);
	public static final String WICKET_ROOM_ID = "wicketroomid";
	private Long userId = null;
	private Set<Right> rights = new HashSet<>();
	private long languageId = -1;
	private OmUrlFragment area = null;
	private TimeZone tz;
	private TimeZone browserTz;
	private FastDateFormat ISO8601FORMAT = null;
	private FastDateFormat  sdf = null;
	private UserDashboard dashboard;
	private Invitation i = null;
	private SOAPLogin soap = null;
	private Long roomId = null;
	private Long recordingId = null;
	private String externalType;
	private boolean kickedByAdmin = false;
	private ExtendedClientProperties extProps = new ExtendedClientProperties();

	public WebSession(Request request) {
		super(request);
	}

	@Override
	public void invalidate() {
		getBean(ClientManager.class).invalidate(userId, getId());
		super.invalidate();
		userId = null;
		rights = Collections.unmodifiableSet(Collections.<Right>emptySet());
		ISO8601FORMAT = null;
		sdf = null;
		languageId = -1;
		i = null;
		soap = null;
		roomId = null;
		recordingId = null;
		externalType = null;
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
				} catch (OmException e) {
					//no-op, bad credentials
				}
			}
		}
		return userId != null && userId.longValue() > 0;
	}

	public void checkHashes(StringValue secure, StringValue invitation) {
		try {
			if (!secure.isEmpty() && (soap == null || !soap.getHash().equals(secure.toString()))) {
				// otherwise already logged-in with the same hash
				if (isSignedIn()) {
					invalidateNow();
				}
				signIn(secure.toString(), true);
			}
			if (!invitation.isEmpty() && (i == null || !i.getHash().equals(invitation.toString()))) {
				// otherwise already logged-in with the same hash
				if (isSignedIn()) {
					invalidateNow();
				}
				i = getBean(InvitationDao.class).getByHash(invitation.toString(), false, true);
				if (i != null && i.isAllowEntry()) {
					Set<Right> hrights = new HashSet<>();
					if (i.getRoom() != null) {
						hrights.add(Right.Room);
						roomId = i.getRoom().getId();
					} else if (i.getAppointment() != null && i.getAppointment().getRoom() != null) {
						hrights.add(Right.Room);
						roomId = i.getAppointment().getRoom().getId();
					} else if (i.getRecording() != null) {
						recordingId = i.getRecording().getId();
					}
					setUser(i.getInvitee(), hrights);
				}
			}
		} catch (Exception e) {
			log.error("Unexpected exception while checking hashes", e);
		}
	}

	public boolean signIn(String secureHash, boolean markUsed) {
		SOAPLoginDao soapDao = getBean(SOAPLoginDao.class);
		SOAPLogin soapLogin = soapDao.get(secureHash);
		if (soapLogin == null) {
			return false;
		}
		if (!soapLogin.isUsed() || soapLogin.getAllowSameURLMultipleTimes()) {
			SessiondataDao sessionDao = getBean(SessiondataDao.class);
			Sessiondata sd = sessionDao.check(soapLogin.getSessionHash());
			if (sd.getXml() != null) {
				RemoteSessionObject remoteUser = RemoteSessionObject.fromXml(sd.getXml());
				if (remoteUser != null && !Strings.isEmpty(remoteUser.getExternalUserId())) {
					UserDao userDao = getBean(UserDao.class);
					User user = userDao.getExternalUser(remoteUser.getExternalUserId(), remoteUser.getExternalUserType());
					if (user == null) {
						user = getNewUserInstance(null);
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setLogin(remoteUser.getUsername());
						user.setType(Type.external);
						user.setExternalId(remoteUser.getExternalUserId());
						user.setExternalType(remoteUser.getExternalUserType());
						user.getRights().clear();
						user.getRights().add(Right.Room);
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
					roomId = soapLogin.getRoomId();
					sd.setUserId(user.getId());
					sd.setRoomId(roomId);
					sessionDao.update(sd);
					setUser(user, null);
					recordingId = soapLogin.getRecordingId();
					soap = soapLogin;
					return true;
				}
			}
		}
		return false;
	}

	private void setUser(User u, Set<Right> rights) {
		changeSessionId(); // required to prevent session fixation
		userId = u.getId();
		if (rights == null || rights.isEmpty()) {
			Set<Right> r = new HashSet<>(u.getRights());
			if (u.getGroupUsers() != null && !AuthLevelUtil.hasAdminLevel(r)) {
				for (GroupUser gu : u.getGroupUsers()) {
					if (gu.isModerator()) {
						r.add(Right.GroupAdmin);
						break;
					}
				}
			}
			this.rights = Collections.unmodifiableSet(r);
		} else {
			this.rights = Collections.unmodifiableSet(rights);
		}
		languageId = u.getLanguageId();
		externalType = u.getExternalType();
		tz = getTimeZone(u);
		ISO8601FORMAT = FastDateFormat.getInstance(ISO8601_FULL_FORMAT_STRING, tz);
		setLocale(LocaleHelper.getLocale(u));
		sdf = FormatHelper.getDateTimeFormat(u);
	}

	public boolean signIn(String login, String password, Type type, Long domainId) throws OmException {
		User u;
		switch (type) {
			case ldap:
				u = getBean(LdapLoginManager.class).login(login, password, domainId);
				break;
			case user:
				/* we will allow login against internal DB in case user 'guess' LDAP password */
				u = getBean(UserDao.class).login(login, password);
				break;
			case oauth:
				// we did all the checks at this stage, just set the user
				u = getBean(UserDao.class).getByLogin(login, Type.oauth, domainId);
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
				session.languageId = getBean(UserDao.class).get(session.userId).getLanguageId();
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
		return i;
	}

	public SOAPLogin getSoapLogin() {
		return soap;
	}

	public static String getExternalType() {
		return get().externalType;
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
		return get().ISO8601FORMAT;
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
		return getBean(IUserManager.class).getLanguage(getLocale());
	}

	public String getClientTZCode() {
		TimeZone _zone = browserTz;
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
				_zone = browserTz;
			} catch (Exception e) {
				//no-op
			}
			if (browserTz == null) {
				_zone = Calendar.getInstance(getLocale()).getTimeZone();
			}
		}
		return _zone == null ? null : _zone.getID();
	}

	public static TimeZone getClientTimeZone() {
		String tzCode = get().getClientTZCode();
		return tzCode == null ? null : TimeZone.getTimeZone(tzCode);
	}

	private void initDashboard() {
		DashboardContext dashboardContext = getDashboardContext();
		dashboard = (UserDashboard)dashboardContext.getDashboardPersister().load();
		boolean existMyRoomWidget = false, existRssWidget = false, existAdminWidget = false;
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		boolean showMyRoomConfValue = cfgDao.getBool(CONFIG_MYROOMS_ENABLED, true) && cfgDao.getBool(CONFIG_DASHBOARD_SHOW_MYROOMS, false);
		boolean showRssConfValue = cfgDao.getBool(CONFIG_DASHBOARD_SHOW_RSS, false);
		boolean showAdminWidget = getRights().contains(User.Right.Admin);
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

	@Override
	public long getOmLanguage() {
		return getLanguage();
	}

	private static void checkIsInvalid() {
		if (isInvaldSession(get().getId())) {
			setKickedByAdmin(true);
			removeInvalidSession(get().getId());
			org.apache.wicket.Session session = get();
			session.invalidateNow();
			Application.get().restartResponseAtSignInPage();
		}
	}

	public ExtendedClientProperties getExtendedProperties() {
		return extProps;
	}
}
