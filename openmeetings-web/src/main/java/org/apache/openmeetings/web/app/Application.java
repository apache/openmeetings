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

import static org.apache.openmeetings.web.pages.HashPage.INVITATION_HASH;
import static org.apache.openmeetings.web.user.rooms.RoomEnterBehavior.getRoomUrlFragment;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getTheme;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.wicketstuff.dashboard.DashboardContextInitializer.DASHBOARD_CONTEXT_KEY;

import java.io.File;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;
import jakarta.websocket.WebSocketContainer;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.sip.SipManager;
import org.apache.openmeetings.core.util.ChatWebSocketHelper;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.ApplicationHelper;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.openmeetings.web.admin.backup.BackupUploadResourceReference;
import org.apache.openmeetings.web.common.PingResourceReference;
import org.apache.openmeetings.web.pages.AccessDeniedPage;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.pages.InternalErrorPage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.PrivacyPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.room.GroupCustomCssResourceReference;
import org.apache.openmeetings.web.room.RoomPreviewResourceReference;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.openmeetings.web.room.sidebar.RoomFileUploadResourceReference;
import org.apache.openmeetings.web.room.wb.WbWebSocketHelper;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RecentRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.admin.AdminWidgetDescriptor;
import org.apache.openmeetings.web.user.record.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.user.record.PngRecordingResourceReference;
import org.apache.openmeetings.web.util.GroupLogoResourceReference;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OmVersion;
import org.apache.openmeetings.web.util.ProfileImageResourceReference;
import org.apache.openmeetings.web.util.UserDashboardPersister;
import org.apache.wicket.DefaultPageManagerProvider;
import org.apache.wicket.Localizer;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerRequestHandler;
import org.apache.wicket.core.request.handler.ListenerRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.pageStore.IPageStore;
import org.apache.wicket.pageStore.SerializingPageStore;
import org.apache.wicket.protocol.ws.WebSocketAwareResourceIsolationRequestCycleListener;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.UrlValidator;
import org.apache.openmeetings.mediaserver.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.wicketstuff.dashboard.WidgetRegistry;
import org.wicketstuff.dashboard.web.DashboardContext;
import org.wicketstuff.dashboard.web.DashboardSettings;
import org.wicketstuff.datastores.hazelcast.HazelcastDataStore;

import org.wicketstuff.jquery.ui.plugins.wysiwyg.settings.WysiwygLibrarySettings;
import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.NoopThemeProvider;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider;
import jakarta.inject.Inject;

@Component
public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	private static boolean isInstalled;
	private static final String INVALID_SESSIONS_KEY = "INVALID_SESSIONS_KEY";
	private static final String SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE = "jakarta.websocket.server.ServerContainer";
	public static final String NAME_ATTR_KEY = "name";
	public static final String SERVER_URL_ATTR_KEY = "server.url";
	//additional maps for faster searching should be created
	static final Set<String> STRINGS_WITH_APP = Set.of("500", "506", "511", "512", "513", "517", "widget.start.desc"
			, "1151", "1155", "1157", "1158", "1194"); // package private for testing
	private static String appName;
	public static final String HASH_MAPPING = "/hash";
	public static final String SIGNIN_MAPPING = "/signin";
	public static final String NOTINIT_MAPPING = "/notinited";
	HazelcastInstance hazelcast;
	private ITopic<IClusterWsMessage> hazelWsTopic;
	private String serverId;
	private final Set<String> wsUrls = new HashSet<>();

	@Inject
	private ApplicationContext ctx;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private RecordingDao recordingDao;
	@Inject
	private UserDao userDao;
	@Inject
	private UserManager userManager;
	@Inject
	private ClientManager cm;
	@Inject
	private WhiteboardManager wbManager;
	@Inject
	private AppointmentDao appointmentDao;
	@Inject
	private SipManager sipManager;
	@Value("${remember.me.encryption.key}")
	private String rememberMeKey;
	@Value("${remember.me.encryption.salt}")
	private String rememberMeSalt;

	@Override
	protected void init() {
		setWicketApplicationName(super.getName());
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy(rememberMeKey, rememberMeSalt));
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
		getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));

		Config cfg = new XmlConfigBuilder().build();
		cfg.setClassLoader(getClass().getClassLoader());
		hazelcast = Hazelcast.getOrCreateHazelcastInstance(cfg);
		serverId = hazelcast.getName();
		hazelcast.getCluster().getMembers().forEach(m -> cm.serverAdded(m.getAttribute(NAME_ATTR_KEY), m.getAttribute(SERVER_URL_ATTR_KEY)));
		hazelWsTopic = hazelcast.getTopic("default");
		hazelWsTopic.addMessageListener(msg -> {
			String mServerId = msg.getPublishingMember().getAttribute(NAME_ATTR_KEY);
			if (mServerId.equals(serverId)) {
				return;
			}
			IClusterWsMessage wsMsg = msg.getMessageObject();
			if (WbWebSocketHelper.send(wsMsg)) {
				return;
			}
			if (ChatWebSocketHelper.send(msg.getMessageObject())) {
				return;
			}
			WebSocketHelper.send(msg.getMessageObject());
		});
		hazelcast.getCluster().addMembershipListener(new MembershipListener() {
			@Override
			public void memberRemoved(MembershipEvent evt) {
				//server down, need to remove all online clients, process persistent addresses
				String downId = evt.getMember().getAttribute(NAME_ATTR_KEY);
				cm.serverRemoved(downId);
				updateJpaAddresses();
			}

			@Override
			public void memberAdded(MembershipEvent evt) {
				//no-op
				//server added, need to process persistent addresses
				updateJpaAddresses();
				//check for duplicate instance-names
				Set<String> names = new HashSet<>();
				for (Member m : evt.getMembers()) {
					if (evt.getMember().getUuid().equals(m.getUuid())) {
						continue;
					}
					String duplicateId = m.getAttribute(NAME_ATTR_KEY);
					names.add(duplicateId);
				}
				String newServerId = evt.getMember().getAttribute(NAME_ATTR_KEY);
				log.warn("Name added: {}", newServerId);
				cm.serverAdded(newServerId, evt.getMember().getAttribute(SERVER_URL_ATTR_KEY));
				if (names.contains(newServerId)) {
					log.warn("Duplicate cluster instance with name {} found {}", newServerId, evt.getMember());
				}
			}
		});
		setPageManagerProvider(new DefaultPageManagerProvider(this) {
			@Override
			protected IPageStore newAsynchronousStore(IPageStore pageStore) {
				return new SerializingPageStore(
						new HazelcastDataStore(getName(), hazelcast)
						, getFrameworkSettings().getSerializer());
			}
		});
		//Add custom resource loader at the beginning, so it will be checked first in the
		//chain of Resource Loaders, if not found it will search in Wicket's internal
		//Resource Loader for a the property key
		getResourceSettings().getStringResourceLoaders().add(0, new LabelResourceLoader());
		getRequestCycleListeners().add(new WebSocketAwareResourceIsolationRequestCycleListener() {
			@Override
			public void onBeginRequest(RequestCycle cycle) {
				String wsUrl = getWsUrl(cycle.getRequest().getUrl());
				if (wsUrl != null && !wsUrls.contains(wsUrl)) {
					wsUrls.add(wsUrl);
					cfgDao.updateCsp();
				}
			}

			@Override
			public void onEndRequest(RequestCycle cycle) {
				Response resp = cycle.getResponse();
				if (resp instanceof WebResponse wresp && wresp.isHeaderSupported()) {
					wresp.setHeader("X-XSS-Protection", "1; mode=block");
					wresp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
					wresp.setHeader("X-Content-Type-Options", "nosniff");
				}
			}
		});
		final WebSocketContainer sc = (WebSocketContainer)getServletContext().getAttribute(SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
		if (sc != null) {
			sc.setDefaultMaxSessionIdleTimeout(60 * 1000L); // should be enough, should it be configurable?
		}
		getHeaderResponseDecorators().add(FilteringHeaderResponse::new);
		super.init();
		final IBootstrapSettings settings = new BootstrapSettings().setAutoAppendResources(false);
		Bootstrap.builder().withBootstrapSettings(settings).install(this);
		WysiwygLibrarySettings.get().setBootstrapCssReference(null);
		WysiwygLibrarySettings.get().setBootstrapDropDownJavaScriptReference(null);
		WysiwygLibrarySettings.get().setPrettifyJavaScriptReference(null);

		// register some widgets
		final DashboardContext dashboardContext = getDashboardContext();
		dashboardContext.setDashboardPersister(new UserDashboardPersister());
		WidgetRegistry widgetRegistry = dashboardContext.getWidgetRegistry();
		widgetRegistry.registerWidget(new MyRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new RecentRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new WelcomeWidgetDescriptor());
		widgetRegistry.registerWidget(new StartWidgetDescriptor());
		widgetRegistry.registerWidget(new RssWidgetDescriptor());
		widgetRegistry.registerWidget(new AdminWidgetDescriptor());
		DashboardSettings dashboardSettings = DashboardSettings.get();
		dashboardSettings.setIncludeJQueryUI(false);

		getRootRequestMapperAsCompound().add(new NoVersionMapper(getHomePage()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(NOTINIT_MAPPING, NotInitedPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("denied", AccessDeniedPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(HASH_MAPPING, HashPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(SIGNIN_MAPPING, getSignInPageClass()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("oauth/${oauthid}", getSignInPageClass()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("privacy", PrivacyPage.class));
		mountPage("install", InstallWizardPage.class);
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountPage("error", InternalErrorPage.class);
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/png/${id}", new PngRecordingResourceReference()); //should be in sync with VideoPlayer
		mountResource("/room/file/${id}", new RoomResourceReference());
		mountResource("/room/preview/${id}", new RoomPreviewResourceReference());
		mountResource("/room/file/upload", new RoomFileUploadResourceReference());
		mountResource("/admin/backup/upload", new BackupUploadResourceReference());
		mountResource("/profile/${id}", new ProfileImageResourceReference());
		mountResource("/group/${id}", new GroupLogoResourceReference());
		mountResource("/group/customcss/${id}", new GroupCustomCssResourceReference());
		mountResource("/ping", new PingResourceReference());

		log.debug("Application::init");
		try {
			if (OmFileHelper.getOmHome() == null) {
				OmFileHelper.setOmHome(new File(getServletContext().getRealPath("/")));
			}
			LabelDao.initLanguageMap();

			log.debug("webAppPath : {}", OmFileHelper.getOmHome());

			// Init all global config properties
			cfgDao.reinit();
			wbManager.init();
			cm.init();

			// Init properties
			updateJpaAddresses();
			setExtProcessTtl(cfgDao.getInt(CONFIG_EXT_PROCESS_TTL, getExtProcessTtl()));
			OmVersion.logOMStarted();
			recordingDao.resetProcessingStatus(); //we are starting so all processing recordings are now errors
			userManager.initHttpClient();
			setInitComplete(true);
			CompletableFuture.runAsync(() -> {
				ThreadContext.setApplication(Application.this);
				ApplicationHelper.ensureRequestCycle(Application.this);
				sipManager.setUserPicture(u -> ProfileImageResourceReference.getUrl(RequestCycle.get(), u));
			});
		} catch (Exception err) {
			log.error("[appStart]", err);
		}
		warmup();
	}

	private void warmup() {
		// warm-up Inject
		Client c = new Client(null, 1, null, null);
		KStream stream = null;
		try {
			stream = new KStream(c.addStream(Client.StreamType.WEBCAM, Activity.AUDIO_VIDEO), null);
		} finally {
			if (stream != null) {
				stream.release();
			}
		}
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
		return (Application)org.apache.wicket.Application.get(getWicketApplicationName());
	}

	public static DashboardContext getDashboardContext() {
		return get().getMetaData(DASHBOARD_CONTEXT_KEY);
	}

	//package private
	static HazelcastInstance getHazelcast() {
		return get().hazelcast;
	}

	//package private
	Map<String, String> getInvalidSessions() {
		return hazelcast.getMap(INVALID_SESSIONS_KEY);
	}

	public static void kickUser(Client client) {
		if (client != null) {
			WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoom().getId(), client, RoomMessage.Type.KICK, client.getUid()));
		}
	}

	public static boolean isInvaldSession(String sessionId) {
		return sessionId != null && get().getInvalidSessions().containsKey(sessionId);
	}

	public static void removeInvalidSession(String sessionId) {
		if (sessionId != null){
			get().getInvalidSessions().remove(sessionId);
		}
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		return ctx == null ? null : ctx.getBean(clazz);
	}

	public static String getString(String id) {
		return getString(id, WebSession.getLanguage());
	}

	public static Locale getLocale(final long languageId) {
		Locale loc = LabelDao.getLocale(languageId);
		if (loc == null) {
			loc = Session.exists() ? WebSession.get().getLocale() : Locale.ENGLISH;
		}
		return loc;
	}

	public static String getString(String key, final long languageId) {
		return getString(key, getLocale(languageId));
	}

	public static String getString(String key, final Locale loc, String... inParams) {
		if (!exists()) {
			ThreadContext.setApplication(org.apache.wicket.Application.get(appName));
		}
		String[] params = inParams;
		if ((params == null || params.length == 0) && STRINGS_WITH_APP.contains(key)) {
			params = new String[]{getApplicationName()};
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
		if (!isInstalled && isInitComplete()) {
			isInstalled = result = get().userDao.count() > 0;
		}
		return result;
	}

	public static String getContactsLink() {
		return PROFILE_MESSAGES.getLink();
	}

	@Override
	public String getOmContactsLink() {
		return getContactsLink();
	}

	@Override
	public String getOmInvitationLink(Invitation i, String baseUrl) {
		return getInvitationLink(i, baseUrl);
	}

	@Override
	public String urlForActivatePage(PageParameters pp) {
		return urlForPage(ActivatePage.class, pp, null);
	}
	//END hack for email templates support (should be in separate module for now

	public static String getInvitationLink(Invitation i, String baseUrl) {
		String link = "";
		Room r = i.getRoom();
		User u = i.getInvitee();
		if (r != null) {
			if ((i.isPasswordProtected() && !r.isOwner(u.getId())) // invitation is password-protected and invitee is not owner
					|| Type.CONTACT == u.getType() || Type.EXTERNAL == u.getType() || !get().isRoomAllowedToUser(r, u)) // no-access
			{
				PageParameters pp = new PageParameters();
				pp.add(INVITATION_HASH, i.getHash());
				if (u.getLanguageId() > 0) {
					pp.add("language", u.getLanguageId());
				}
				link = urlForPage(HashPage.class, pp, baseUrl);
			} else {
				link = getRoomUrlFragment(r.getId()).getLink();
			}
		}
		Recording rec = i.getRecording();
		if (rec != null) {
			link = urlForPage(HashPage.class, new PageParameters().add(INVITATION_HASH, i.getHash()), baseUrl);
		}
		return link;
	}

	private static boolean checkAppointment(Appointment a, User u) {
		if (a == null || a.isDeleted()) {
			return false;
		}
		if (a.isOwner(u.getId())) {
			log.debug("[isRoomAllowedToUser] appointed room, Owner entered");
			return true;
		}
		return a.getMeetingMembers().stream()
				.map(MeetingMember::getUser)
				.map(User::getId)
				.anyMatch(userId -> userId.equals(u.getId()));
	}

	private static boolean checkGroups(Room r, User u) {
		if (null == r.getGroups()) { //u.getGroupUsers() can't be null due to user was able to login
			return false;
		}
		Set<Long> roomGroups = r.getGroups().stream()
				.map(RoomGroup::getGroup)
				.map(Group::getId)
				.collect(Collectors.toSet());
		return u.getGroupUsers().stream()
				.map(GroupUser::getGroup)
				.map(Group::getId)
				.anyMatch(roomGroups::contains);
	}

	public boolean isRoomAllowedToUser(Room r, User u) {
		if (r == null) {
			return false;
		}
		if (r.isAppointment()) {
			Appointment a = appointmentDao.getByRoom(r.getId());
			return checkAppointment(a, u);
		}
		if (r.getIspublic() || r.isOwner(u.getId())) {
			log.debug("[isRoomAllowedToUser] public ? {} , ownedId ? {} ALLOWED", r.getIspublic(), r.getOwnerId());
			return true;
		}
		return checkGroups(r, u);
	}

	public static boolean isUrlValid(String url) {
		return new UrlValidator(new String[] {"http", "https"}).isValid(url);
	}

	private static String getValidBaseUrl(String inBaseUrl) {
		if (isUrlValid(inBaseUrl)) {
			return inBaseUrl;
		}
		return isUrlValid(getBaseUrl()) ? getBaseUrl() : "";
	}

	public static String urlForPage(Class<? extends Page> clazz, PageParameters pp, String inBaseUrl) {
		RequestCycle rc = RequestCycle.get();
		String baseUrl = getValidBaseUrl(inBaseUrl);
		if (!Strings.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		return rc.getUrlRenderer().renderFullUrl(Url.parse(baseUrl + rc.mapUrlFor(clazz, pp)));
	}

	@Override
	public String getOmString(String key, long languageId) {
		return getString(key, languageId);
	}

	@Override
	public String getOmString(String key) {
		return getString(key);
	}

	@Override
	public String getOmString(String key, final Locale loc, String... params) {
		return getString(key, loc, params);
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public void updateJpaAddresses() {
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
			cfgDao.updateClusterAddresses(sb.toString());
		} catch (UnknownHostException e) {
			log.error("Uexpected exception while updating JPA addresses", e);
			throw new WicketRuntimeException(e);
		}
	}

	@Override
	public void publishWsTopic(@Nonnull IClusterWsMessage msg) {
		hazelWsTopic.publish(msg);
	}

	@Override
	public Set<String> getWsUrls() {
		return Set.copyOf(wsUrls);
	}

	@Override
	public void updateTheme() {
		BootswatchTheme theme = Stream.of(BootswatchTheme.values())
				.filter(v -> v.name().equalsIgnoreCase(getTheme()))
				.findFirst()
				.orElse(null);
		IBootstrapSettings settings = Bootstrap.getSettings(this);
		settings.setThemeProvider(theme == null ? new NoopThemeProvider()
				: new BootswatchThemeProvider(theme));
		if (Session.exists()) {
			settings.getActiveThemeProvider().setActiveTheme(theme == null
					? settings.getThemeProvider().defaultTheme()
					: settings.getThemeProvider().byName(theme.name()));
		}
	}

	// package private for testing
	static String getWsUrl(Url reqUrl) {
		if (!reqUrl.isFull()) {
			return null;
		}
		final boolean insecure = "http".equalsIgnoreCase(reqUrl.getProtocol());
		String delim = ":";
		String port;
		if (reqUrl.getPort() == null || reqUrl.getPort() < 0
				|| (insecure && 80 == reqUrl.getPort())
				|| (!insecure && 443 == reqUrl.getPort()))
		{
			port = "";
			delim = "";
		} else {
			port = String.valueOf(reqUrl.getPort());
		}
		String url = (insecure ? "ws" : "wss") + "://" + reqUrl.getHost() + delim + port;
		if (log.isTraceEnabled()) {
			log.trace("Getting WS url from '{}', result: '{}'", reqUrl.toString(StringMode.FULL), url);
		}
		return url;
	}
}
