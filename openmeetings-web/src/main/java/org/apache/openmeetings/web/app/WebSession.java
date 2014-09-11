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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;

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

import org.apache.openmeetings.core.IWebSession;
import org.apache.openmeetings.core.ldap.LdapLoginManagement;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.State;
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
	public static int MILLIS_IN_MINUTE = 60000;
	//private static final Map<String, Locale> LNG_TO_LOCALE_MAP = new HashMap<String, Locale> ();
	private long userId = -1;
	private Set<Right> rights = new HashSet<User.Right>(); //TODO renew somehow on user edit !!!!
	private long languageId = -1; //TODO renew somehow on user edit !!!!
	private String SID = null;
	private OmUrlFragment area = null;
	private TimeZone tz;
	private TimeZone browserTz;
	public final static String ISO8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
	private DateFormat ISO8601FORMAT = new SimpleDateFormat(ISO8601_FORMAT_STRING); //FIXME not thread safe
	private DateFormat sdf;
	private UserDashboard dashboard;
	private Locale browserLocale = null;
	private Long recordingId;
	private Long loginError = null;
	private String externalType;
	private static Set<Long> STRINGS_WITH_APP = new HashSet<Long>(); //FIXME need to be removed
	public final static List<String> AVAILABLE_TIMEZONES = Arrays.asList(TimeZone.getAvailableIDs());
	public final static Set<String> AVAILABLE_TIMEZONE_SET = new LinkedHashSet<String>(AVAILABLE_TIMEZONES);
	static {
		STRINGS_WITH_APP.addAll(Arrays.asList(499L, 500L, 506L, 511L, 512L, 513L, 517L, 532L, 622L, 804L
				, 909L, 952L, 978L, 981L, 984L, 989L, 990L, 999L, 1151L, 1155L, 1157L, 1158L, 1194L));
	}
	
	public WebSession(Request request) {
		super(request);
		browserLocale = getLocale();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		userId = -1;
		rights = new HashSet<User.Right>();
		SID = null;
		sdf = null;
		recordingId = null;
		externalType = null;
		tz = null;
		browserTz = null;
		loginError = null;
	}
	
	@Override
	public Roles getRoles() {
		//first of all will check hashes
		try {
			IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
			StringValue secureHash = params.getParameterValue("secureHash");
			StringValue invitationHash = params.getParameterValue("invitationHash");
			if (!secureHash.isEmpty() || !invitationHash.isEmpty()) {
				PageParameters pp = new PageParameters();
				for (String p : params.getParameterNames()) {
					for (StringValue sv : params.getParameterValues(p)) {
						if (!sv.isEmpty()) {
							pp.add(p, sv.toString());
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
		if (userId < 1) {
			IAuthenticationStrategy strategy = getAuthenticationStrategy();
			// get username and password from persistence store
			String[] data = strategy.load();
			if (data != null && data.length > 3 && data[2] != null) {
				Long domainId = null;
				try {
					domainId = Long.parseLong(data[3]);
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
		return userId > -1;
	}

	public boolean signIn(String secureHash) {
		//FIXME code is duplicated from MainService, need to be unified
		SOAPLoginDao soapDao = getBean(SOAPLoginDao.class);
		SOAPLogin soapLogin = soapDao.get(secureHash);
		if (soapLogin != null && !soapLogin.getUsed()) { //add code for  || (soapLogin.getAllowSameURLMultipleTimes())
			SessiondataDao sessionDao = getBean(SessiondataDao.class);
			Sessiondata sd = sessionDao.getSessionByHash(soapLogin.getSessionHash());
			if (sd != null && sd.getSessionXml() != null) {
				RemoteSessionObject remoteUser = RemoteSessionObject.fromXml(sd.getSessionXml());
				if (remoteUser != null && !Strings.isEmpty(remoteUser.getExternalUserId())) {
					UserDao userDao = getBean(UserDao.class);
					User user = userDao.getExternalUser(remoteUser.getExternalUserId(), remoteUser.getExternalUserType());
					if (user == null) {
						user = userDao.getNewUserInstance(null);
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setLogin(remoteUser.getUsername()); //FIXME check if login UNIQUE
						user.setExternalUserId(remoteUser.getExternalUserId());
						user.setExternalUserType(remoteUser.getExternalUserType());
						user.getRights().add(Right.Room);
						user.getAdresses().setEmail(remoteUser.getEmail());
						user.setPictureuri(remoteUser.getPictureUrl());
					} else {
						user.setFirstname(remoteUser.getFirstname());
						user.setLastname(remoteUser.getLastname());
						user.setPictureuri(remoteUser.getPictureUrl());
					}
					user = userDao.update(user, null);

					soapLogin.setUsed(true);
					soapLogin.setUseDate(new Date());
					//soapLogin.setClientURL(clientURL); //FIXME
					soapDao.update(soapLogin);

					sessionDao.updateUser(SID, user.getId());
					setUser(user);
					recordingId = soapLogin.getRoomRecordingId();
					return true;
				}
			}
		}
		return false;
	}
	
	private void setUser(User u) {
		userId = u.getId();
		rights = Collections.unmodifiableSet(u.getRights());
		languageId = u.getLanguage_id();
		externalType = u.getExternalUserType();
		tz = getBean(TimezoneUtil.class).getTimeZone(u);
		ISO8601FORMAT.setTimeZone(tz);
		//FIXMW locale need to be set by User language first
		sdf = DateFormat.getDateTimeInstance(SHORT, SHORT, getLocale());
		if (null == getId()) {
			bind();
		}
	}
	
	public boolean signIn(String login, String password, Type type, Long domainId) {
		Sessiondata sessData = getBean(SessiondataDao.class).startsession();
		SID = sessData.getSession_id();
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
					u = getBean(UserDao.class).getByName(login, Type.oauth);
					break;
				default:
					throw new OmException(-1L);
			}
			if (u == null) {
				return false;
			}
			setUser(u);
			return true;
		} catch (OmException oe) {
			loginError = oe.getCode() == null ? -1L : oe.getCode();
		}
		return false;
	}
	
	public Long getLoginError() {
		return loginError;
	}
	
	public static WebSession get() {
		return (WebSession)AbstractAuthenticatedWebSession.get();
	}
	
	public static String getString(long id) {
		String s = getBean(FieldLanguagesValuesDao.class).getString(id, getLanguage());
		s = s == null ? "[Missing]" :
			(STRINGS_WITH_APP.contains(id) ? s.replaceAll("\\$APP_NAME", getBean(ConfigurationDao.class).getAppName()) : s);
		return s + (Application.get().getDebugSettings().isDevelopmentUtilitiesEnabled() ? " [" + id + "]" : "");
	}
	
	public void setLanguage(long languageId) {
		this.languageId = languageId;
	}
	
	public static long getLanguage() {
		WebSession session = get();
		if (session.languageId < 0) {
			if (session.isSignedIn()) {
				session.languageId = getBean(UserDao.class).get(session.userId).getLanguage_id();
			} else {
				session.languageId = getBean(ConfigurationDao.class).getConfValue(CONFIG_DEFAUT_LANG_KEY, Long.class, "1");
			}
		}
		return session.languageId;
	}
	
	public static FieldLanguage getLanguageObj() {
		return getBean(FieldLanguageDao.class).get(getLanguage());
	}
	
	public String getValidatedSid() {
		SessiondataDao sessionDao = getBean(SessiondataDao.class);
		Long _userId = sessionDao.checkSession(SID);
		if (_userId == null || userId != _userId) {
			Sessiondata sessionData = sessionDao.getSessionByHash(SID);
			if (sessionData == null) {
				sessionData = sessionDao.startsession();
			}
			if (!sessionDao.updateUser(sessionData.getSession_id(), userId, false, languageId)) {
				//something bad, force user to re-login
				invalidate();
			} else {
				SID = sessionData.getSession_id();
			}
		}
		return SID;
	}
	
	public static String getSid() {
		return get().getValidatedSid();
	}

	public static long getUserId() {
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
		return get().rights;
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

	public FieldLanguage getLanguageByBrowserLocale() {
		List<FieldLanguage> languages = getBean(FieldLanguageDao.class).get();
		for (FieldLanguage l : languages) {
			if (getBrowserLocale().getLanguage().equals(new Locale(l.getCode()).getLanguage())){
				return l;
			}
		}
		return languages.get(0);
	}

	public State getCountryByBrowserLocale() {
		List<State> states = getBean(StateDao.class).getStates();
		String code = getBrowserLocale().getISO3Country().toUpperCase();
		for (State s : states) {
			if (s.getShortName().toUpperCase().equals(code)){
				return s;
			}
		}
		return states.get(0);
	}

	public String getClientTZCode() {
		TimeZone _zone = browserTz;
		if (browserTz == null) {
			try {
				browserTz = getClientInfo().getProperties().getTimeZone();
				if (!AVAILABLE_TIMEZONE_SET.contains(browserTz.getID())) {
					for (String availableID : AVAILABLE_TIMEZONES) {
						if (availableID.startsWith("Etc")) {
							continue; //somehow these timezones has reverted rules
						}
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
		dashboard = (UserDashboard)dashboardContext.getDashboardPersiter().load();
		boolean existMyRoomWidget = false, existRssWidget = false;
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		boolean showMyRoomConfValue = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_MYROOMS_KEY, Integer.class, "0");
		boolean showRssConfValue = 1 == cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_RSS_KEY, Integer.class, "0");
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
		}
		if (save) {
			dashboardContext.getDashboardPersiter().save(dashboard);
		}
	}

	public String getOmString(long id) {
		return getString(id);
	}
}
