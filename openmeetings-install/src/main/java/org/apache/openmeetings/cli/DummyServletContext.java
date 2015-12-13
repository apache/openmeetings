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
package org.apache.openmeetings.cli;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class DummyServletContext implements ServletContext {
	private String ctx;
	
	public DummyServletContext(String ctx) {
		this.ctx = ctx;
	}

	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		// stab
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		// stab
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		// stab
		return null;
	}

	@Override
	public void addListener(String arg0) {
		// stab
	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {
		// stab
	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		// stab
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, String arg1) {
		// stab
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Servlet arg1) {
		// stab
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Class<? extends Servlet> arg1) {
		// stab
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0) throws ServletException {
		// stab
		return null;
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0) throws ServletException {
		// stab
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0) throws ServletException {
		// stab
		return null;
	}

	@Override
	public void declareRoles(String... arg0) {
		// stab
	}

	@Override
	public Object getAttribute(String arg0) {
		// stab
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// stab
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		// stab
		return null;
	}

	@Override
	public ServletContext getContext(String arg0) {
		// stab
		return null;
	}

	@Override
	public String getContextPath() {
		return ctx;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// stab
		return null;
	}

	@Override
	public int getEffectiveMajorVersion() {
		// stab
		return 0;
	}

	@Override
	public int getEffectiveMinorVersion() {
		// stab
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// stab
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		// stab
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// stab
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {
		// stab
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		// stab
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// stab
		return null;
	}

	@Override
	public int getMajorVersion() {
		// stab
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		// stab
		return null;
	}

	@Override
	public int getMinorVersion() {
		// stab
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		// stab
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// stab
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// stab
		return null;
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		// stab
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		// stab
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		// stab
		return null;
	}

	@Override
	public String getServerInfo() {
		// stab
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		// stab
		return null;
	}

	@Override
	public String getServletContextName() {
		// stab
		return null;
	}

	@Override
	public Enumeration<String> getServletNames() {
		// stab
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		// stab
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// stab
		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		// stab
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		// stab
		return null;
	}

	@Override
	public String getVirtualServerName() {
		// stab
		return null;
	}

	@Override
	public void log(String arg0) {
		// stab
	}

	@Override
	public void log(Exception arg0, String arg1) {
		// stab
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		// stab
	}

	@Override
	public void removeAttribute(String arg0) {
		// stab
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// stab
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		// stab
		return false;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		// stab
	}
}
