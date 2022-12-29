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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getAppClass;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.IWebSession;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.util.OMContextListener;
import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ApplicationHelper {
	private static final Logger log = LoggerFactory.getLogger(ApplicationHelper.class);
	private static final Object SYNC_OBJ = new Object();

	private ApplicationHelper() {}

	private static WebApplication createApp(WebApplication inApp) {
		WebApplication app = inApp;
		if (app == null) {
			// This is the case for non-web-app applications (command line admin)
			try {
				app = (WebApplication)getAppClass().getDeclaredConstructor().newInstance();
				app.setName(String.format("--%s--", UUID.randomUUID())); //temporary name for temporary application
				final ServletContext sc = new MockServletContext(app, null);
				XmlWebApplicationContext xmlContext = new XmlWebApplicationContext();
				xmlContext.setConfigLocation("classpath:applicationContext.xml");
				xmlContext.setServletContext(sc);
				xmlContext.refresh();
				sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, xmlContext);
				app = xmlContext.getBean(WebApplication.class);
				app.setName(getWicketApplicationName());
				app.setServletContext(sc);
				if (app.getWicketFilter() == null) {
					final FilterConfig filterConfig = new FilterConfig() {
						private final Map<String, String> initParameters = Map.of(WicketFilter.FILTER_MAPPING_PARAM, "/servlet/*");

						@Override
						public String getFilterName() {
							return getClass().getName();
						}

						@Override
						public ServletContext getServletContext() {
							return sc;
						}

						@Override
						public String getInitParameter(String s) {
							return initParameters.get(s);
						}

						@Override
						public Enumeration<String> getInitParameterNames() {
							throw new UnsupportedOperationException("Not implemented");
						}
					};
					WicketFilter filter = new WicketFilter() {
						@Override
						public FilterConfig getFilterConfig() {
							return filterConfig;
						}
					};
					app.setWicketFilter(filter);
				}
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
				log.error("Failed to create Application");
			}
		}
		return app;
	}

	private static WebApplication initApp(WebApplication app) {
		if (app != null) {
			try {
				app.getServletContext();
			} catch(IllegalStateException e) {
				app.setServletContext(new MockServletContext(app, null));
			}
			try {
				app.setConfigurationType(RuntimeConfigurationType.DEPLOYMENT);
			} catch (IllegalStateException e) {
				// no-op, might be already set
			}
			OMContextListener omcl = new OMContextListener();
			omcl.contextInitialized(new ServletContextEvent(app.getServletContext()));
			ThreadContext.setApplication(app);
			app.initApplication();
		}
		return app;
	}

	public static IApplication ensureApplication() {
		if (Application.exists()) {
			return (IApplication)Application.get();
		}
		synchronized (SYNC_OBJ) {
			if (Application.exists()) {
				return (IApplication)Application.get();
			}
			WebApplication app = createApp((WebApplication)Application.get(getWicketApplicationName()));
			LabelDao.initLanguageMap();
			if (app != null) {
				if (!isInitComplete()) {
					initApp(app);
				}
				ThreadContext.setApplication(app);
			}
			return (IApplication)Application.get(getWicketApplicationName());
		}
	}

	public static void ensureRequestCycle(IApplication a) {
		if (ThreadContext.getRequestCycle() == null) {
			ServletWebRequest req = new ServletWebRequest(new MockHttpServletRequest((Application)a, new MockHttpSession(a.getServletContext()), a.getServletContext()), "");
			RequestCycleContext rctx = new RequestCycleContext(req, new MockWebResponse(), a.getRootRequestMapper(), a.getExceptionMapperProvider().get());
			ThreadContext.setRequestCycle(new RequestCycle(rctx));
		}
	}

	public static IApplication ensureApplication(Long langId) {
		IApplication a = ensureApplication();
		ensureRequestCycle(a);
		if (ThreadContext.getSession() == null) {
			WebSession s = WebSession.get();
			if (langId > 0) {
				((IWebSession)s).setLanguage(langId);
			}
		}
		return a;
	}

	public static void destroyApplication() {
		WebApplication app = (WebApplication)Application.get(getWicketApplicationName());
		WebApplicationContext ctx = getWebApplicationContext(app.getServletContext());
		app.internalDestroy(); //need to be called too
		if (ctx != null) {
			((XmlWebApplicationContext)ctx).close();
		}
		ThreadContext.setApplication(null);
		ThreadContext.setRequestCycle(null);
		ThreadContext.setSession(null);
	}

	public static WebApplicationContext getApplicationContext(Long langId) {
		return getWebApplicationContext(((WebApplication)ensureApplication(langId)).getServletContext());
	}
}
