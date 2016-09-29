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
import static org.apache.openmeetings.web.pages.HashPage.INVITATION_HASH;
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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.MainService;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.web.app.Client.Activity;
import org.apache.openmeetings.web.pages.AccessDeniedPage;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.user.record.AviRecordingResourceReference;
import org.apache.openmeetings.web.user.record.FlvRecordingResourceReference;
import org.apache.openmeetings.web.user.record.JpgRecordingResourceReference;
import org.apache.openmeetings.web.user.record.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.user.record.OggRecordingResourceReference;
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
import org.wicketstuff.dashboard.WidgetRegistry;
import org.wicketstuff.dashboard.web.DashboardContext;
import org.wicketstuff.dashboard.web.DashboardContextInjector;
import org.wicketstuff.dashboard.web.DashboardSettings;

public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = getLogger(Application.class, webAppRootKey);
	private static boolean isInstalled;
	private static ConcurrentHashMap<String, Client> ONLINE_USERS = new ConcurrentHashMap<>(); 
	private static ConcurrentHashMap<String, Client> INVALID_SESSIONS = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Long, Set<String>> ROOMS = new ConcurrentHashMap<>();
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
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
		
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
		getRootRequestMapperAsCompound().add(new NoVersionMapper("/hash", HashPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("signin", getSignInPageClass()));
		mountPage("install", InstallWizardPage.class);
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountResource("/recordings/avi/${id}", new AviRecordingResourceReference());
		mountResource("/recordings/flv/${id}", new FlvRecordingResourceReference());
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/ogg/${id}", new OggRecordingResourceReference());
		mountResource("/recordings/jpg/${id}", new JpgRecordingResourceReference()); //should be in sync with VideoPlayer
		mountResource("/room/file/${id}", new RoomResourceReference());
		mountResource("/profile/${id}", new ProfileImageResourceReference());
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
	
	public static void addOnlineUser(Client c) {
		log.debug("Adding online client: {}, room: {}", c.getUid(), c.getRoomId());
		ONLINE_USERS.put(c.getUid(), c);
	}
	
	public static void removeOnlineUser(Client c) {
		if (c != null) {
			log.debug("Removing online client: {}, room: {}", c.getUid(), c.getRoomId());
			ONLINE_USERS.remove(c.getUid());
		}
	}
	
	@Override
	public org.apache.openmeetings.db.entity.room.Client updateClient(org.apache.openmeetings.db.entity.room.Client rcl) {
		if (rcl == null) {
			return null;
		}
		if (!rcl.isScreenClient()) {
			Client client = getOnlineClient(rcl.getPublicSID());
			if (client == null) {
				return null;
			}
			rcl.setIsSuperModerator(client.hasRight(Right.superModerator));
			rcl.setIsMod(client.hasRight(Right.moderator));
			rcl.setIsBroadcasting(client.hasRight(Right.audio));
			rcl.setCanVideo(client.hasRight(Right.video) && client.hasActivity(Activity.broadcastVideo));
			rcl.setCanDraw(client.hasRight(Right.whiteBoard));
			if (client.hasActivity(Activity.broadcastAudio) || client.hasActivity(Activity.broadcastVideo)) {
				rcl.setIsBroadcasting(true);
				rcl.setBroadCastID(ScopeApplicationAdapter.nextBroadCastId());
				StringBuilder sb = new StringBuilder();
				if (client.hasActivity(Activity.broadcastAudio)) {
					sb.append('a');
				}
				if (client.hasActivity(Activity.broadcastVideo)) {
					sb.append('v');
				}
				rcl.setAvsettings(sb.toString());
			} else {
				rcl.setAvsettings("n");
				rcl.setIsBroadcasting(false);
				rcl.setBroadCastID(-1L);
			}
		}
		return rcl;
	}
	
	public static Client getOnlineClient(String uid) {
		return uid == null ? null : ONLINE_USERS.get(uid);
	}
	
	public static boolean isUserOnline(Long userId) {
		boolean isUserOnline = false;
		for (Map.Entry<String, Client> e : ONLINE_USERS.entrySet()) {
			if (e.getValue().getUserId().equals(userId)) {
				isUserOnline = true;
				break;
			}
		} 
		return isUserOnline;
	}

	public static List<Client> getClients() {
		return new ArrayList<Client>(ONLINE_USERS.values());
	}

	public static List<Client> getClients(Long userId) {
		List<Client> result =  new ArrayList<>();
		for (Map.Entry<String, Client> e : ONLINE_USERS.entrySet()) {
			if (e.getValue().getUserId().equals(userId)) {
				result.add(e.getValue());
				break;
			}
		}
		return result;
	}
	
	public static int getClientsSize() {
		return ONLINE_USERS.size();
	}
	
	public static Client getClientByKeys(Long userId, String sessionId) {
		Client client = null;
		for (Map.Entry<String, Client> e : ONLINE_USERS.entrySet()) {
			Client c = e.getValue();
			if (c.getUserId().equals(userId) && c.getSessionId().equals(sessionId)) {
				client = c;
				break;
			}
		} 
		return client;
	}
	
	@Override
	public void invalidateClient(Long userId, String sessionId) {
		Client client = getClientByKeys(userId, sessionId);
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
		if (sessionId != null){
			INVALID_SESSIONS.remove(sessionId);
		}
	}
	
	public static Client addUserToRoom(Client c) {
		log.debug("Adding online room client: {}, room: {}", c.getUid(), c.getRoomId());
		ROOMS.putIfAbsent(c.getRoomId(), new ConcurrentHashSet<String>());
		ROOMS.get(c.getRoomId()).add(c.getUid());
		return c;
	}
	
	public static Client removeUserFromRoom(Client c) {
		log.debug("Removing online room client: {}, room: {}", c.getUid(), c.getRoomId());
		if (c.getRoomId() != null) {
			Set<String> clients = ROOMS.get(c.getRoomId());
			if (clients != null) {
				clients.remove(c.getUid());
				c.setRoomId(null);
			}
		}
		return c;
	}
	
	public static List<Client> getRoomClients(Long roomId) {
		List<Client> clients = new ArrayList<>();
		if (roomId != null) {
			Set<String> uids = ROOMS.get(roomId);
			if (uids != null) {
				for (String uid : uids) {
					Client c = getOnlineClient(uid);
					if (c != null) {
						clients.add(c);
					}
				}
			}
		}
		return clients;
	}
	
	public static Set<Long> getUserRooms(Long userId) {
		Set<Long> result = new HashSet<>();
		for (Entry<Long, Set<String>> me : ROOMS.entrySet()) {
			for (String uid : me.getValue()) {
				Client c = getOnlineClient(uid);
				if (c != null && c.getUserId().equals(userId)) {
					result.add(me.getKey());
				}
			}
		}
		return result;
	}
	
	public static boolean isUserInRoom(long roomId, long userId) {
		Set<String> clients = ROOMS.get(roomId);
		if (clients != null) {
			for (String uid : clients) {
				if (getOnlineClient(uid).getUserId().equals(userId)) {
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

	public static String getInvitationLink(Invitation i) {
		String link = "";
		Room r = i.getRoom();
		User u = i.getInvitee();
		if (r != null) {
			boolean allowed = u.getType() != Type.contact;
			if (allowed) {
				allowed = getBean(MainService.class).isRoomAllowedToUser(r, u);
			}
			if (!allowed) {
				PageParameters pp = new PageParameters();
				pp.add(INVITATION_HASH, i.getHash());
				if (u.getLanguageId() > 0) {
					pp.add("language", u.getLanguageId());
				}
				link = urlForPage(HashPage.class, pp);
			} else {
				link = getRoomUrlFragment(r.getId()).getLink();
			}
		}
		Recording rec = i.getRecording();
		if (rec != null) {
			link = urlForPage(HashPage.class, new PageParameters().add(INVITATION_HASH, i.getHash()));
		}
		return link;
	}
	
	@Override
	public String getOmInvitationLink(Invitation i) { //FIXME hack for email templates support (should be in separate module for now
		return getInvitationLink(i);
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
