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

import static org.apache.openmeetings.core.util.WebSocketHelper.sendRoom;
import static org.apache.openmeetings.db.dao.room.SipDao.SIP_FIRST_NAME;
import static org.apache.openmeetings.db.dao.room.SipDao.SIP_USER_NAME;
import static org.apache.openmeetings.util.OmFileHelper.SIP_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_XFRAME_SAMEORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;
import static org.apache.openmeetings.web.pages.HashPage.INVITATION_HASH;
import static org.apache.openmeetings.web.user.rooms.RoomEnterBehavior.getRoomUrlFragment;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.apache.wicket.resource.JQueryResourceReference.getV3;
import static org.red5.logging.Red5LoggerFactory.getLogger;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.MainService;
import org.apache.openmeetings.core.remote.MobileService;
import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.Pod;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
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
import org.apache.openmeetings.web.user.dashboard.RecentRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.user.record.JpgRecordingResourceReference;
import org.apache.openmeetings.web.user.record.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.GroupLogoResourceReference;
import org.apache.openmeetings.web.util.ProfileImageResourceReference;
import org.apache.openmeetings.web.util.UserDashboardPersister;
import org.apache.wicket.DefaultPageManagerProvider;
import org.apache.wicket.Localizer;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerRequestHandler;
import org.apache.wicket.core.request.handler.ListenerRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.protocol.ws.WebSocketAwareCsrfPreventionRequestCycleListener;
import org.apache.wicket.protocol.ws.api.WebSocketResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.validation.validator.UrlValidator;
import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.wicketstuff.dashboard.WidgetRegistry;
import org.wicketstuff.dashboard.web.DashboardContext;
import org.wicketstuff.dashboard.web.DashboardContextInjector;
import org.wicketstuff.dashboard.web.DashboardSettings;
import org.wicketstuff.datastores.hazelcast.HazelcastDataStore;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = getLogger(Application.class, webAppRootKey);
	private static boolean isInstalled;
	private final static String ONLINE_USERS_KEY = "ONLINE_USERS_KEY";
	private final static String UID_BY_SID_KEY = "UID_BY_SID_KEY";
	private final static String INVALID_SESSIONS_KEY = "INVALID_SESSIONS_KEY";
	private final static String ROOMS_KEY = "ROOMS_KEY";
	private final static String STREAM_CLIENT_KEY = "STREAM_CLIENT_KEY";
	public final static String NAME_ATTR_KEY = "name";
	//additional maps for faster searching should be created
	private DashboardContext dashboardContext;
	private static Set<String> STRINGS_WITH_APP = new HashSet<>(); //FIXME need to be removed
	private static String appName;
	static {
		STRINGS_WITH_APP.addAll(Arrays.asList("499", "500", "506", "511", "512", "513", "517", "532", "622", "widget.start.desc"
				, "909", "952", "978", "981", "984", "989", "990", "999", "1151", "1155", "1157", "1158", "1194"));
	}
	public static final String HASH_MAPPING = "/hash";
	public static final String SIGNIN_MAPPING = "/signin";
	public static final String NOTINIT_MAPPING = "/notinited";
	private String xFrameOptions = HEADER_XFRAME_SAMEORIGIN;
	private String contentSecurityPolicy = OpenmeetingsVariables.HEADER_CSP_SELF;
	private final HazelcastInstance hazelcast = Hazelcast.getOrCreateHazelcastInstance(new XmlConfigBuilder().build());
	private ITopic<IClusterWsMessage> hazelWsTopic;

	@Override
	protected void init() {
		wicketApplicationName = super.getName();
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy());
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);

		hazelcast.getCluster().getLocalMember().setStringAttribute(NAME_ATTR_KEY, hazelcast.getName());
		hazelWsTopic = hazelcast.getTopic("default");
		hazelWsTopic.addMessageListener(new MessageListener<IClusterWsMessage>() {
			@Override
			public void onMessage(Message<IClusterWsMessage> msg) {
				String serverId = msg.getPublishingMember().getStringAttribute(NAME_ATTR_KEY);
				if (serverId.equals(hazelcast.getName())) {
					return;
				}
				WebSocketHelper.send(msg.getMessageObject());
			}
		});
		hazelcast.getCluster().addMembershipListener(new MembershipListener() {
			@Override
			public void memberRemoved(MembershipEvent evt) {
				//server down, need to remove all online clients, process persistent addresses
				String serverId = evt.getMember().getStringAttribute(NAME_ATTR_KEY);
				for (Map.Entry<String, Client> e : getOnlineUsers().entrySet()) {
					if (serverId.equals(e.getValue().getServerId())) {
						exit(e.getValue());
					}
				}
				Map<String, StreamClient> streams = getStreamClients();
				for (Map.Entry<String, StreamClient> e : streams.entrySet()) {
					if (serverId.equals(e.getValue().getServerId())) {
						streams.remove(e.getKey());
					}
				}
				updateJpaAddresses(_getBean(ConfigurationDao.class));
			}

			@Override
			public void memberAttributeChanged(MemberAttributeEvent evt) {
			}

			@Override
			public void memberAdded(MembershipEvent evt) {
				//server added, need to process persistent addresses
				updateJpaAddresses(_getBean(ConfigurationDao.class));
				//check for duplicate instance-names
				Set<String> names = new HashSet<>();
				for (Member m : evt.getMembers()) {
					String serverId = evt.getMember().getStringAttribute(NAME_ATTR_KEY);
					if (names.contains(serverId)) {
						log.warn("Duplicate cluster instance with name {} found {}", serverId, m);
					}
					names.add(serverId);
				}
			}
		});
		setPageManagerProvider(new DefaultPageManagerProvider(this) {
			@Override
			protected IDataStore newDataStore() {
				return new HazelcastDataStore(hazelcast);
			}
		});
		//Add custom resource loader at the beginning, so it will be checked first in the
		//chain of Resource Loaders, if not found it will search in Wicket's internal
		//Resource Loader for a the property key
		getResourceSettings().getStringResourceLoaders().add(0, new LabelResourceLoader());
		getJavaScriptLibrarySettings().setJQueryReference(getV3());
		getRequestCycleListeners().add(new WebSocketAwareCsrfPreventionRequestCycleListener() {
			@Override
			public void onEndRequest(RequestCycle cycle) {
				Response resp = cycle.getResponse();
				if (resp instanceof WebResponse && !(resp instanceof WebSocketResponse)) {
					WebResponse wresp = (WebResponse)resp;
					wresp.setHeader("X-XSS-Protection", "1; mode=block");
					wresp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
					wresp.setHeader("X-Content-Type-Options", "nosniff");
					wresp.setHeader("X-Frame-Options", xFrameOptions);
					wresp.setHeader("Content-Security-Policy", contentSecurityPolicy);
				}
			}
		});

		super.init();

		// register some widgets
		dashboardContext = new DashboardContext();
		dashboardContext.setDashboardPersister(new UserDashboardPersister());
		WidgetRegistry widgetRegistry = dashboardContext.getWidgetRegistry();
		widgetRegistry.registerWidget(new MyRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new RecentRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new WelcomeWidgetDescriptor());
		widgetRegistry.registerWidget(new StartWidgetDescriptor());
		widgetRegistry.registerWidget(new RssWidgetDescriptor());
		widgetRegistry.registerWidget(new AdminWidgetDescriptor());
		// add dashboard context injector
		getComponentInstantiationListeners().add(new DashboardContextInjector(dashboardContext));
		DashboardSettings dashboardSettings = DashboardSettings.get();
		dashboardSettings.setIncludeJQueryUI(false);

		getRootRequestMapperAsCompound().add(new NoVersionMapper(getHomePage()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(NOTINIT_MAPPING, NotInitedPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(HASH_MAPPING, HashPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(SIGNIN_MAPPING, getSignInPageClass()));
		mountPage("install", InstallWizardPage.class);
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/jpg/${id}", new JpgRecordingResourceReference()); //should be in sync with VideoPlayer
		mountResource("/room/file/${id}", new RoomResourceReference());
		mountResource("/profile/${id}", new ProfileImageResourceReference());
		mountResource("/group/${id}", new GroupLogoResourceReference());
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
			if (requestHandler instanceof ListenerRequestHandler || requestHandler instanceof BookmarkableListenerRequestHandler) {
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
		return (Application)org.apache.wicket.Application.get(wicketApplicationName);
	}

	public static DashboardContext getDashboardContext() {
		return get().dashboardContext;
	}

	private Map<String, Client> getOnlineUsers() {
		return hazelcast.getMap(ONLINE_USERS_KEY);
	}

	private Map<String, String> getInvalidSessions() {
		return hazelcast.getMap(INVALID_SESSIONS_KEY);
	}

	private Map<Long, Set<String>> getRooms() {
		return hazelcast.getMap(ROOMS_KEY);
	}

	private Map<String, String> getUidBySid() {
		return hazelcast.getMap(UID_BY_SID_KEY);
	}

	@Override
	public Set<Long> getActiveRoomIds() {
		return getRooms().keySet();
	}

	@Override
	public Map<String, StreamClient> getStreamClients() {
		return hazelcast.getMap(STREAM_CLIENT_KEY);
	}

	@Override
	public StreamClient update(StreamClient c) {
		hazelcast.getMap(STREAM_CLIENT_KEY).put(c.getUid(), c);
		return c;
	}

	public static void addOnlineUser(Client c) {
		log.debug("Adding online client: {}, room: {}", c.getUid(), c.getRoomId());
		c.setServerId(get().getServerId());
		get().getOnlineUsers().put(c.getUid(), c);
		get().getUidBySid().put(c.getSid(), c.getUid());
	}

	public static void exitRoom(Client c) {
		Long roomId = c.getRoomId();
		removeUserFromRoom(c);
		if (roomId != null) {
			sendRoom(new RoomMessage(roomId, c.getUserId(), RoomMessage.Type.roomExit));
			getBean(ConferenceLogDao.class).add(
					ConferenceLog.Type.roomLeave
					, c.getUserId(), "0", roomId
					, c.getRemoteAddress()
					, "" + roomId);
		}
	}

	@Override
	public void exit(String uid) {
		if (uid != null) {
			exit(getOnlineUsers().get(uid));
		}
	}

	private static void exit(Client c) {
		if (c != null) {
			if (c.getRoomId() != null) {
				exitRoom(c);
			}
			log.debug("Removing online client: {}, room: {}", c.getUid(), c.getRoomId());
			get().getOnlineUsers().remove(c.getUid());
			get().getUidBySid().remove(c.getSid());
		}
	}

	private static boolean hasVideo(StreamClient rcl) {
		return rcl != null && rcl.getAvsettings().contains("v");
	}

	private static boolean hasVideo(Client c) {
		return c != null && c.hasActivity(Activity.broadcastV);
	}

	@Override
	public StreamClient updateClient(StreamClient rcl, boolean forceSize) {
		if (rcl == null) {
			return null;
		}
		Client client = getClientBySid(rcl.getOwnerSid());
		if (client == null) {
			if (rcl.isMobile()) {
				Sessiondata sd = getBean(SessiondataDao.class).check(rcl.getOwnerSid());
				UserDao udao = getBean(UserDao.class);
				User u = udao.get(sd.getUserId());
				rcl = getBean(MobileService.class).create(rcl, u);
				//Mobile client enters the room
				client = new Client(rcl, udao.get(rcl.getUserId()));
				addOnlineUser(client);
				if (rcl.getRoomId() != null) {
					client.setCam(0);
					client.setMic(0);
					addUserToRoom(client);
					//FIXME TODO unify this
					WebSocketHelper.sendRoom(new RoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.roomEnter));
				}
				//FIXME TODO rights
			} else if (client == null && rcl.isSipTransport()) {
				rcl.setUsername(SIP_USER_NAME);
				rcl.setUserId(SIP_USER_ID);
				//SipTransport enters the room
				User u = new User();
				u.setId(SIP_USER_ID);
				u.setLogin(SIP_USER_NAME);
				u.setFirstname(SIP_FIRST_NAME);
				client = new Client(rcl, u);
				addOnlineUser(client);
				client.setCam(0);
				client.setMic(0);
				client.allow(Room.Right.audio, Room.Right.video);
				client.set(Activity.broadcastA);
				addUserToRoom(client);
				//FIXME TODO unify this
				WebSocketHelper.sendRoom(new RoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.roomEnter));
			} else {
				return null;
			}
		}
		if (rcl.getRoomId() == null || !rcl.getRoomId().equals(client.getRoomId())) {
			//TODO mobile
			return null;
		}
		User u = client.getUser();
		rcl.setUserId(u.getId());
		rcl.setUsername(u.getLogin());
		rcl.setFirstname(u.getFirstname());
		rcl.setLastname(u.getLastname());
		rcl.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
		rcl.setSuperMod(client.hasRight(Right.superModerator));
		rcl.setMod(client.hasRight(Right.moderator));
		rcl.setCanVideo(client.hasRight(Right.video) && client.isCamEnabled() && client.hasActivity(Activity.broadcastV));
		if (client.hasActivity(Activity.broadcastA) && client.getMic() < 0) {
			client.remove(Activity.broadcastA);
		}
		if (client.hasActivity(Activity.broadcastV) && client.getCam() < 0) {
			client.remove(Activity.broadcastV);
		}
		if (client.hasActivity(Activity.broadcastA) || client.hasActivity(Activity.broadcastV)) {
			if (forceSize || rcl.getWidth() == 0 || rcl.getHeight() == 0) {
				rcl.setWidth(client.getWidth());
				rcl.setHeight(client.getHeight());
			}
			if (client.getPod() != Pod.none) {
				rcl.setInterviewPodId(client.getPod() == Pod.left ? 1 : 2);
			}
			StringBuilder sb = new StringBuilder();
			if (client.hasActivity(Activity.broadcastA)) {
				sb.append('a');
			}
			if (client.hasActivity(Activity.broadcastV)) {
				sb.append('v');
			}
			if (!rcl.isBroadcasting() || hasVideo(rcl) != hasVideo(client)) {
				rcl.setBroadcasting(true);
			}
			rcl.setAvsettings(sb.toString());
		} else {
			rcl.setAvsettings("n");
			rcl.setBroadcasting(false);
		}
		return rcl;
	}

	public static Client getOnlineClient(String uid) {
		return uid == null ? null : get().getOnlineUsers().get(uid);
	}

	@Override
	public Client getOmOnlineClient(String uid) {
		return getOnlineClient(uid);
	}

	@Override
	public Client getOmClientBySid(String sid) {
		return getClientBySid(sid);
	}

	public static Client getClientBySid(String sid) {
		if (sid == null) {
			return null;
		}
		String uid = get().getUidBySid().get(sid);
		return uid == null ? null : get().getOnlineUsers().get(uid);
	}

	public static boolean isUserOnline(Long userId) {
		boolean isUserOnline = false;
		for (Map.Entry<String, Client> e : get().getOnlineUsers().entrySet()) {
			if (e.getValue().getUserId().equals(userId)) {
				isUserOnline = true;
				break;
			}
		}
		return isUserOnline;
	}

	public static List<Client> getClients() {
		return new ArrayList<>(get().getOnlineUsers().values());
	}

	public static List<Client> getClients(Long userId) {
		List<Client> result =  new ArrayList<>();
		for (Map.Entry<String, Client> e : get().getOnlineUsers().entrySet()) {
			if (e.getValue().getUserId().equals(userId)) {
				result.add(e.getValue());
				break;
			}
		}
		return result;
	}

	public static Client getClientByKeys(Long userId, String sessionId) {
		Client client = null;
		for (Map.Entry<String, Client> e : get().getOnlineUsers().entrySet()) {
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
			Map<String, String> invalid = getInvalidSessions();
			if (!invalid.containsKey(client.getSessionId())) {
				invalid.put(client.getSessionId(), client.getUid());
				exit(client);
			}
		}
	}

	public static boolean isInvaldSession(String sessionId) {
		return sessionId == null ? false : get().getInvalidSessions().containsKey(sessionId);
	}

	public static void removeInvalidSession(String sessionId) {
		if (sessionId != null){
			get().getInvalidSessions().remove(sessionId);
		}
	}

	public static Client update(Client c) {
		get().getOnlineUsers().put(c.getUid(), c); // update in storage
		return c;
	}

	public static Client addUserToRoom(Client c) {
		log.debug("Adding online room client: {}, room: {}", c.getUid(), c.getRoomId());
		Map<Long, Set<String>> rooms = get().getRooms();
		rooms.putIfAbsent(c.getRoomId(), new ConcurrentHashSet<String>());
		Set<String> set = rooms.get(c.getRoomId());
		set.add(c.getUid());
		rooms.put(c.getRoomId(), set);
		update(c);
		return c;
	}

	public static Client removeUserFromRoom(Client c) {
		Long roomId = c.getRoomId();
		log.debug("Removing online room client: {}, room: {}", c.getUid(), roomId);
		if (roomId != null) {
			Set<String> clients = get().getRooms().get(roomId);
			if (clients != null) {
				clients.remove(c.getUid());
			}
			getBean(ScopeApplicationAdapter.class).roomLeaveByScope(c.getUid(), roomId);
			c.clear();
			update(c);
		}
		return c;
	}

	@Override
	public List<Client> getOmRoomClients(Long roomId) {
		return getRoomClients(roomId);
	}

	@Override
	public List<Client> getOmClients(Long userId) {
		return getClients(userId);
	}

	public static List<Client> getRoomClients(Long roomId) {
		return getRoomClients(roomId, null);
	}
	public static List<Client> getRoomClients(Long roomId, Predicate<Client> filter) {
		List<Client> clients = new ArrayList<>();
		if (roomId != null) {
			Set<String> uids = get().getRooms().get(roomId);
			if (uids != null) {
				for (String uid : uids) {
					Client c = getOnlineClient(uid);
					if (c != null && (filter == null || filter.test(c))) {
						clients.add(c);
					}
				}
			}
		}
		return clients;
	}

	public static Set<Long> getUserRooms(Long userId) {
		Set<Long> result = new HashSet<>();
		for (Entry<Long, Set<String>> me : get().getRooms().entrySet()) {
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
		Set<String> clients = get().getRooms().get(roomId);
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
			loc = Session.exists() ? WebSession.get().getLocale() : Locale.ENGLISH;
		}
		return loc;
	}

	public static String getString(String key, final long languageId) {
		return getString(key, getLocale(languageId));
	}

	public static String getString(long id, final long languageId) {
		return getString(id, getLocale(languageId));
	}

	public static String getString(long id, final Locale loc) {
		return getString("" + id, loc);
	}

	public static String getString(String key, final Locale loc, String... params) {
		if (!exists()) {
			ThreadContext.setApplication(org.apache.wicket.Application.get(appName));
		}
		if ((params == null || params.length == 0) && STRINGS_WITH_APP.contains(key)) {
			params = new String[]{getBean(ConfigurationDao.class).getAppName()};
		}
		Localizer l = get().getResourceSettings().getLocalizer();
		String value = l.getStringIgnoreSettings(key, null, null, loc, null, "[Missing]");
		if (params != null && params.length > 0) {
			final MessageFormat format = new MessageFormat(value, loc);
			value = format.format(params);
		}
		if (RuntimeConfigurationType.DEVELOPMENT == get().getConfigurationType()) {
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

	@Override
	public <T> T _getOmBean(Class<T> clazz) { //FIXME hack for web services support (should be in separate module for now
		return Application.get()._getBean(clazz);
	}

	public static String getContactsLink() {
		return PROFILE_MESSAGES.getLink();
	}

	@Override
	public String getOmContactsLink() { //FIXME hack for email templates support (should be in separate module for now
		return getContactsLink();
	}

	public static String getInvitationLink(Invitation i, String baseUrl) {
		String link = "";
		Room r = i.getRoom();
		User u = i.getInvitee();
		if (r != null) {
			if (r.isAppointment() && i.getInvitedBy().getId().equals(u.getId())) {
				link = getRoomUrlFragment(r.getId()).getLink();
			} else {
				boolean allowed = Type.contact != u.getType() && Type.external != u.getType();
				if (allowed) {
					allowed = getBean(MainService.class).isRoomAllowedToUser(r, u);
				}
				if (allowed) {
					link = getRoomUrlFragment(r.getId()).getLink();
				} else {
					PageParameters pp = new PageParameters();
					pp.add(INVITATION_HASH, i.getHash());
					if (u.getLanguageId() > 0) {
						pp.add("language", u.getLanguageId());
					}
					link = urlForPage(HashPage.class, pp, baseUrl);
				}
			}
		}
		Recording rec = i.getRecording();
		if (rec != null) {
			link = urlForPage(HashPage.class, new PageParameters().add(INVITATION_HASH, i.getHash()), baseUrl);
		}
		return link;
	}

	@Override
	public String getOmInvitationLink(Invitation i) { //FIXME hack for email templates support (should be in separate module for now
		return getInvitationLink(i, null);
	}

	public static String urlForPage(Class<? extends Page> clazz, PageParameters pp, String _baseUrl) {
		RequestCycle rc = RequestCycle.get();
		String baseUrl = getBean(ConfigurationDao.class).getBaseUrl();
		if (!new UrlValidator(new String[] {"http", "https"}).isValid(baseUrl) && !Strings.isEmpty(_baseUrl)) {
			baseUrl = _baseUrl;
		}
		return rc.getUrlRenderer().renderFullUrl(Url.parse(baseUrl + rc.urlFor(clazz, pp)));
	}

	@Override
	public String urlForActivatePage(PageParameters pp) { //FIXME hack for email templates support (should be in separate module for now
		return urlForPage(ActivatePage.class, pp, null);
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

	@Override
	public String getOmString(String key, final Locale loc, String... params) {
		return getString(key, loc, params);
	}

	@Override
	public Client getOmClient(String uid) {
		return getOnlineClient(uid);
	}

	@Override
	public void setXFrameOptions(String xFrameOptions) {
		this.xFrameOptions = xFrameOptions;
	}

	@Override
	public void setContentSecurityPolicy(String contentSecurityPolicy) {
		this.contentSecurityPolicy = contentSecurityPolicy;
	}

	@Override
	public String getServerId() {
		return hazelcast.getName();
	}

	public List<Member> getServers() {
		return new ArrayList<>(hazelcast.getCluster().getMembers());
	}

	@Override
	public void updateJpaAddresses(ConfigurationDao dao) {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for (Member m : hazelcast.getCluster().getMembers()) {
			sb.append(delim).append(m.getAddress().getHost());
			delim = ";";
		}
		if (Strings.isEmpty(delim)) {
			sb.append("localhost");
		}
		try {
			dao.updateClusterAddresses(sb.toString());
		} catch (UnknownHostException e) {
			log.error("Uexpected exception while updating JPA addresses", e);
			throw new WicketRuntimeException(e);
		}
	}

	@Override
	public void publishWsTopic(IClusterWsMessage msg) {
		hazelWsTopic.publish(msg);
	}
}
