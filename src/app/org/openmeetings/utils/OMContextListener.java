package org.openmeetings.utils;

import javax.servlet.ServletContextEvent;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.ContextLoggingListener;

public class OMContextListener extends ContextLoggingListener {
	
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	public void contextInitialized(ServletContextEvent event) {
		ScopeApplicationAdapter.webAppRootKey = pathToName(event);
		ScopeApplicationAdapter.webAppRootPath = "/" + ScopeApplicationAdapter.webAppRootKey;
		System.setProperty("current_openmeetings_context_name", ScopeApplicationAdapter.webAppRootKey);
		System.setProperty("webapp.contextPath", ScopeApplicationAdapter.webAppRootPath);
		System.setProperty("logback.configurationFile", "logback-config.xml");
		super.contextInitialized(event);
	}

	private String pathToName(ServletContextEvent event) {
		String contextName = event.getServletContext().getContextPath().replaceAll("/", "");
		if ("".equals(contextName)) {
			contextName = "root";
		}
		return contextName;
	}
}
