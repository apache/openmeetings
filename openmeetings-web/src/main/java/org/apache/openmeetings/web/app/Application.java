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

import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_XFRAME_SAMEORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
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
import java.util.Set;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.service.MainService;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.ws.IClusterWsMessage;
import org.apache.openmeetings.web.pages.AccessDeniedPage;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.PrivacyPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.room.RoomPreviewResourceReference;
import org.apache.openmeetings.web.room.RoomResourceReference;
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
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.UrlValidator;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
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

@Component
public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = getLogger(Application.class, getWebAppRootKey());
	private static boolean isInstalled;
	private static final String INVALID_SESSIONS_KEY = "INVALID_SESSIONS_KEY";
	public static final String NAME_ATTR_KEY = "name";
	//additional maps for faster searching should be created
	private DashboardContext dashboardContext;
	private static final Set<String> STRINGS_WITH_APP = new HashSet<>();
	private static String appName;
	static {
		STRINGS_WITH_APP.addAll(Arrays.asList("499", "500", "506", "511", "512", "513", "517", "532", "622", "widget.start.desc"
				, "909", "952", "978", "981", "984", "989", "990", "999", "1151", "1155", "1157", "1158", "1194"));
	}
	public static final String HASH_MAPPING = "/hash";
	public static final String SIGNIN_MAPPING = "/signin";
	public static final String NOTINIT_MAPPING = "/notinited";
	final HazelcastInstance hazelcast = Hazelcast.getOrCreateHazelcastInstance(new XmlConfigBuilder().build());
	private String xFrameOptions = HEADER_XFRAME_SAMEORIGIN;
	private String contentSecurityPolicy = OpenmeetingsVariables.HEADER_CSP_SELF;
	private ITopic<IClusterWsMessage> hazelWsTopic;

	@Override
	protected void init() {
		setWicketApplicationName(super.getName());
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy());
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);

		hazelcast.getCluster().getLocalMember().setStringAttribute(NAME_ATTR_KEY, hazelcast.getName());
		hazelWsTopic = hazelcast.getTopic("default");
		hazelWsTopic.addMessageListener(msg -> {
			String serverId = msg.getPublishingMember().getStringAttribute(NAME_ATTR_KEY);
			if (serverId.equals(hazelcast.getName())) {
				return;
			}
			WbWebSocketHelper.send(msg.getMessageObject());
		});
		hazelcast.getCluster().addMembershipListener(new MembershipListener() {
			@Override
			public void memberRemoved(MembershipEvent evt) {
				//server down, need to remove all online clients, process persistent addresses
				String serverId = evt.getMember().getStringAttribute(NAME_ATTR_KEY);
				getBean(ClientManager.class).clean(serverId);
				getBean(StreamClientManager.class).clean(serverId);
				updateJpaAddresses(_getBean(ConfigurationDao.class));
			}

			@Override
			public void memberAttributeChanged(MemberAttributeEvent evt) {
				//no-op
			}

			@Override
			public void memberAdded(MembershipEvent evt) {
				//server added, need to process persistent addresses
				updateJpaAddresses(_getBean(ConfigurationDao.class));
				//check for duplicate instance-names
				Set<String> names = new HashSet<>();
				for (Member m : evt.getMembers()) {
					if (evt.getMember().getUuid().equals(m.getUuid())) {
						continue;
					}
					String serverId = m.getStringAttribute(NAME_ATTR_KEY);
					names.add(serverId);
				}
				String serverId = evt.getMember().getStringAttribute(NAME_ATTR_KEY);
				if (names.contains(serverId)) {
					log.warn("Duplicate cluster instance with name {} found {}", serverId, evt.getMember());
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
					Url reqUrl = cycle.getRequest().getUrl();
					wresp.setHeader("Content-Security-Policy"
							, String.format("%s; connect-src 'self' %s;", contentSecurityPolicy, getWsUrl(reqUrl)));
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
		getRootRequestMapperAsCompound().add(new NoVersionMapper("denied", AccessDeniedPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(HASH_MAPPING, HashPage.class));
		getRootRequestMapperAsCompound().add(new NoVersionMapper(SIGNIN_MAPPING, getSignInPageClass()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("oauth/${oauthid}", getSignInPageClass()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("privacy", PrivacyPage.class));
		mountPage("install", InstallWizardPage.class);
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/png/${id}", new PngRecordingResourceReference()); //should be in sync with VideoPlayer
		mountResource("/room/file/${id}", new RoomResourceReference());
		mountResource("/room/preview/${id}", new RoomPreviewResourceReference());
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
		return (Application)org.apache.wicket.Application.get(getWicketApplicationName());
	}

	public static DashboardContext getDashboardContext() {
		return get().dashboardContext;
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
		WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoom().getId(), client, RoomMessage.Type.kick, client.getUid()));
	}

	public static boolean isInvaldSession(String sessionId) {
		return sessionId == null ? false : get().getInvalidSessions().containsKey(sessionId);
	}

	public static void removeInvalidSession(String sessionId) {
		if (sessionId != null){
			get().getInvalidSessions().remove(sessionId);
		}
	}

	public <T> T _getBean(Class<T> clazz) {
		WebApplicationContext wac = getWebApplicationContext(getServletContext());
		return wac == null ? null : wac.getBean(clazz);
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

	public static String getString(String key, final Locale loc, String... _params) {
		if (!exists()) {
			ThreadContext.setApplication(org.apache.wicket.Application.get(appName));
		}
		String[] params = _params;
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
			isInstalled = result = get()._getBean(UserDao.class).count() > 0;
		}
		return result;
	}

	public static <T> T getBean(Class<T> clazz) {
		if (isInitComplete()) {
			if (!isInstalled()) {
				throw new RestartResponseException(InstallWizardPage.class);
			}
			return get()._getBean(clazz);
		} else {
			throw new RestartResponseException(NotInitedPage.class);
		}
	}

	//BEGIN hack for email templates support (should be in separate module for now
	@Override
	public <T> T getOmBean(Class<T> clazz) {
		return Application.getBean(clazz);
	}

	@Override
	public <T> T _getOmBean(Class<T> clazz) {
		return Application.get()._getBean(clazz);
	}

	public static String getContactsLink() {
		return PROFILE_MESSAGES.getLink();
	}

	@Override
	public String getOmContactsLink() {
		return getContactsLink();
	}

	@Override
	public String getOmInvitationLink(Invitation i) {
		return getInvitationLink(i, null);
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

	public static String urlForPage(Class<? extends Page> clazz, PageParameters pp, String _baseUrl) {
		RequestCycle rc = RequestCycle.get();
		String baseUrl = getBaseUrl();
		if (!new UrlValidator(new String[] {"http", "https"}).isValid(baseUrl) && !Strings.isEmpty(_baseUrl)) {
			baseUrl = _baseUrl;
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

	private static String getWsUrl(Url reqUrl) {
		final boolean insecure = "http".equalsIgnoreCase(reqUrl.getProtocol());
		String delim = ":";
		String port = reqUrl.getPort() == null || reqUrl.getPort() < 0 ? "" : String.valueOf(reqUrl.getPort());
		if (!port.isEmpty() && ((insecure && 80 == reqUrl.getPort()) || (!insecure && 443 == reqUrl.getPort()))) {
			port = "";
		}
		if (port.isEmpty()) {
			delim = "";
		}
		return String.format("%s://%s%s%s;"
				, insecure ? "ws" : "wss"
					, reqUrl.getHost()
					, delim
					, port);
	}
}
