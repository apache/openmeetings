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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;
import static org.apache.openmeetings.web.user.rooms.RoomEnterBehavior.getRoomUrlFragment;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.red5.logging.Red5LoggerFactory.getLogger;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.MainService;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.RecordingPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.util.AviRecordingResourceReference;
import org.apache.openmeetings.web.util.FlvRecordingResourceReference;
import org.apache.openmeetings.web.util.JpgRecordingResourceReference;
import org.apache.openmeetings.web.util.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.OggRecordingResourceReference;
import org.apache.openmeetings.web.util.ProfileImageResourceReference;
import org.apache.openmeetings.web.util.UserDashboardPersister;
import org.apache.wicket.Localizer;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import ro.fortsoft.wicket.dashboard.WidgetRegistry;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextInjector;
import ro.fortsoft.wicket.dashboard.web.DashboardSettings;

public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = getLogger(Application.class, webAppRootKey);
	private static boolean isInstalled;
	private static MultiKeyMap<String, org.apache.openmeetings.web.app.Client> ONLINE_USERS = new MultiKeyMap<>(); 
	private static Map<String, org.apache.openmeetings.web.app.Client> INVALID_SESSIONS = new ConcurrentHashMap<String, org.apache.openmeetings.web.app.Client>();
	private static Map<Long, Set<Client>> ROOMS = new ConcurrentHashMap<Long, Set<Client>>();
	//additional maps for faster searching should be created
	private DashboardContext dashboardContext;
	private static Set<String> STRINGS_WITH_APP = new HashSet<>(); //FIXME need to be removed
	private static String appName;
	static {
		STRINGS_WITH_APP.addAll(Arrays.asList("499", "500", "506", "511", "512", "513", "517", "532", "622", "804"
				, "909", "952", "978", "981", "984", "989", "990", "999", "1151", "1155", "1157", "1158", "1194"));
	}
	
	@Override
	protected void init() {
		wicketApplicationName = super.getName();
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy());
		
		//Add custom resource loader at the beginning, so it will be checked first in the 
		//chain of Resource Loaders, if not found it will search in Wicket's internal 
		//Resource Loader for a the property key
		getResourceSettings().getStringResourceLoaders().add(0, new LabelResourceLoader());
		
		super.init();
		
		// register some widgets
		dashboardContext = new DashboardContext();
		dashboardContext.setDashboardPersister(new UserDashboardPersister());
		WidgetRegistry widgetRegistry = dashboardContext.getWidgetRegistry();
		widgetRegistry.registerWidget(new MyRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new WelcomeWidgetDescriptor());
		widgetRegistry.registerWidget(new StartWidgetDescriptor());
		widgetRegistry.registerWidget(new RssWidgetDescriptor());
		widgetRegistry.registerWidget(new AdminWidgetDescriptor());
		// add dashboard context injector
		getComponentInstantiationListeners().add(new DashboardContextInjector(dashboardContext));
		DashboardSettings dashboardSettings = DashboardSettings.get();
		dashboardSettings.setIncludeJQuery(false);
		dashboardSettings.setIncludeJQueryUI(false);
		
		getRootRequestMapperAsCompound().add(new NoVersionMapper(getHomePage()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("notinited", NotInitedPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("swf", SwfPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("signin", getSignInPageClass()));
		mountPage("install", InstallWizardPage.class);
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountPage("/recording/${hash}", RecordingPage.class);
		mountResource("/recordings/avi/${id}", new AviRecordingResourceReference());
		mountResource("/recordings/flv/${id}", new FlvRecordingResourceReference());
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/ogg/${id}", new OggRecordingResourceReference());
		mountResource("/recordings/jpg/${id}", new JpgRecordingResourceReference()); //should be in sync with VideoPlayer
		mountResource("/profile/${id}", new ProfileImageResourceReference()); //should be in sync with VideoPlayer
	}

	private static class NoVersionMapper extends MountedMapper {
		public NoVersionMapper(final Class<? extends IRequestablePage> pageClass) {
			this("/", pageClass);
		}
		
		public NoVersionMapper(String mountPath, final Class<? extends IRequestablePage> pageClass) {
			super(mountPath, pageClass, new PageParametersEncoder());
		}

		@Override
		protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
			//Does nothing
		}
		
		@Override
		public Url mapHandler(IRequestHandler requestHandler) {
			if (requestHandler instanceof ListenerInterfaceRequestHandler || requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
				return null;
			} else {
				return super.mapHandler(requestHandler);
			}
		}
	}

	public static OmAuthenticationStrategy getAuthenticationStrategy() {
		return (OmAuthenticationStrategy)get().getSecuritySettings().getAuthenticationStrategy();
	}
	
	@Override
	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return WebSession.class;
	}

	@Override
	public Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
	
	public static Application get() {
		return (Application) WebApplication.get();
	}
	
	public static DashboardContext getDashboardContext() {
		return get().dashboardContext;
	}
	
	public synchronized static void addOnlineUser(org.apache.openmeetings.web.app.Client client) {
		try {
			ONLINE_USERS.put("" + client.getUserId(), client.getSessionId(), client);
		} catch (Exception err) {
			log.error("[addOnlineUser]", err);
		}
	}
	
	public synchronized static void removeOnlineUser(org.apache.openmeetings.web.app.Client c) {
		try {
			if (c != null) {
				ONLINE_USERS.removeAll("" + c.getUserId(), c.getSessionId());
			}
		} catch (Exception err) {
			log.error("[removeOnlineUser]", err);
		}
	}
	
	public static boolean isUserOnline(Long userId) {
		MapIterator<MultiKey<? extends String>, org.apache.openmeetings.web.app.Client> it = ONLINE_USERS.mapIterator();
		boolean isUserOnline = false;
		while (it.hasNext()) {
			MultiKey<? extends String> multi = it.next();
			if (multi.size() > 0 && userId.equals(multi.getKey(0))) {
				isUserOnline = true;
				break;
			}
		} 
		return isUserOnline;
	}

	public static List<org.apache.openmeetings.web.app.Client> getClients() {
		return new ArrayList<org.apache.openmeetings.web.app.Client>(ONLINE_USERS.values());
	}

	public static List<org.apache.openmeetings.web.app.Client> getClients(Long userId) {
		List<org.apache.openmeetings.web.app.Client> result =  new ArrayList<org.apache.openmeetings.web.app.Client>();
		MapIterator<MultiKey<? extends String>, org.apache.openmeetings.web.app.Client> it = ONLINE_USERS.mapIterator();
		while (it.hasNext()) {
			MultiKey<? extends String> multi = it.next();
			if (multi.size() > 1 && userId.equals(multi.getKey(0))) {
				result.add(getClientByKeys(userId, (String)(multi.getKey(1))));
				break;
			}
		}
		return result;
	}
	
	public static int getClientsSize() {
		return ONLINE_USERS.size();
	}
	
	public static org.apache.openmeetings.web.app.Client getClientByKeys(Long userId, String sessionId) {
		return (org.apache.openmeetings.web.app.Client) ONLINE_USERS.get(userId, sessionId);
	}
	
	@Override
	public void invalidateClient(Long userId, String sessionId) {
		org.apache.openmeetings.web.app.Client client = getClientByKeys(userId, sessionId);
		if (client != null) {
			if (!INVALID_SESSIONS.containsKey(client.getSessionId())) {
				INVALID_SESSIONS.put(client.getSessionId(), client);
				removeOnlineUser(client);
			}
		}
	}
	
	public static boolean isInvaldSession(String sessionId) {
		return sessionId == null ? false : INVALID_SESSIONS.containsKey(sessionId);
	}
	
	public static void removeInvalidSession(String sessionId) {
		if (INVALID_SESSIONS.containsKey(sessionId)){
			INVALID_SESSIONS.remove(sessionId);
		}
	}
	
	public static Client addUserToRoom(long roomId, int pageId) {
		if (!ROOMS.containsKey(roomId)) {
			ROOMS.put(roomId, new ConcurrentHashSet<Client>());
		}
		Client c = new Client(WebSession.get().getId(), pageId, WebSession.getUserId());
		c.setUid(UUID.randomUUID().toString());
		ROOMS.get(roomId).add(c);
		return c;
	}
	
	public static void removeUserFromRoom(long roomId, int pageId) {
		removeUserFromRoom(roomId, new Client(WebSession.get().getId(), pageId, WebSession.getUserId()));
	}
	
	public static Client removeUserFromRoom(long roomId, Client _c) {
		if (ROOMS.containsKey(roomId)) {
			Set<Client> clients = ROOMS.get(roomId);
			for (Client c : clients) {
				if (c.equals(_c)) {
					clients.remove(c);
					return c;
				}
			}
			if (clients.isEmpty()) {
				ROOMS.remove(roomId);
			}
		}
		return _c;
	}
	
	public static long getRoom(Client c) {
		for (Entry<Long, Set<Client>> me : ROOMS.entrySet()) {
			Set<Client> clients = me.getValue();
			if (clients.contains(c)) {
				return me.getKey();
			}
		}
		return -1;
	}
	
	public static Set<Client> getRoomUsers(long roomId) {
		return ROOMS.containsKey(roomId) ? ROOMS.get(roomId) : new HashSet<Client>();
	}
	
	public static Set<Long> getUserRooms(long userId) {
		Set<Long> result = new HashSet<Long>();
		for (Entry<Long, Set<Client>> me : ROOMS.entrySet()) {
			for (Client c : me.getValue()) {
				if (c.getUserId() == userId) {
					result.add(me.getKey());
				}
			}
		}
		return result;
	}
	
	public static boolean isUserInRoom(long roomId, long userId) {
		if (ROOMS.containsKey(roomId)) {
			Set<Client> clients = ROOMS.get(roomId);
			for (Client c : clients) {
				if (c.getUserId() == userId) {
					return true;
				}
			}
		}
		return false;
	}
	
	//TODO need more safe way FIXME
	public <T> T _getBean(Class<T> clazz) {
		WebApplicationContext wac = getWebApplicationContext(getServletContext());
		return wac == null ? null : wac.getBean(clazz);
	}

	public static String getString(long id) {
		return getString(id, WebSession.getLanguage());
	}

	public static String getString(String id) {
		return getString(id, WebSession.getLanguage());
	}

	public static Locale getLocale(final long languageId) {
		Locale loc = LabelDao.languages.get(languageId);
		if (loc == null) {
			loc = WebSession.exists() ? WebSession.get().getLocale() : Locale.ENGLISH;
		}
		return loc;
	}
	
	public static String getString(String key, final long languageId) {
		return getString(key, getLocale(languageId), false);
	}
	
	public static String getString(long id, final long languageId) {
		return getString(id, getLocale(languageId));
	}
	
	public static String getString(long id, final Locale loc) {
		return getString("" + id, loc, false);
	}
	
	public static String getString(String key, final Locale loc, boolean noReplace) {
		if (!exists()) {
			ThreadContext.setApplication(Application.get(appName));
		}
		Localizer l = get().getResourceSettings().getLocalizer();
		String value = l.getStringIgnoreSettings(key, null, null, loc, null, "[Missing]");
		if (!noReplace && STRINGS_WITH_APP.contains(key)) {
			final MessageFormat format = new MessageFormat(value, loc);
			value = format.format(new Object[]{getBean(ConfigurationDao.class).getAppName()});
		}
		if (!noReplace && RuntimeConfigurationType.DEVELOPMENT == get().getConfigurationType()) {
			value += String.format(" [%s]", key);
		}
		return value;
	}

	public static boolean isInstalled() {
		boolean result = isInstalled;
		if (!isInstalled) {
			if (InitializationContainer.initComplete) {
				//TODO can also check crypt class here
				isInstalled = result = get()._getBean(UserDao.class).count() > 0;
			}
		}
		return result;
	}
	
	public static <T> T getBean(Class<T> clazz) {
		if (InitializationContainer.initComplete) {
			if (!isInstalled()) {
				throw new RestartResponseException(InstallWizardPage.class);
			}
			return get()._getBean(clazz);
		} else {
			throw new RestartResponseException(NotInitedPage.class);
		}
	}
	
	@Override
	public <T> T getOmBean(Class<T> clazz) { //FIXME hack for email templates support (should be in separate module for now
		return Application.getBean(clazz);
	}

	public static String getContactsLink() {
		return PROFILE_MESSAGES.getLink();
	}

	@Override
	public String getOmContactsLink() { //FIXME hack for email templates support (should be in separate module for now
		return getContactsLink();
	}

	public static String getInvitationLink(String baseUrl, Invitation i) {
		String link = baseUrl;
		if (link == null) {
			return null;
		}
		Room r = i.getRoom();
		User u = i.getInvitee();
		if (r != null) {
			boolean allowed = u.getType() != Type.contact;
			if (allowed) {
				allowed = getBean(MainService.class).isRoomAllowedToUser(r, u);
			}
			if (!allowed) {
				link += "?invitationHash=" + i.getHash();
		
				if (u.getLanguageId() > 0) {
					link += "&language=" + u.getLanguageId();
				}
			} else {
				link = getRoomUrlFragment(r.getId()).getLink();
			}
		}
		return link;
	}
	
	@Override
	public String getOmInvitationLink(String baseUrl, Invitation i) { //FIXME hack for email templates support (should be in separate module for now
		return getInvitationLink(baseUrl, i);
	}
	
	public static String urlForPage(Class<? extends Page> clazz, PageParameters pp) {
		RequestCycle rc = RequestCycle.get();
		return rc.getUrlRenderer().renderFullUrl(Url.parse(getBean(ConfigurationDao.class).getBaseUrl() + rc.urlFor(clazz, pp)));
	}

	@Override
	public String urlForActivatePage(PageParameters pp) { //FIXME hack for email templates support (should be in separate module for now
		return urlForPage(ActivatePage.class, pp);
	}

	@Override
	public String getOmString(long id) {
		return getString(id);
	}
	
	@Override
	public String getOmString(long id, long languageId) {
		return getString(id, languageId);
	}

	@Override
	public String getOmString(String key, long languageId) {
		return getString(key, languageId);
	}
}
