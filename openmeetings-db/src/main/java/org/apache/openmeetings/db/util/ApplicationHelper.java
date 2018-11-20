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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.red5.logging.Red5LoggerFactory.getLogger;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

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
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ApplicationHelper {
	private static final Logger log = getLogger(ApplicationHelper.class, getWebAppRootKey());
	private static final Object SYNC_OBJ = new Object();

	private ApplicationHelper() {}

	public static IApplication ensureApplication() {
		return ensureApplication(-1L);
	}

	public static IApplication _ensureApplication() {
		if (Application.exists()) {
			return (IApplication)Application.get();
		}
		synchronized (SYNC_OBJ) {
			if (Application.exists()) {
				return (IApplication)Application.get();
			}
			WebApplication app = (WebApplication)Application.get(getWicketApplicationName());
			LabelDao.initLanguageMap();
			if (app == null) {
				try {
					app = (WebApplication)LabelDao.getAppClass().newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					log.error("Failed to create Application");
					return null;
				}
				app.setServletContext(new MockServletContext(app, null));
				app.setName(getWicketApplicationName());
				ServletContext sc = app.getServletContext();
				OMContextListener omcl = new OMContextListener();
				omcl.contextInitialized(new ServletContextEvent(sc));
				XmlWebApplicationContext xmlContext = new XmlWebApplicationContext();
				xmlContext.setConfigLocation("classpath:applicationContext.xml");
				xmlContext.setServletContext(sc);
				xmlContext.refresh();
				sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, xmlContext);
				app.setConfigurationType(RuntimeConfigurationType.DEPLOYMENT);
				ThreadContext.setApplication(app);
				app.initApplication();
			} else {
				ThreadContext.setApplication(app);
			}
			return (IApplication)Application.get(getWicketApplicationName());
		}
	}

	public static IApplication ensureApplication(Long langId) {
		IApplication a = _ensureApplication();
		if (ThreadContext.getRequestCycle() == null) {
			ServletWebRequest req = new ServletWebRequest(new MockHttpServletRequest((Application)a, new MockHttpSession(a.getServletContext()), a.getServletContext()), "");
			RequestCycleContext rctx = new RequestCycleContext(req, new MockWebResponse(), a.getRootRequestMapper(), a.getExceptionMapperProvider().get());
			ThreadContext.setRequestCycle(new RequestCycle(rctx));
		}
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
		app.internalDestroy(); //need to be called to
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
