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
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.openmeetings.core.IApplication;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
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
import org.apache.openmeetings.web.util.AviRecordingResourceReference;
import org.apache.openmeetings.web.util.FlvRecordingResourceReference;
import org.apache.openmeetings.web.util.JpgRecordingResourceReference;
import org.apache.openmeetings.web.util.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.OggRecordingResourceReference;
import org.apache.openmeetings.web.util.UserDashboardPersister;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.settings.PageSettings;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.util.tester.WicketTester;
import org.wicketstuff.select2.ApplicationSettings;
import org.slf4j.Logger;
import org.springframework.web.context.support.XmlWebApplicationContext;

import ro.fortsoft.wicket.dashboard.WidgetRegistry;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextInjector;
import ro.fortsoft.wicket.dashboard.web.DashboardSettings;

public class Application extends AuthenticatedWebApplication implements IApplication {
	private static final Logger log = getLogger(Application.class, webAppRootKey);
	private static boolean isInstalled;
	private static Map<Long, Set<Client>> ONLINE_USERS = new ConcurrentHashMap<Long, Set<Client>>();
	private static Map<Long, Set<Client>> ROOMS = new ConcurrentHashMap<Long, Set<Client>>();
	//additional maps for faster searching should be created
	private DashboardContext dashboardContext;
	
	@Override
	protected void init() {
		wicketApplicationName = super.getName();
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy());
		PageSettings pageSettings = getPageSettings();
		pageSettings.addComponentResolver(new MessageResolver());
		pageSettings.addComponentResolver(new MessageTagHandler());
		getMarkupSettings().setMarkupFactory(new MarkupFactory(){
			@Override
			public MarkupParser newMarkupParser(MarkupResourceStream resource) {
				MarkupParser mp = super.newMarkupParser(resource);
				mp.add(new MessageTagHandler());
				return mp;
			}
		});
		
		//Add custom resource loader at the beginning, so it will be checked first in the 
		//chain of Resource Loaders, if not found it will search in Wicket's internal 
		//Resource Loader for a the property key
		getResourceSettings().getStringResourceLoaders().add(0, new LabelResourceLoader());
		
		
		ApplicationSettings.get().setIncludeJqueryUI(false);
		
		super.init();
		
		// register some widgets
		dashboardContext = new DashboardContext();
		dashboardContext.setDashboardPersiter(new UserDashboardPersister());
		WidgetRegistry widgetRegistry = dashboardContext.getWidgetRegistry();
		widgetRegistry.registerWidget(new MyRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new WelcomeWidgetDescriptor());
		widgetRegistry.registerWidget(new StartWidgetDescriptor());
		widgetRegistry.registerWidget(new RssWidgetDescriptor());
		// add dashboard context injector
		getComponentInstantiationListeners().add(new DashboardContextInjector(dashboardContext));
		DashboardSettings dashboardSettings = DashboardSettings.get();
		dashboardSettings.setIncludeJQuery(false);
		dashboardSettings.setIncludeJQueryUI(false);
		
		getRootRequestMapperAsCompound().add(new NoVersionMapper(getHomePage()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("notinited", NotInitedPage.class));
		mountPage("swf", SwfPage.class);
		mountPage("install", InstallWizardPage.class);
		mountPage("signin", getSignInPageClass());
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountPage("/recording/${hash}", RecordingPage.class);
		mountResource("/recordings/avi/${id}", new AviRecordingResourceReference());
		mountResource("/recordings/flv/${id}", new FlvRecordingResourceReference());
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/ogg/${id}", new OggRecordingResourceReference());
		mountResource("/recordings/jpg/${id}", new JpgRecordingResourceReference()); //should be in sync with VideoPlayer
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
	
	public static void addOnlineUser(Client client) {
		long userId = client.getUserId();
		if (!ONLINE_USERS.containsKey(userId)) {
			ONLINE_USERS.put(userId, new ConcurrentHashSet<Client>());
		}
		ONLINE_USERS.get(userId).add(client);
	}
	
	public static void removeOnlineUser(Client c) {
		long userId = c.getUserId();
		if (ONLINE_USERS.containsKey(userId)) {
			Set<Client> clients = ONLINE_USERS.get(userId);
			clients.remove(c);
			if (clients.isEmpty()) {
				ONLINE_USERS.remove(userId);
			}
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
	
	public static boolean isUserOnline(long userId) {
		return ONLINE_USERS.containsKey(userId);
	}
	
	//TODO need more safe way FIXME
	public <T> T _getBean(Class<T> clazz) {
		return getWebApplicationContext(getServletContext()).getBean(clazz);
	}
	
	public static boolean isInstalled() {
		boolean result = isInstalled;
		if (!isInstalled) {
			if (InitializationContainer.initComplete) {
				//TODO can also check crypt class here
				isInstalled = result = get()._getBean(UserDao.class).count() > 0
						&& get()._getBean(FieldLanguagesValuesDao.class).count() > 0;
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
	
	public static WicketTester getWicketTester() {
		return getWicketTester(-1);
	}
	
	public static WicketTester getWicketTester(long langId) {
		Application app = new Application();
        
		WicketTester tester = new WicketTester(app);
		ServletContext sc = app.getServletContext();
        XmlWebApplicationContext xmlContext = new XmlWebApplicationContext();
        xmlContext.setConfigLocation("classpath:openmeetings-applicationContext.xml");
        xmlContext.setServletContext(sc);
        xmlContext.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, xmlContext);
        if (langId > 0) {
        	WebSession.get().setLanguage(langId);
        }
        InitializationContainer.initComplete = true;
        return tester;
	}
	
	public static void destroy(WicketTester tester) {
		if (tester != null) {
			ServletContext sc = tester.getServletContext();
			try {
				((XmlWebApplicationContext)sc.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).close();
			} catch (Exception e) {
				log.error("Unexpected error while destroying XmlWebApplicationContext", e);
			}
			tester.destroy();
		}
	}
	
	public <T> T getOmBean(Class<T> clazz) { //FIXME hack for email templates support (should be in separate module for now
		return Application.getBean(clazz);
	}

	public static String getContactsLink() {
		return PROFILE_MESSAGES.getLink();
	}

	public String getOmContactsLink() { //FIXME hack for email templates support (should be in separate module for now
		return getContactsLink();
	}

	public static String getInvitationLink(String baseUrl, Invitation i) {
		String link = baseUrl;
		if (link == null) {
			return null;
		}
		if (i.getRoom() != null) {
			if (i.getInvitee().getType() == Type.contact) {
				link += "?invitationHash=" + i.getHash();
		
				if (i.getInvitee().getLanguage_id() > 0) {
					link += "&language=" + i.getInvitee().getLanguage_id().toString();
				}
			} else {
				link = getRoomUrlFragment(i.getRoom().getId()).getLink();
			}
		}
		return link;
	}
	
	public String getOmInvitationLink(String baseUrl, Invitation i) { //FIXME hack for email templates support (should be in separate module for now
		return getInvitationLink(baseUrl, i);
	}
	
	public static String urlForPage(Class<? extends Page> clazz, PageParameters pp) {
		RequestCycle rc = RequestCycle.get();
		return rc.getUrlRenderer().renderFullUrl(Url.parse(getBean(ConfigurationDao.class).getBaseUrl() + rc.urlFor(clazz, pp)));
	}

	public String urlForActivatePage(PageParameters pp) { //FIXME hack for email templates support (should be in separate module for now
		return urlForPage(ActivatePage.class, pp);
	}
}
