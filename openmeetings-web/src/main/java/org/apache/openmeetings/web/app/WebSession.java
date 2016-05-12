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

import static java.text.DateFormat.SHORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG_KEY;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getClientByKeys;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;
import static org.apache.openmeetings.web.app.Application.isInvaldSession;
import static org.apache.openmeetings.web.app.Application.removeInvalidSession;
import static org.apache.openmeetings.web.app.Application.removeOnlineUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.apache.openmeetings.IWebSession;
import org.apache.openmeetings.core.ldap.LdapLoginManagement;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidget;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidget;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidget;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.UserDashboard;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetFactory;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;

public class WebSession extends AbstractAuthenticatedWebSession implements IWebSession {
	private static final long serialVersionUID = 1L;
	public static final int MILLIS_IN_MINUTE = 60000;
	public static final String SECURE_HASH = "secureHash";
	public static final String INVITATION_HASH = "invitationHash";
	public static final String ISO8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final List<String> AVAILABLE_TIMEZONES = Arrays.asList(TimeZone.getAvailableIDs());
	public static final Set<String> AVAILABLE_TIMEZONE_SET = new LinkedHashSet<String>(AVAILABLE_TIMEZONES);
	private Long userId = null;
	private Set<Right> rights = new HashSet<User.Right>(); //TODO renew somehow on user edit !!!!
	private long languageId = -1; //TODO renew somehow on user edit !!!!
	private String SID = null;
	private OmUrlFragment area = null;
	private TimeZone tz;
	private TimeZone browserTz;
	private DateFormat ISO8601FORMAT = new SimpleDateFormat(ISO8601_FORMAT_STRING); //FIXME not thread safe
	private DateFormat sdf;
	private UserDashboard dashboard;
	private Locale browserLocale = null;
	private Long recordingId;
	private Long loginError = null;
	private String externalType;
	private boolean kickedByAdmin = false;
	
	public WebSession(Request request) {
		super(request);
		browserLocale = getLocale();
	}

	@Override
	public void invalidate() {
		removeOnlineUser(getClientByKeys(getUserId(), get().getId()));
		super.invalidate();
		userId = null;
		rights = new HashSet<User.Right>();
		SID = null;
		sdf = null;
		recordingId = null;
		externalType = null;
		tz = null;
		browserTz = null;
		loginError = null;
		browserLocale = null;
	}
	
	@Override
	public Roles getRoles() {
		//first of all will check hashes
		try {
			IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
			StringValue secureHash = params.getParameterValue(SECURE_HASH);
			StringValue invitationHash = params.getParameterValue(INVITATION_HASH);
			if (!secureHash.isEmpty() || !invitationHash.isEmpty()) {
				PageParameters pp = new PageParameters();
				for (String p : params.getParameterNames()) {
					List<StringValue> vals = params.getParameterValues(p);
					if (vals != null) {
						for (StringValue sv : vals) {
							if (!sv.isEmpty()) {
								pp.add(p, sv.toString());
							}
						}
					}
				}
				if (isSignedIn()) {
					invalidate();
				}
				throw new RestartResponseAtInterceptPageException(SwfPage.class, pp);
			}
		} catch (RestartResponseAtInterceptPageException e) {
			throw e;
		} catch (Exception e) {
			//no-op, will continue to sign-in page
		}
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
				if (!signIn(data[0], data[1], Type.valueOf(data[2]), domainId)) {
					// the loaded credentials are wrong. erase them.
					strategy.remove();
				}
			}
		}
		return userId != null && userId.longValue() > 0;
	}

	public boolean signIn(String secureHash, boolean markUsed) {
		//FIXME code is duplicated from MainService, need to be unified
		SOAPLoginDao soapDao = getBean(SOAPLoginDao.class);
		SOAPLogin soapLogin = soapDao.get(secureHash);
		if (soapLogin != null && !soapLogin.isUsed()) { //add code for  || (soapLogin.getAllowSameURLMultipleTimes())
			SessiondataDao sessionDao = getBean(SessiondataDao.class);
			Sessiondata sd = sessionDao.getSessionByHash(soapLogin.getSessionHash());
			if (sd != null && sd.getXml() != null) {
				RemoteSessionObject remoteUser = RemoteSessionObject.fromXml(sd.getXml());
				if (remoteUser != null && !Strings.isEmpty(remoteUser.getExternalUserId())) {
					UserDao userDao = getBean(UserDao.class);
					User user = userDao.getExternalUser(remoteUser.getExternalUserId(), remoteUser.getExternalUserType());
					if (user == null) {
						user = userDao.getNewUserInstance(null);
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setLogin(remoteUser.getUsername()); //FIXME check if login UNIQUE
						user.setExternalId(remoteUser.getExternalUserId());
						user.setExternalType(remoteUser.getExternalUserType());
						user.getRights().add(Right.Room);
						user.getAddress().setEmail(remoteUser.getEmail());
						user.setPictureuri(remoteUser.getPictureUrl());
					} else {
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setPictureuri(remoteUser.getPictureUrl());
					}
					user = userDao.update(user, null);
					if (markUsed) {
						soapLogin.setUsed(true);
						soapLogin.setUseDate(new Date());
						//soapLogin.setClientURL(clientURL); //FIXME
						soapDao.update(soapLogin);
					}
					sessionDao.updateUser(SID, user.getId());
					setUser(user);
					recordingId = soapLogin.getRecordingId();
					return true;
				}
			}
		}
		return false;
	}
	
	private void setUser(User u) {
		String _sid = SID;
		Long _recordingId = recordingId;
		replaceSession(); // required to prevent session fixation
		if (_sid != null) {
			SID = _sid;
		}
		if (_recordingId != null) {
			recordingId = _recordingId;
		}
		userId = u.getId();
		rights = Collections.unmodifiableSet(u.getRights());
		languageId = u.getLanguageId();
		externalType = u.getExternalType();
		tz = getBean(TimezoneUtil.class).getTimeZone(u);
		ISO8601FORMAT.setTimeZone(tz);
		setLocale(languageId == 3 ? Locale.GERMANY : LabelDao.languages.get(languageId));
		//FIXMW locale need to be set by User language first
		sdf = DateFormat.getDateTimeInstance(SHORT, SHORT, getLocale());
	}
	
	public boolean signIn(String login, String password, Type type, Long domainId) {
		try {
			User u = null;
			switch (type) {
				case ldap:
					u = getBean(LdapLoginManagement.class).login(login, password, domainId);
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
					throw new OmException(-1L);
			}
			if (u == null) {
				return false;
			}
			signIn(u);
			return true;
		} catch (OmException oe) {
			loginError = oe.getCode() == null ? Long.valueOf(-1) : oe.getCode();
		}
		return false;
	}
	
	public boolean signIn(User u) {
		Sessiondata sessData = getBean(SessiondataDao.class).startsession();
		SID = sessData.getSessionId();
		if (u == null) {
			return false;
		}
		setUser(u);
		return true;
	}
	
	public Long getLoginError() {
		return loginError;
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
				session.languageId = getBean(ConfigurationDao.class).getConfValue(CONFIG_DEFAULT_LANG_KEY, Long.class, "1");
			}
		}
		return session.languageId;
	}
	
	public String getValidatedSid() {
		SessiondataDao sessionDao = getBean(SessiondataDao.class);
		Long _userId = sessionDao.checkSession(SID);
		if (_userId == null || !_userId.equals(userId)) {
			Sessiondata sessionData = sessionDao.getSessionByHash(SID);
			if (sessionData == null) {
				sessionData = sessionDao.startsession();
			}
			if (!sessionDao.updateUser(sessionData.getSessionId(), userId, false, languageId)) {
				//something bad, force user to re-login
				invalidate();
			} else {
				SID = sessionData.getSessionId();
			}
		}
		return SID;
	}
	
	public static String getSid() {
		return get().getValidatedSid();
	}

	public static Long getUserId() {
		checkIsInvalid();
		return get().userId;
	}
	
	public static Long getRecordingId() {
		return get().recordingId;
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

	public static DateFormat getIsoDateFormat() {
		return get().ISO8601FORMAT;
	}
	
	public static DateFormat getDateFormat() {
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
	
	public Locale getBrowserLocale(){
		return browserLocale;
	}

	public Long getLanguageByBrowserLocale() {
		return getBean(IUserManager.class).getLanguage(getBrowserLocale());
	}

	public String getClientTZCode() {
		TimeZone _zone = browserTz;
		if (browserTz == null) {
			try {
				browserTz = getClientInfo().getProperties().getTimeZone();
				if (!AVAILABLE_TIMEZONE_SET.contains(browserTz.getID())) {
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
				_zone = Calendar.getInstance(getBrowserLocale()).getTimeZone();
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
		boolean showMyRoomConfValue = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_MYROOMS_KEY, Integer.class, "0");
		boolean showRssConfValue = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_RSS_KEY, Integer.class, "0");
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
				} else if ((w.getClass().equals(RssWidget.class))) {
					// RssWidget is stored in the profile of user. Now, Show_RSS_Key is disable.
					existRssWidget = true;
					if (!showRssConfValue) {
						iter.remove();
					}
				} else if ((w.getClass().equals(AdminWidget.class))) {
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
			org.apache.wicket.Session session = (org.apache.wicket.Session)get();
			session.invalidate();
			Application.get().restartResponseAtSignInPage();
		}
	}
}
